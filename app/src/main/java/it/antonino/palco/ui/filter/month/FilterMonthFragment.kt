package it.antonino.palco.ui.filter.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.BuildConfig
import it.antonino.palco.MainActivity
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomFilterAdapter
import it.antonino.palco.adapter.MonthListAdapter
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.Months
import it.antonino.palco.ui.viewmodel.SharedViewModel
import it.antonino.palco.util.Constant.maxMonthValue
import it.antonino.palco.util.Constant.layoutWeight
import it.antonino.palco.util.PalcoUtils
import kotlinx.android.synthetic.main.filter_month_fragment.filter_header_month
import kotlinx.android.synthetic.main.filter_month_fragment.filter_month_list
import kotlinx.android.synthetic.main.filter_month_fragment.filter_concert_list
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterMonthFragment : Fragment() {

    private val viewModel: SharedViewModel by viewModel()
    private var monthList = ArrayList<String>()
    private var monthAdapter : MonthListAdapter? = null
    private var adapter: CustomFilterAdapter? = null
    var artisti = ArrayList<String>()
    var places = ArrayList<String>()
    var times = ArrayList<String>()
    val cities = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_month_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Months.values().forEach {
            monthList.add(it.name)
        }


        monthAdapter = MonthListAdapter(monthList) {

            val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            layoutParams.weight = layoutWeight
            filter_header_month.layoutParams = layoutParams
            filter_header_month.text = getString(R.string.filter_month_selected, it)

            viewModel.getNationalConcertsByMonth(
                if (Months.valueOf(it).ordinal + 1 < maxMonthValue)
                    "0"+(Months.valueOf(it).ordinal + 1).toString()
                else
                    (Months.valueOf(it).ordinal + 1).toString()
            ).observe(viewLifecycleOwner, concertsObserver)
            /*viewModel.getInternationalConcertsByMonth(
                if (Months.valueOf(it).ordinal + 1 < maxMonthValue)
                    "0"+(Months.valueOf(it).ordinal + 1).toString()
                else
                    (Months.valueOf(it).ordinal + 1).toString()
            ).observe(viewLifecycleOwner, concertsObserver)*/

            (activity as MainActivity).showProgress()
        }

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)
        filter_month_list.layoutManager = layoutManager
        filter_month_list.adapter = monthAdapter

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
                        times.add(concerto.getTime())
                    }
                }

                adapter = CustomFilterAdapter(
                    artisti,
                    places,
                    cities,
                    times
                ) {
                    if (!BuildConfig.BUY_TICKET) {
                        val dialog = CustomDialog(it)
                        dialog.show(childFragmentManager,null)
                    }
                    else {
                        Toast.makeText(context, "Ops c'è stato un problema", Toast.LENGTH_LONG).show()
                    }
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

    private fun showConcerti() {
        filter_month_list.visibility = View.GONE
        filter_concert_list.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        filter_month_list.visibility = View.VISIBLE
        filter_concert_list.visibility = View.GONE
    }

}
