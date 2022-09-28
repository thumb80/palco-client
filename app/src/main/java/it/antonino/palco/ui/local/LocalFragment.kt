package it.antonino.palco.ui.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import it.antonino.palco.BuildConfig
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.adapter.CustomAdapter
import it.antonino.palco.common.CustomSnapHelper
import it.antonino.palco.common.DotsLocalItemDecoration
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.getDate
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.ui.viewmodel.SharedViewModel
import it.antonino.palco.util.PalcoUtils
import kotlinx.android.synthetic.main.fragment_local.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LocalFragment : Fragment() {

    private val viewModel: SharedViewModel by viewModel()
    private var sharedPreferences: SharedPreferences? = null
    private var adapter: CustomAdapter? = null
    private var cityAdapter: CityListAdapter? = null
    private var concertilayoutManager: LinearLayoutManager? = null

    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALY)
    private var cityList = ArrayList<City>()
    private var position : Int? = null
    private var dotsLocalItemDecoration: DotsLocalItemDecoration? = null
    private var lastPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        dotsLocalItemDecoration = DotsLocalItemDecoration()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCities().observe(viewLifecycleOwner, cityObserver)

        calendar_view.setLocale(TimeZone.getTimeZone("it"), Locale.ITALY)
        calendar_view.setUseThreeLetterAbbreviation(true)
        calendar_view.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                displayCurrentEvents(dateClicked)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                monthView?.text = simpleDateFormat.format(firstDayOfNewMonth.time)
                displayCurrentEvents(firstDayOfNewMonth)
            }

        })

        nextMonth.setOnClickListener {
            calendar_view.scrollRight()
        }

        prevMonth.setOnClickListener {
            calendar_view.scrollLeft()
        }

        val dividerItemDecoration = DividerItemDecoration(
            context,
            LinearLayoutManager.HORIZONTAL
        )
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.card_view_divider, null)!!)
        concertilayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        concerts_list.layoutManager = concertilayoutManager
        concerts_list?.setHasFixedSize(true)
        concerts_list?.addItemDecoration(dividerItemDecoration)

        val snapHelper = CustomSnapHelper()
        snapHelper.attachToRecyclerView(concerts_list)

        concerts_list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val view = snapHelper.findSnapView(recyclerView.layoutManager)
                position = view?.let { recyclerView.getChildAdapterPosition(it) }
                position?.let {
                    adapter?.setSelectedItem(it)
                }
                lastPosition = position!!

                if ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() < 0)
                    return
                else if ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0 ||
                    PalcoUtils.compareLastDayOfMonth(adapter?.times?.get(
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())!!
                    ))  {
                    monthView?.text = simpleDateFormat.format(PalcoUtils.getDateTime(adapter?.times?.get(1)!!)!!)
                    calendar_view.setCurrentDate(PalcoUtils.getDateTime(adapter?.times?.get(1)!!)!!)
                }
                else  {
                    monthView?.text = simpleDateFormat.format(PalcoUtils.getDateTime(adapter?.times?.get(
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    )!!)!!)
                    calendar_view.setCurrentDate(
                        PalcoUtils.getDateTime(
                            adapter?.times?.get(
                                (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                            )!!
                        )
                    )
                }
            }
        })

    }

    private val cityObserver = Observer<ArrayList<City?>?> {
        when (!it.isNullOrEmpty()) {
            true -> {
                for (city in it) {
                    cityList.add(city!!)
                }
                cityAdapter = CityListAdapter(cityList) {
                    (activity as MainActivity).showProgress()
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.weight = 0.1F
                    header.layoutParams = layoutParams
                    header.text = getString(R.string.city_selected, it)
                    viewModel.getConcertiNazionaliByCity(it).observe(viewLifecycleOwner, concertiObserver)
                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                city_recycler.layoutManager = layoutManager
                city_recycler.adapter = cityAdapter
            }
        }
    }

    private val concertiObserver = Observer<ArrayList<Concerto?>?> {
        when (!it.isNullOrEmpty()) {
            true -> {
                (activity as MainActivity).hideProgress()
                it.sortByDescending {
                    it?.getTime()
                }
                it.reverse()
                showConcerti()
                showConcertiList()
                var artisti = ArrayList<String>()
                var places = ArrayList<String>()
                var times = ArrayList<String>()
                val cities = ArrayList<String>()
                //val bills = ArrayList<String?>()
                for (concerto in it) {
                    if (!PalcoUtils.checkObject(concerto)) {
                        artisti.add(concerto?.getArtist()!!)
                        places.add(concerto.getPlace())
                        cities.add(concerto.getCity())
                        times.add(concerto.getTime())
                        //bills.add(concerto.getBill())
                        val event = Event(
                            Color.rgb(241, 90, 36),
                            concerto.let {
                                it.getTime().getDate()
                            } + 86400000,
                            concerto
                        )
                        calendar_view.addEvent(event)
                    }
                }

                artisti.add(0,"")
                places.add(0,"")
                cities.add(0,"")
                times.add(0,"")

                adapter = CustomAdapter(artisti,places,cities, times) { concertRow ->

                    if (!BuildConfig.BUY_TICKET) {
                        val dialog = CustomDialog(concertRow)
                        dialog.show(childFragmentManager,null)
                    }
                    else {
                        //val dialog = CustomDialog(ConcertRow(concertRow.artist,concertRow.place,null,concertRow.bill,concertRow.artistThumb))
                        //dialog.show(childFragmentManager,null)
                        Toast.makeText(context, "Ops c'è stato un problema", Toast.LENGTH_LONG).show()
                    }

                }
                dotsLocalItemDecoration?.let { it_decoration -> concerts_list?.addItemDecoration(it_decoration) }
                concerts_list.adapter = adapter

                monthView?.text = simpleDateFormat.format(PalcoUtils.getDateTime(adapter?.times?.get(1)!!)!!)

                header.setOnClickListener {
                    calendar_view?.removeAllEvents()
                    header.text = getString(R.string.city_select)
                    dotsLocalItemDecoration?.let { it_decoration -> concerts_list?.removeItemDecoration(it_decoration) }
                    hideConcerti()
                }
            }
            else -> {
                (activity as MainActivity).hideProgress()
                hideConcerti()
                Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showConcerti() {
        monthLayout.visibility = View.VISIBLE
        calendar_view.visibility = View.VISIBLE
        nested_concerti_list.visibility = View.VISIBLE
        city_recycler.visibility = View.INVISIBLE
        arrow_container.visibility = View.GONE
    }

    private fun hideConcerti() {
        nested_concerti_list.visibility = View.INVISIBLE
        monthLayout.visibility = View.INVISIBLE
        calendar_view.visibility = View.INVISIBLE
        city_recycler.visibility = View.VISIBLE
        arrow_container.visibility = View.VISIBLE
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 0.05F
        header.layoutParams = layoutParams
    }

    private fun hideConcertiList() {
        concerts_list.visibility = View.INVISIBLE
        no_data.visibility = View.VISIBLE
        no_data.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
    }

    private fun showConcertiList() {
        concerts_list.visibility = View.VISIBLE
        no_data.visibility = View.INVISIBLE
    }

    private fun displayCurrentEvents(date: Date) {
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        val dateClicked = outputFormat.format(date)
        val position = adapter?.times?.indexOfFirst {
            it == dateClicked
        }
        if (position == null || position == RecyclerView.NO_POSITION)
            hideConcertiList()
        else if (position - lastPosition < 20) {
            showConcertiList()
            concerts_list?.smoothScrollToPosition(position)
        }
        else {
            showConcertiList()
            concerts_list.scrollToPosition(position)
        }
    }

}