package it.antonino.palco.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.eftimoff.androipathview.PathView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import it.antonino.palco.PalcoApplication.Companion.file_1
import it.antonino.palco.PalcoApplication.Companion.file_2
import it.antonino.palco.PalcoApplication.Companion.file_3
import it.antonino.palco.PalcoApplication.Companion.file_4
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentEventsBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.DotsItemDecoration
import it.antonino.palco.ext.compareDate
import it.antonino.palco.ext.getNavigationBarHeight
import it.antonino.palco.ext.hasSoftwareKeys
import it.antonino.palco.ext.isActualMonth
import it.antonino.palco.ext.setAccessibility
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.CustomAdapter
import it.antonino.palco.model.CustomSnapHelper
import it.antonino.palco.util.Constant.blueColorRGB
import it.antonino.palco.util.Constant.concertoDateFormat
import it.antonino.palco.util.Constant.greenColorRGB
import it.antonino.palco.util.Constant.monthDateFormat
import it.antonino.palco.util.Constant.redColorRGB
import it.antonino.palco.viewmodel.SharedViewModel
import it.antonino.palco.workers.BatchWorker_01
import it.antonino.palco.workers.BatchWorker_02
import it.antonino.palco.workers.BatchWorker_03
import it.antonino.palco.workers.BatchWorker_04
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.toString

class EventsFragment: Fragment() {

    val viewModel: SharedViewModel by activityViewModel<SharedViewModel>()
    private var adapter: CustomAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var dotsItemDecoration: DotsItemDecoration? = null
    private lateinit var binding: FragmentEventsBinding
    private lateinit var firstBatchRequestId: UUID
    private lateinit var secondBatchRequestId: UUID
    private lateinit var thirdBatchRequestId: UUID
    private lateinit var fourthBatchRequestId: UUID
    private lateinit var richPath: PathView
    private lateinit var threeDotPath: PathView

    companion object {
        private var currentDayInstance: Calendar? = null
        private var selectedDayInstance: Date? = null
        private var canClickDays: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentEventsBinding.inflate(layoutInflater)
        currentDayInstance = Calendar.getInstance(TimeZone.getDefault())
        dotsItemDecoration = DotsItemDecoration(
            resources.getDimension(R.dimen.dp_4).toInt(),
            resources.getDimension(R.dimen.dp_6).toInt(),
            resources.getDimension(R.dimen.dp_8).toInt(),
            resources.getDimension(R.dimen.recycler_item_spacing).toInt(),
            ResourcesCompat.getColor(resources, R.color.colorWhite, null),
            ResourcesCompat.getColor(resources, R.color.colorAccent, null)
        )

        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        val prefs = context?.getSharedPreferences("dailyTaskPrefs", Context.MODE_PRIVATE)

        if (prefs?.getBoolean("isNewDay", false) == true) {
            viewModel.setIsNewDay(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(layoutInflater)
        richPath = binding.animation
        threeDotPath = binding.threeDots
        if (requireContext().hasSoftwareKeys()) {
            val layoutParams = ConstraintLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            layoutParams.bottomMargin = requireActivity().getNavigationBarHeight()
            binding.concertiRecycler.layoutParams = layoutParams
            binding.threeDots.visibility = GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthView.text = currentDayInstance?.time?.let {
            monthDateFormat.format(it).replaceFirstChar { mTime ->
                if (mTime.isLowerCase()) mTime.titlecase(Locale.getDefault()) else mTime.toString()
            }
        }

        binding.calendarView.setLocale(TimeZone.getDefault(), Locale.ITALY)
        binding.calendarView.setUseThreeLetterAbbreviation(true)

        binding.calendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                if (canClickDays) {
                    selectedDayInstance = dateClicked
                    if (viewModel.batchEnded.value != false) {
                        if (dateClicked?.compareDate() == false) {
                            hideEmpty()
                            displayCurrentEvents(dateClicked)
                        }
                        else
                            showEmpty()
                    }
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                selectedDayInstance = firstDayOfNewMonth
                binding.monthView.text = monthDateFormat
                    .format(firstDayOfNewMonth?.time)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                if (firstDayOfNewMonth?.isActualMonth() == true) {
                    binding.calendarView.setCurrentDate(currentDayInstance?.time)
                    if (viewModel.batchEnded.value != false)
                        displayCurrentEvents(currentDayInstance?.time)
                } else {
                    binding.calendarView.setCurrentDate(firstDayOfNewMonth)
                    if (viewModel.batchEnded.value != false)
                        displayCurrentEvents(firstDayOfNewMonth)
                }
            }

        })

        binding.concertiRecycler.layoutManager = layoutManager

        val snapHelper = CustomSnapHelper()
        snapHelper.attachToRecyclerView(binding.concertiRecycler)

        binding.concertiRecycler.setHasFixedSize(true)
        dotsItemDecoration?.let { binding.concertiRecycler.addItemDecoration(it) }
        binding.concertiRecycler.setAccessibility()

        binding.nextMonth.setOnClickListener {
            binding.calendarView.shouldSelectFirstDayOfMonthOnScroll(true)
            binding.calendarView.scrollRight()
        }

        binding.prevMonth.setOnClickListener {
            binding.calendarView.scrollLeft()
        }

        viewModel.concerti.observe(viewLifecycleOwner) {
            collectConcerts(it)
        }

        viewModel.isNewDay.observe(viewLifecycleOwner) {
            if (it) {
                hideAll()
                startAnimation()
                startThreeDots()
                startFirstBatch()
            }
        }

        viewModel.isAppUpdate.observe(viewLifecycleOwner) {
            if (it) {
                hideAll()
                startAnimation()
                startThreeDots()
                startFirstBatch()
            }
        }

        val prefs = context?.getSharedPreferences("dailyTaskPrefs", Context.MODE_PRIVATE)

        if ((file_1.exists() || file_2.exists() || file_3.exists() || file_4.exists()) && prefs?.getBoolean("isNewDay", false) == false)
            viewModel.setConcerts(
                viewModel.getAllConcerts(requireContext())
            )

    }

    private fun displayCurrentEvents(
        currentDate: Date?
    ) {
        val events: List<Event> = binding.calendarView.getEvents(currentDate).orEmpty()
        if (events.isNotEmpty()) {
            val concerts = JsonArray(events.size )
            for (event in events)
            {
                val inside: String = event.data.toString().substring(event.data.toString().indexOf('(') + 1, event.data.toString().lastIndexOf(')'))
                val jsonObject = JsonObject()
                val pairs =
                    inside.split(", (?=[a-zA-Z]+=)".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                for (pair in pairs) {
                    val keyValue = pair.split("=".toRegex(), limit = 2)
                        .toTypedArray()
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim { it <= ' ' }
                        val value = keyValue[1].trim { it <= ' ' }
                        jsonObject.addProperty(key, value)
                    }
                }
                concerts.add(jsonObject)
            }
            adapter = CustomAdapter(concerts) { concertRow ->
                val dialog = CustomDialog(concertRow)
                dialog.show(childFragmentManager,null)

            }
            binding.concertiRecycler.adapter = adapter
            hideEmpty()
        }
        else {
            binding.concertiRecycler.adapter = null
            showEmpty()
        }

    }

    private fun collectConcerts(
        concerts: ArrayList<Concerto?>
    ) {
        binding.calendarView.removeAllEvents()
        concerts.forEach {
            if (it?.time != null) {
                val time: Long = concertoDateFormat.parse(it.time)?.time ?: 0L
                val event = Event(
                    Color.rgb(redColorRGB, greenColorRGB, blueColorRGB),
                    time,
                    it
                )
                if (concertoDateFormat.parse(it.time)?.compareDate() == false)
                    binding.calendarView.addEvent(event)
            }
        }
        if (selectedDayInstance == null)
            displayCurrentEvents(currentDayInstance?.time)
        else
            displayCurrentEvents(selectedDayInstance)

        enableCalendarTouch(true)
    }

    private fun enableCalendarTouch(isEnabled: Boolean) {
        binding.nextMonth.isEnabled = isEnabled
        binding.prevMonth.isEnabled = isEnabled
        canClickDays = isEnabled
    }

    private fun showEmpty() {
        binding.noData.visibility = View.VISIBLE
        binding.noData.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        binding.concertiRecycler.visibility = View.INVISIBLE
    }

    private fun hideEmpty() {
        binding.noData.visibility = View.INVISIBLE
        binding.concertiRecycler.visibility = View.VISIBLE
    }

    private fun hideAll() {
        binding.noData.visibility = View.INVISIBLE
        binding.concertiRecycler.visibility = View.INVISIBLE
        binding.animation.visibility = View.VISIBLE
        binding.courtesyMessage.visibility = View.VISIBLE
        binding.threeDots.visibility = View.VISIBLE
    }

    private fun startFirstBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<BatchWorker_01>()
            .setConstraints(constraints)
            .build()
        firstBatchRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-batch-01",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )

        enableCalendarTouch(false)

        checkWorker(firstBatchRequestId) {
            startSecondBatch()
        }
    }

    private fun startSecondBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<BatchWorker_02>()
            .setConstraints(constraints)
            .build()
        secondBatchRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-batch-02",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )

        checkWorker(secondBatchRequestId) {
            startThirdBatch()
        }
    }

    private fun startThirdBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<BatchWorker_03>()
            .setConstraints(constraints)
            .build()
        thirdBatchRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-batch-03",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )

        checkWorker(thirdBatchRequestId) {
            startFourthBatch()
        }
    }

    private fun startFourthBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<BatchWorker_04>()
            .setConstraints(constraints)
            .build()
        fourthBatchRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-batch-04",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )

        checkFinalWorker(fourthBatchRequestId, {
            handleSuccessBatchState()
        }, {
            handleBlockedBatchState()
        })
    }

    private fun checkWorker(requestID: UUID, callback: () -> Unit) {
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(requestID)
            .observe(requireActivity(), Observer { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.d(tag, "checkWorker ${requestID.mostSignificantBits} enqueued in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d(tag, "checkWorker ${requestID.mostSignificantBits} running in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        callback.invoke()
                    }
                    WorkInfo.State.BLOCKED, WorkInfo.State.FAILED -> {
                        callback.invoke()
                        Log.d(tag, "checkWorker ${requestID.mostSignificantBits} failed/blocked in ${workInfo.runAttemptCount}")
                    }
                    else -> Log.d(tag, "checkWorker ${requestID.mostSignificantBits} canceled in ${workInfo?.runAttemptCount}")
                }
            })
    }

    private fun checkFinalWorker(requestID: UUID, callbacSuccessk: () -> Unit, callbackFailure: () -> Unit) {
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(requestID)
            .observe(requireActivity(), Observer { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.d(tag, "checkWorker ${requestID.mostSignificantBits} enqueued in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d(tag, "checkWorker ${requestID.mostSignificantBits} running in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        callbacSuccessk.invoke()
                    }
                    WorkInfo.State.BLOCKED, WorkInfo.State.FAILED -> {
                        callbackFailure.invoke()
                        Log.d(tag, "checkWorker ${requestID.mostSignificantBits} failed/blocked in ${workInfo.runAttemptCount}")
                    }
                    else -> Log.d(tag, "checkWorker ${requestID.mostSignificantBits} canceled in ${workInfo?.runAttemptCount}")
                }
            })
    }

    private fun startAnimation() {
        richPath
            .pathAnimator
            .interpolator { input ->
                input.plus(1f).mod(1f)
            }
            .listenerEnd {
                startAnimation()
            }
            .delay(0)
            .duration(3000)
            .start()
    }

    private fun startThreeDots() {
        threeDotPath
            .pathAnimator
            .interpolator { input ->
                input.plus(1f).mod(1f)
            }
            .listenerEnd {
                startThreeDots()
            }
            .delay(0)
            .duration(3000)
            .start()
    }

    private fun handleSuccessBatchState() {
        viewModel.setBatchEnded(true)
        val concerts = viewModel.getAllConcerts(requireContext())
        viewModel.setConcerts(
            concerts
        )
        binding.animation.visibility = View.INVISIBLE
        binding.threeDots.visibility = View.INVISIBLE
        binding.courtesyMessage.visibility = View.INVISIBLE
        Toast.makeText(requireContext(), getString(R.string.db_initialized, concerts.size.toString()), Toast.LENGTH_SHORT).show()
    }

    private fun handleBlockedBatchState() {
        if (viewModel.getAllConcerts(requireContext()).isNotEmpty()) {
            viewModel.setBatchEnded(true)
            val concerti = viewModel.getAllConcerts(requireContext())
            viewModel.setConcerts(
                concerti
            )
            binding.animation.visibility = View.INVISIBLE
            binding.threeDots.visibility = View.INVISIBLE
            binding.courtesyMessage.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), getString(R.string.db_initialized, concerti.size.toString()), Toast.LENGTH_SHORT).show()
        } else {
            viewModel.setBatchEnded(false)
            binding.animation.visibility = View.INVISIBLE
            binding.threeDots.visibility = View.INVISIBLE
            binding.courtesyMessage.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), getString(R.string.db_not_init), Toast.LENGTH_LONG).show()
        }
    }

}