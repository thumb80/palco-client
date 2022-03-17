package it.antonino.palco.ui.filters.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.BuildConfig
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.adapter.CustomAdapter
import it.antonino.palco.common.CustomSnapHelper
import it.antonino.palco.common.DotsItemDecoration
import it.antonino.palco.databinding.FragmentFiltersBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.Months
import it.antonino.palco.ui.filters.FiltersFragment
import it.antonino.palco.ui.filters.FiltersViewModel
import kotlinx.android.synthetic.main.fragment_filter_month.*
import kotlinx.android.synthetic.main.fragment_filters.*
import kotlinx.android.synthetic.main.fragment_national.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersMonthFragment : Fragment() {

    private val viewModel: FiltersViewModel by viewModel()
    private val monthsList = ArrayList<String>()
    private var monthAdapter: CityListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_filter_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Months.values().forEach {
            monthsList.add(it.name)
        }
        monthAdapter = CityListAdapter(monthsList) {
            viewModel.getConcertiNazionaliByMonth(Months.getMonthNum(it)).observe(viewLifecycleOwner, monthObserver)
        }
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)
        month_recycler.layoutManager = layoutManager
        month_recycler.adapter = monthAdapter


    }

    private val monthObserver = Observer<ArrayList<Concerto?>?> {
        if (!it.isNullOrEmpty()) {
            viewModel.concerts.value?.addAll(it)
            viewModel.postFillConcerts(true)
        }
        else
            viewModel.postFillConcerts(false)
        (activity as MainActivity).hideProgress()
    }

}