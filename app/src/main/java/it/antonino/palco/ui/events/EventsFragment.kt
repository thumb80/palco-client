package it.antonino.palco.ui.events

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomAdapter
import it.antonino.palco.common.CustomSnapHelper
import it.antonino.palco.common.DotsItemDecoration
import it.antonino.palco.databinding.FragmentEventsBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.compareDate
import it.antonino.palco.ext.isActualMonth
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.blueColorRGB
import it.antonino.palco.util.Constant.greenColorRGB
import it.antonino.palco.util.Constant.monthDateFormat
import it.antonino.palco.util.Constant.redColorRGB
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class EventsFragment: Fragment() {

    private val viewModel: SharedViewModel by sharedViewModel()
    private var adapter: CustomAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var dotsItemDecoration: DotsItemDecoration? = null
    private lateinit var binding: FragmentEventsBinding

    companion object {
        private var currentDayInstance: Calendar? = null
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(layoutInflater)

        viewModel.concerti.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                collectConcerti(it)
                displayCurrentEvents(currentDayInstance?.time)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthView.text = currentDayInstance?.time?.let {
            monthDateFormat.format(it).capitalize()
        }

        binding.calendarView.setLocale(TimeZone.getDefault(), Locale.ITALY)
        binding.calendarView.setUseThreeLetterAbbreviation(true)

        binding.calendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                if (dateClicked?.compareDate() == false) {
                    hideEmpty()
                    displayCurrentEvents(dateClicked)
                }
                else
                    showEmpty()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                binding.monthView.text = monthDateFormat
                    .format(firstDayOfNewMonth?.time)
                    .capitalize()
                if (firstDayOfNewMonth?.isActualMonth() == true) {
                    binding.prevMonth.visibility = View.GONE
                    binding.calendarView.setCurrentDate(currentDayInstance?.time)
                    displayCurrentEvents(currentDayInstance?.time)
                } else {
                    displayCurrentEvents(firstDayOfNewMonth)
                }

            }

        })

        val dividerItemDecoration = DividerItemDecoration(
            context,
            LinearLayoutManager.HORIZONTAL
        )
        dividerItemDecoration.setDrawable(resources?.getDrawable(R.drawable.card_view_divider)!!)

        layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.concertiRecycler.layoutManager = layoutManager

        val snapHelper = CustomSnapHelper()
        snapHelper.attachToRecyclerView(binding.concertiRecycler)

        binding.concertiRecycler.setHasFixedSize(true)
        binding.concertiRecycler.addItemDecoration(dividerItemDecoration)
        dotsItemDecoration?.let { binding.concertiRecycler.addItemDecoration(it) }

        binding.nextMonth.setOnClickListener {
            binding.prevMonth.visibility = View.VISIBLE
            binding.calendarView.shouldSelectFirstDayOfMonthOnScroll(true)
            binding.calendarView.scrollRight()
        }

        binding.prevMonth.setOnClickListener {
            binding.calendarView.scrollLeft()
        }

    }

    private fun displayCurrentEvents(currentDate: Date?) {

        binding.noData.visibility = View.INVISIBLE

        val events: List<Event> = binding.calendarView.getEvents(currentDate).orEmpty()

        if (events.isNotEmpty()) {
            val concerti = JsonArray(events.size)
            for (event in events)
            {
                concerti.add(JsonParser().parse(GsonBuilder().setLenient().create().toJson(event.data)))
            }
            concerti.set(0, GsonBuilder().create().toJsonTree(Concerto(
                artist = "",
                place = "",
                city = "",
                time = java.sql.Date(0L)
            )))
            adapter = CustomAdapter(concerti) { concertRow ->
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

    private fun collectConcerti(concerti: ArrayList<Concerto?>?) {
        if (concerti != null) {
            for (concerto in concerti) {
                if (concerto?.getTime()?.compareDate() == false) {
                    val event = concerto.getTime().let { time ->
                        Event(
                            Color.rgb(redColorRGB, greenColorRGB, blueColorRGB),
                            time.time,
                            concerto
                        )
                    }
                    binding.calendarView.addEvent(event)
                }
            }
        }
        else {
            showEmpty()
        }
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
}
