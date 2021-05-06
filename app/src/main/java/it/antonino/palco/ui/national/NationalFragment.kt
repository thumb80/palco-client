package it.antonino.palco.ui.national

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import it.antonino.palco.BuildConfig
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomAdapter
import it.antonino.palco.common.CustomSnapHelper
import it.antonino.palco.common.DotsItemDecoration
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.dpToPixels
import it.antonino.palco.ext.getDate
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.Password
import kotlinx.android.synthetic.main.fragment_national.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NationalFragment: Fragment() {

    private val TAG = NationalFragment::class.simpleName
    private val viewModel: NationalViewModel by viewModel()
    private var adapter: CustomAdapter? = null

    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALY)
    private val currentDayInstance: Calendar = Calendar.getInstance(Locale.ITALY)
    private var layoutManager: LinearLayoutManager? = null
    private var position : Int? = null
    private var dotsItemDecoration: DotsItemDecoration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        return inflater.inflate(R.layout.fragment_national, container, false)
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
                val cardContainer = view?.findViewById<LinearLayout>(R.id.cardContainer)
                position = view?.let { recyclerView.getChildAdapterPosition(it) }
                position?.let {
                    adapter?.setSelectedItem(it)
                }

                /*when ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == position) {
                    true -> {
                        cardContainer?.background = ResourcesCompat.getDrawable(resources,R.drawable.card_view_background_white,null)
                    }
                    else -> cardContainer?.background = ResourcesCompat.getDrawable(resources,R.drawable.card_view_background_grey,null)
                 }

                if (dx == 0 && dy == 0)
                    cardContainer?.background = ResourcesCompat.getDrawable(resources,R.drawable.card_view_background_white,null)*/
            }
        })

        nextMonth.setOnClickListener {
            calendar_view.scrollRight()
        }

        prevMonth.setOnClickListener {
            calendar_view.scrollLeft()
        }

        (activity as MainActivity).showProgress()
        val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        viewModel.getConcertiNazionali(Password(sharedPreferences?.getString("password","")!!)).observe(viewLifecycleOwner, concertiObserver)

    }

    private val concertiObserver = Observer<ArrayList<Concerto?>?> {
        when (!it.isNullOrEmpty()) {
            true -> {
                (activity as MainActivity).hideProgress()
                calendar_view.removeAllEvents()
                for (concerto in it) {
                    val event = Event(
                        Color.rgb(241, 90, 36),
                        concerto?.let {
                            it.getTime().getDate()
                        }!! + 86400000,
                        concerto
                    )
                    calendar_view.addEvent(event)
                }
                displayCurrentEvents(currentDayInstance.time)
            }
            else -> {
                (activity as MainActivity).hideProgress()
                Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show()
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
        //val bills: ArrayList<String?> = ArrayList(events.size)
        val times: ArrayList<String> = ArrayList(events.size)
        for (concerto in concerti)
        {
            if (!checkObject(concerto)) {
                artisti.add(concerto.asJsonObject.get("artist").asString)
                places.add(concerto.asJsonObject.get("place").asString)
                cities.add(concerto.asJsonObject.get("city").asString)
                times.add(concerto.asJsonObject.get("time").asString)
                //bills.add(concerto.asJsonObject.get("bill").asString)
            }
        }

        artisti.add(0,"")
        places.add(0,"")
        cities.add(0,"")
        times.add(0,"")
        //bills.add(0,"")

        //position?.let { adapter?.setSelectedItem(it) }
        adapter = CustomAdapter(artisti, places, cities, times) { concertRow ->

            if (!BuildConfig.BUY_TICKET) {
                val dialog = CustomDialog(concertRow)
                dialog.show(childFragmentManager,null)
            }
            else {
                //val dialog = CustomDialog(concertRow)
                //dialog.show(childFragmentManager,null)
                Toast.makeText(context, "Ops c'è stato un problema", Toast.LENGTH_LONG).show()
            }

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

    private fun showEmpty() {
        (activity as MainActivity).hideProgress()
        no_data.visibility = View.VISIBLE
        no_data.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        concerti_recycler?.visibility = View.INVISIBLE
    }

    private fun hideEmpty() {
        no_data.visibility = View.INVISIBLE
        concerti_recycler?.visibility = View.VISIBLE
    }

    //TODO return true also if event is before Instant.now()
    private fun checkObject(concerto: JsonElement): Boolean {
        return concerto.asJsonObject.get("artist").asString.isNullOrEmpty()
                || concerto.asJsonObject.get("place").asString.isNullOrEmpty()
                || concerto.asJsonObject.get("city").asString.isNullOrEmpty()
                || concerto.asJsonObject.get("time").asString.isNullOrEmpty()
                //|| concerto.asJsonObject.get("time").asString?.let { getDateTime(it)?.before(DateTimeUtils.toDate(Instant.now())) } == true
    }

    fun getDateTime(time: String): Date? {
        val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        return insdf.parse(time)
    }

}