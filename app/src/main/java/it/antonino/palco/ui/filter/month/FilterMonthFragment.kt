package it.antonino.palco.ui.filter.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomFilterAdapter
import it.antonino.palco.adapter.MonthListAdapter
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.DateSearchDTO
import it.antonino.palco.model.Months
import it.antonino.palco.util.Constant.layoutWeight
import it.antonino.palco.util.PalcoUtils
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.filter_month_fragment.*
import java.util.*

class FilterMonthFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private var monthAdapter : MonthListAdapter? = null
    private var adapter: CustomFilterAdapter? = null
    var artisti = ArrayList<String>()
    var places = ArrayList<String>()
    var times = ArrayList<String>()
    var cities = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_month_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.months.observe(viewLifecycleOwner, object : Observer<ArrayList<String>> {
            override fun onChanged(t: ArrayList<String>?) {
                monthAdapter = MonthListAdapter(t) {

                    (activity as MainActivity).showProgress()

                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.weight = layoutWeight
                    filter_header_month.layoutParams = layoutParams
                    filter_header_month.text = getString(R.string.filter_month_selected, it)

                    val calendar = Calendar.getInstance()
                    val year = it.substringAfter(" ")
                    var month = ""
                    val tempMonth = Months.values().filter { month ->
                        month.name == it.substringBefore(" ")
                    }[0].ordinal + 1
                    month = if (tempMonth < 10)
                        "0".plus(tempMonth)
                    else
                        tempMonth.toString()
                    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val minDay = "01"
                    val dateSearchDTO = DateSearchDTO(
                        startDate = "$year-$month-$minDay",
                        endDate = "$year-$month-$maxDay"
                    )

                    artisti = arrayListOf()
                    places = arrayListOf()
                    cities = arrayListOf()
                    times = arrayListOf()

                    viewModel.getNationalConcertsByMonth(dateSearchDTO).observe(viewLifecycleOwner, concertsObserver)
                }

                displayMonths()
            }

        })

    }

    private val concertsObserver = Observer<ArrayList<Concerto?>?> {

        when(!it.isNullOrEmpty()) {
            true -> {
                (activity as MainActivity).hideProgress()
                showConcerti()

                for (concerto in it) {
                    if (!PalcoUtils.checkObject(concerto)) {
                        artisti.add(concerto?.getArtist()!!)
                        places.add(concerto.getPlace())
                        cities.add(concerto.getCity())
                        times.add(
                            PalcoUtils.getDateTimeString(
                                concerto.getTime().substringBefore(" ")
                            )
                        )
                    }
                }

                adapter = CustomFilterAdapter(
                    artisti,
                    places,
                    cities,
                    times
                ) {
                    val dialog = CustomDialog(it)
                    dialog.show(childFragmentManager,null)
                }

                val dividerItemDecoration = DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                filter_concert_list.layoutManager = layoutManager
                filter_concert_list.adapter = adapter
                filter_concert_list.addItemDecoration(dividerItemDecoration)

                filter_header_month.setOnClickListener {
                    filter_header_month.text = getString(R.string.filter_month_select)
                    hideConcerti()
                }

            }
            else -> {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun displayMonths() {
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)
        filter_month_list.layoutManager = layoutManager
        filter_month_list.adapter = monthAdapter
    }

    private fun showConcerti() {
        filter_month_list.visibility = View.GONE
        filter_concert_list.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        filter_month_list.visibility = View.VISIBLE
        filter_concert_list.visibility = View.GONE
    }

}
