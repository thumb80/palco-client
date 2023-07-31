package it.antonino.palco.ui.filter.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.MainActivity
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.adapter.CustomFilterAdapter
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.checkObject
import it.antonino.palco.ext.getDateFromString
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.filter_city_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterCityFragment : Fragment() {

    private val viewModel: SharedViewModel by sharedViewModel()
    private var cityList = ArrayList<String>()
    private var cityAdapter: CityListAdapter? = null
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
        return inflater.inflate(R.layout.filter_city_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNationalCities().observe(viewLifecycleOwner, cityObserver)

        hideConcerti()

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                cityAdapter?.filter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                cityAdapter?.filter?.filter(newText)
                return true
            }

        })

    }

    private val cityObserver = Observer<ArrayList<String?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {
                for (city in it) {
                    if (!cityList.contains(city))
                        city?.let { it_city -> cityList.add(it_city) }
                }
                cityList.sortBy { it }
                cityAdapter = CityListAdapter(cityList) {

                    artisti = arrayListOf()
                    places = arrayListOf()
                    cities = arrayListOf()
                    times = arrayListOf()

                    filter_header_city.text = getString(R.string.filter_city_selected, it)
                    (activity as MainActivity).progressBar?.visibility = View.VISIBLE
                    viewModel.getNationalConcertsByCity(it).observe(viewLifecycleOwner, concertsObserver)
                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                filter_city_list.layoutManager = layoutManager
                filter_city_list.adapter = cityAdapter
            }
            else -> {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private val concertsObserver = Observer<ArrayList<Concerto?>?> {

        when(!it.isNullOrEmpty()) {
            true -> {
                (activity as MainActivity).progressBar?.visibility = View.INVISIBLE
                showConcerti()

                for (concerto in it) {
                    if (concerto?.checkObject() == false) {
                        artisti.add(concerto.getArtist())
                        places.add(concerto.getPlace())
                        cities.add(concerto.getCity())
                        concerto.getTime().substringBefore(" ").getDateFromString()?.let { dateFromString ->
                            times.add(
                                dateFromString
                            )
                        }
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
                filter_concert_city_list.layoutManager = layoutManager
                filter_concert_city_list.adapter = adapter
                filter_concert_city_list.addItemDecoration(dividerItemDecoration)

                filter_header_city.setOnClickListener {
                    hideConcerti()
                }

            }
            else -> {
                (activity as MainActivity).progressBar?.visibility = View.INVISIBLE
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showConcerti() {
        filter_city_list.visibility = View.GONE
        search_bar.visibility = View.GONE
        filter_header_city.visibility = View.VISIBLE
        filter_concert_city_list.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        filter_city_list.visibility = View.VISIBLE
        search_bar.visibility = View.VISIBLE
        filter_header_city.visibility = View.GONE
        filter_concert_city_list.visibility = View.GONE
    }

}
