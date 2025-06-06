package it.antonino.palco.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import it.antonino.palco.PalcoApplication
import it.antonino.palco.PalcoApplication.Companion.file
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentEventsBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.DotsItemDecoration
import it.antonino.palco.ext.compareDate
import it.antonino.palco.ext.isActualMonth
import it.antonino.palco.ext.setAccessibility
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.CustomAdapter
import it.antonino.palco.model.CustomSnapHelper
import it.antonino.palco.model.HorizontalSpaceItemDecoration
import it.antonino.palco.workers.Scrape01Worker
import it.antonino.palco.workers.Scrape02Worker
import it.antonino.palco.workers.ScrapeRockolWorker
import it.antonino.palco.util.Constant
import it.antonino.palco.util.Constant.concertoDateFormat
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class EventsFragment: Fragment() {

    val viewModel: SharedViewModel by activityViewModel<SharedViewModel>()
    private var adapter: CustomAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var dotsItemDecoration: DotsItemDecoration? = null
    private lateinit var binding: FragmentEventsBinding
    private lateinit var workCanzoniRequestId: UUID
    private lateinit var workGothRequestId: UUID
    private lateinit var workRockolRequestId: UUID
    private lateinit var richPath: PathView
    private lateinit var threeDotPath: PathView

    companion object {
        private var currentDayInstance: Calendar? = null
        private var selectedDayInstance: Date? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentEventsBinding.inflate(layoutInflater)

        currentDayInstance = Calendar.getInstance(TimeZone.getDefault())

        dotsItemDecoration = DotsItemDecoration(
            resources.getDimension(R.dimen.dp_4).toInt(),
            resources.getDimension(R.dimen.dp_6).toInt(),
            resources.getDimension(R.dimen.dp_8).toInt(),
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthView.text = currentDayInstance?.time?.let {
            Constant.monthDateFormat.format(it).capitalize()
        }

        binding.calendarView.setLocale(TimeZone.getDefault(), Locale.ITALY)
        binding.calendarView.setUseThreeLetterAbbreviation(true)

        binding.calendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
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

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                selectedDayInstance = firstDayOfNewMonth
                binding.monthView.text = Constant.monthDateFormat
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
            collectConcerti(it)
        }

        viewModel.isNewDay.observe(viewLifecycleOwner) {
            if (it) {
                hideAll()
                startAnimation()
                startThreeDots()
                setScrapeCanzoniBatch()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isTargetHour.collect { isTarget ->
                    if (isTarget) {
                        hideAll()
                        startAnimation()
                        startThreeDots()
                        setScrapeCanzoniBatch()
                    }
                }
            }
        }

        val prefs = context?.getSharedPreferences("dailyTaskPrefs", Context.MODE_PRIVATE)

        if (file?.exists() == true && prefs?.getBoolean("isNewDay", false) == false)
            viewModel.setConcerti(
                viewModel.getAllConcerti()
            )

    }

    private fun displayCurrentEvents(
        currentDate: Date?
    ) {

        val events: List<Event> = binding.calendarView.getEvents(currentDate).orEmpty()

        if (events.isNotEmpty()) {
            val concerti = JsonArray(events.size )
            for (event in events)
            {
                concerti.add(JsonParser().parse(GsonBuilder().setLenient().create().toJson(event.data)))
            }
            adapter = CustomAdapter(concerti) { concertRow ->
                val dialog = CustomDialog(concertRow)
                dialog.show(childFragmentManager,null)

            }
            val spacing = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
            binding.concertiRecycler.addItemDecoration(HorizontalSpaceItemDecoration(spacing))
            binding.concertiRecycler.adapter = adapter
            hideEmpty()
        }
        else {
            binding.concertiRecycler.adapter = null
            showEmpty()
        }

    }

    private fun collectConcerti(
        concerti: ArrayList<Concerto>
    ) {
        binding.calendarView.removeAllEvents()
        for (concerto in concerti) {
            val time: Long = concertoDateFormat.parse(concerto.time)?.time ?: 0L
            val event = Event(
                Color.rgb(Constant.redColorRGB, Constant.greenColorRGB, Constant.blueColorRGB),
                time,
                concerto
            )
            if (concertoDateFormat.parse(concerto.time)?.compareDate() == false)
                binding.calendarView.addEvent(event)
        }
        if (selectedDayInstance == null)
            displayCurrentEvents(currentDayInstance?.time)
        else
            displayCurrentEvents(selectedDayInstance)
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

    private fun setScrapeCanzoniBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<Scrape01Worker>()
            .setConstraints(constraints)
            .build()
        workCanzoniRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-scrape-canzoni",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )

        //PalcoApplication.concerti = arrayListOf()

        checkCanzoniWorker()
    }

    private fun setScrapeGothBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<Scrape02Worker>()
            .setConstraints(constraints)
            .build()
        workGothRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-scrape-goth",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )
        //PalcoApplication.concerti = arrayListOf()
        checkGothWorker()
    }

    private fun setScrapeRockShockBatch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val scrapeWork = OneTimeWorkRequestBuilder<ScrapeRockolWorker>()
            .setConstraints(constraints)
            .build()
        workRockolRequestId = scrapeWork.id
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "it-antonino-scrape-rockol",
            ExistingWorkPolicy.REPLACE,
            scrapeWork
        )
        checkRockShockWorker()
    }

    private fun checkCanzoniWorker() {

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workCanzoniRequestId)
            .observe(requireActivity(), Observer { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.d(tag, "checkCanzoniWorker enqueued in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d(tag, "checkCanzoniWorker running in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        setScrapeGothBatch()
                        Log.d(tag, "checkCanzoniWorker success in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.BLOCKED, WorkInfo.State.FAILED -> {
                        PalcoApplication.concerti = arrayListOf()
                        setScrapeGothBatch()
                        Log.d(tag, "checkCanzoniWorker failed/blocked in ${workInfo.runAttemptCount}")
                    }
                    else -> Log.d(tag, "checkCanzoniWorker canceled in ${workInfo?.runAttemptCount}")
                }
            })
    }

    private fun checkGothWorker() {

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workGothRequestId)
            .observe(requireActivity(), Observer { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.d(tag, "checkGothWorker enqueued in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d(tag, "checkGothWorker running in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        viewModel.setBatchEnded(true)
                        val concerti = viewModel.getAllConcerti()
                        viewModel.setConcerti(
                            concerti
                        )
                        binding.animation.visibility = View.INVISIBLE
                        binding.threeDots.visibility = View.INVISIBLE
                        binding.courtesyMessage.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), getString(R.string.db_initialized, concerti.size.toString()), Toast.LENGTH_SHORT).show()
                    }
                    WorkInfo.State.BLOCKED, WorkInfo.State.FAILED -> {
                        if (viewModel.getAllConcerti().isNotEmpty()) {
                            viewModel.setBatchEnded(true)
                            val concerti = viewModel.getAllConcerti()
                            viewModel.setConcerti(
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
                        Log.d(tag, "checkGothWorker failed/blocked in ${workInfo.runAttemptCount}")
                    }
                    else -> Log.d(tag, "checkGothWorker canceled in ${workInfo?.runAttemptCount}")
                }
            })
    }

    private fun checkRockShockWorker() {

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workRockolRequestId)
            .observe(requireActivity(), Observer { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.d(tag, "checkRockShockWorker enqueued in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d(tag, "checkRockShockWorker running in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        viewModel.setBatchEnded(true)
                        val concerti = viewModel.getAllConcerti()
                        viewModel.setConcerti(
                            concerti
                        )
                        binding.animation.visibility = View.INVISIBLE
                        binding.threeDots.visibility = View.INVISIBLE
                        binding.courtesyMessage.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), getString(R.string.db_initialized, concerti.size.toString()), Toast.LENGTH_SHORT).show()
                        Log.d(tag, "checkRockShockWorker success in ${workInfo.runAttemptCount}")
                    }
                    WorkInfo.State.BLOCKED, WorkInfo.State.FAILED -> {
                        if (viewModel.getAllConcerti().isNotEmpty()) {
                            viewModel.setBatchEnded(true)
                            val concerti = viewModel.getAllConcerti()
                            viewModel.setConcerti(
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
                        Log.d(tag, "checkRockShockWorker failed/blocked in ${workInfo.runAttemptCount}")
                    }
                    else -> Log.d(tag, "checkRockShockWorker canceled in ${workInfo?.runAttemptCount}")
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

}