package it.antonino.palco.ui.national

import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomAdapter
import it.antonino.palco.common.CustomSnapHelper
import it.antonino.palco.common.DotsItemDecoration
import it.antonino.palco.ext.*
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.blueColorRGB
import it.antonino.palco.util.Constant.concertoTimeOffset
import it.antonino.palco.util.Constant.greenColorRGB
import it.antonino.palco.util.Constant.redColorRGB
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_events.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventsFragment: Fragment() {

    private val viewModel: SharedViewModel by sharedViewModel()
    private var adapter: CustomAdapter? = null
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALY)
    private val currentDayInstance: Calendar = Calendar.getInstance(Locale.ITALY)
    private var layoutManager: LinearLayoutManager? = null
    private var position : Int? = null
    private var dotsItemDecoration: DotsItemDecoration? = null
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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

        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monthView?.text = simpleDateFormat.format(
            DateTimeUtils.toDate(
                Instant.ofEpochMilli(
                    Calendar.getInstance(Locale.ITALY).timeInMillis
                )
            )
        )

        calendar_view.setLocale(TimeZone.getTimeZone("it"), Locale.ITALY)
        calendar_view.setUseThreeLetterAbbreviation(true)

        calendar_view.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                displayCurrentEvents(dateClicked!!)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                monthView?.text = simpleDateFormat.format(firstDayOfNewMonth?.time)
                displayCurrentEvents(firstDayOfNewMonth!!)
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
        concerti_recycler.layoutManager = layoutManager

        val snapHelper = CustomSnapHelper()
        snapHelper.attachToRecyclerView(concerti_recycler)

        concerti_recycler?.setHasFixedSize(true)
        concerti_recycler?.addItemDecoration(dividerItemDecoration)
        dotsItemDecoration?.let { concerti_recycler?.addItemDecoration(it) }

        concerti_recycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val view = snapHelper.findSnapView(recyclerView.layoutManager)
                position = view?.let { recyclerView.getChildAdapterPosition(it) }
                position?.let {
                    adapter?.setSelectedItem(it)
                }

            }
        })

        nextMonth.setOnClickListener {
            calendar_view.scrollRight()
        }

        prevMonth.setOnClickListener {
            calendar_view.scrollLeft()
        }

        viewModel.concerti.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                displayNationalEvents(it)
            }
        }
    }

    private fun displayCurrentEvents(currentDate: Date) {

        no_data.visibility = View.INVISIBLE

        val events: List<Event> = calendar_view.getEvents(currentDate).orEmpty()
        val concerti = JsonArray(events.size)
        for (event in events)
        {
            concerti.add(JsonParser().parse(GsonBuilder().setLenient().create().toJson(event.data)))
        }
        val artisti: ArrayList<String> = ArrayList(events.size)
        val places: ArrayList<String> = ArrayList(events.size)
        val cities: ArrayList<String> = ArrayList(events.size)
        val times: ArrayList<String> = ArrayList(events.size)
        for (concerto in concerti)
        {
            if (!concerto.checkObject()) {
                artisti.add(concerto.asJsonObject.get("artist").asString)
                places.add(concerto.asJsonObject.get("place").asString)
                cities.add(concerto.asJsonObject.get("city").asString)
                times.add(concerto.asJsonObject.get("time").asString)
            }
        }

        artisti.add(0,"")
        places.add(0,"")
        cities.add(0,"")
        times.add(0,"")

        adapter = CustomAdapter(artisti, places, cities, times) { concertRow ->

            val dialog = CustomDialog(concertRow)
            dialog.show(childFragmentManager,null)

        }

        if (events.isNotEmpty()) {
            concerti_recycler?.adapter = adapter
            hideEmpty()
        }
        else {
            concerti_recycler?.adapter = null
            showEmpty()
        }

    }

    private fun displayNationalEvents(concerti: ArrayList<Concerto?>?) {
        if (concerti != null) {
            for (concerto in concerti) {
                if (concerto?.getTime()?.let { time -> time.compareDate() } == false) {
                    val event = Event(
                        Color.rgb(redColorRGB, greenColorRGB, blueColorRGB),
                        concerto.getTime().getDate() + concertoTimeOffset,
                        concerto
                    )
                    calendar_view.addEvent(event)
                }
            }
            displayCurrentEvents(currentDayInstance.time)
        }
        else {
            showEmpty()
        }
    }

    private fun showEmpty() {
        no_data.visibility = View.VISIBLE
        no_data.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        concerti_recycler?.visibility = View.INVISIBLE
    }

    private fun hideEmpty() {
        no_data.visibility = View.INVISIBLE
        concerti_recycler?.visibility = View.VISIBLE
    }
}
