package it.antonino.palco.ui.filter.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.model.Concerto
import it.antonino.palco.ui.filter.FilterViewModel
import kotlinx.android.synthetic.main.filter_month_fragment.*
import kotlinx.android.synthetic.main.fragment_local.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Month

class FilterMonthFragment : Fragment() {

    private val viewModel: FilterViewModel by viewModel()
    private var monthList = ArrayList<String>()
    private var monthAdapter : CityListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_month_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Month.values().forEach {
            monthList.add(it.name)
        }
        monthAdapter = CityListAdapter(monthList) {
            viewModel.getNationalConcertsByMonth(it).observe(viewLifecycleOwner, concertsObserver)
            viewModel.getInternationalConcertsByMonth(it).observe(viewLifecycleOwner, concertsObserver)
        }
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)
        filter_month_list.layoutManager = layoutManager
        filter_month_list.adapter = monthAdapter

    }

    private val concertsObserver = Observer<ArrayList<Concerto?>?> {

        

    }

}