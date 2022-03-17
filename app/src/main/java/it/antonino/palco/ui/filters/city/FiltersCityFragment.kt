package it.antonino.palco.ui.filters.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.adapter.MixedListAdapter
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.ui.filters.FiltersViewModel
import kotlinx.android.synthetic.main.fragment_filter_city.*
import kotlinx.android.synthetic.main.fragment_filter_month.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersCityFragment : Fragment() {

    private val viewModel: FiltersViewModel by viewModel()
    private var mixedCityAdapter: MixedListAdapter? = null
    private var cities: ArrayList<Pair<String, Int>> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getNationalCities().observe(viewLifecycleOwner, citiesNazionaliObserver)
        viewModel.getStranieriCities().observe(viewLifecycleOwner, citiesEsteroObserver)

        return inflater.inflate(R.layout.fragment_filter_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)

        if (viewModel.cities.value == true) {
            mixedCityAdapter = MixedListAdapter(cities, {
                viewModel.getConcertiNazionaliByCity(it).observe(viewLifecycleOwner, concertiNazionaliObserver)
            }, {
                viewModel.getConcertiStranieriByCity(it).observe(viewLifecycleOwner, concertiStranieriObserver)
            })

            city_recycler.layoutManager = layoutManager
            city_recycler.adapter = mixedCityAdapter
        }

    }

    private val citiesNazionaliObserver = Observer<ArrayList<City?>?> {
        if (!it.isNullOrEmpty()) {
            it.forEach {
                it?.getCity()?.let {
                        city ->
                            if (!cities.contains(Pair(city, 0)))
                                cities.add(Pair(city, 0))
                }
            }
        }
    }

    private val citiesEsteroObserver = Observer<ArrayList<City?>?> {
        if (!it.isNullOrEmpty()) {
            it.forEach {
                it?.getCity()?.let {
                        city ->
                            if (!cities.contains(Pair(city, 1)))
                                cities.add(Pair(city, 1))
                }
            }
        }
    }

    private val concertiNazionaliObserver = Observer<ArrayList<Concerto?>?> {
        if (!it.isNullOrEmpty()) {
            viewModel.concerts.value?.addAll(it)
            viewModel.postFillConcerts(true)
        }
        else
            viewModel.postFillConcerts(false)
        (activity as MainActivity).hideProgress()
    }

    private val concertiStranieriObserver = Observer<ArrayList<Concerto?>?> {
        if (!it.isNullOrEmpty()) {
            viewModel.concerts.value?.addAll(it)
            viewModel.postFillConcerts(true)
        }
        else
            viewModel.postFillConcerts(false)
        (activity as MainActivity).hideProgress()
    }

}