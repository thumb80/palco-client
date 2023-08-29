package it.antonino.palco.ui.filter.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.adapter.CustomFilterAdapter
import it.antonino.palco.databinding.FragmentFilterCityBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.collections.ArrayList

class FilterCityFragment : Fragment() {

    private val viewModel: SharedViewModel by sharedViewModel()
    private var cityList = ArrayList<String>()
    private var cityAdapter: CityListAdapter? = null
    private var adapter: CustomFilterAdapter? = null
    private lateinit var binding: FragmentFilterCityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterCityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNationalCities().observe(viewLifecycleOwner, cityObserver)

        hideConcerti()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

                    binding.filterHeaderCity.text = getString(R.string.filter_city_selected, it)
                    (activity as MainActivity).progressBar?.visibility = View.VISIBLE
                    viewModel.getNationalConcertsByCity(it).observe(viewLifecycleOwner, concertsObserver)
                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                binding.filterCityList.layoutManager = layoutManager
                binding.filterCityList.adapter = cityAdapter
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

                val concerti = it.sortedBy { item -> item?.getTime() }
                val sortedByDateItems: ArrayList<Concerto?> = arrayListOf()
                concerti.forEach {
                    sortedByDateItems.add(it)
                }
                adapter = CustomFilterAdapter(
                    sortedByDateItems
                ) { concertRow ->
                    val dialog = CustomDialog(concertRow)
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
                binding.filterConcertCityList.layoutManager = layoutManager
                binding.filterConcertCityList.adapter = adapter
                binding.filterConcertCityList.addItemDecoration(dividerItemDecoration)

                binding.filterHeaderCity.setOnClickListener {
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
        binding.filterCityList.visibility = View.GONE
        binding.searchBar.visibility = View.GONE
        binding.filterHeaderCity.visibility = View.VISIBLE
        binding.filterHeaderCity.background = AppCompatResources.getDrawable(requireContext(), R.drawable.edit_background_grey)
        binding.filterConcertCityList.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        binding.filterCityList.visibility = View.VISIBLE
        binding.searchBar.visibility = View.VISIBLE
        binding.filterHeaderCity.visibility = View.GONE
        binding.filterConcertCityList.visibility = View.GONE
    }
}
