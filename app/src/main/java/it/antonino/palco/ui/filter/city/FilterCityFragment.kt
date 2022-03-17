package it.antonino.palco.ui.filter.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.model.City
import it.antonino.palco.ui.filter.FilterViewModel
import kotlinx.android.synthetic.main.filter_city_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterCityFragment : Fragment() {

    private val viewModel: FilterViewModel by viewModel()
    private var cityList = ArrayList<String>()
    private var cityAdapter: CityListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_city_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNationalCities().observe(viewLifecycleOwner, cityObserver)
        viewModel.getInternationalCities().observe(viewLifecycleOwner, cityObserver)

    }

    private val cityObserver = Observer<ArrayList<City?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {
                for (city in it) {
                    if (!cityList.contains(city?.getCity()))
                        city?.getCity()?.let { it_city -> cityList.add(it_city) }
                }
                cityList.sortBy { it }
                cityAdapter = CityListAdapter(cityList) {

                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                filter_city_list.layoutManager = layoutManager
                filter_city_list.adapter = cityAdapter
            }
        }

    }


}