package it.antonino.palco.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.model.CustomFilterCityAdapter
import it.antonino.palco.databinding.FragmentFilterCityBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.setAccessibility
import it.antonino.palco.ext.toPx
import it.antonino.palco.model.CityListAdapter
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class FilterCityFragment : Fragment() {


    val viewModel: SharedViewModel by activityViewModel<SharedViewModel>()
    private var cityList = ArrayList<String>()
    private var cityAdapter: CityListAdapter? = null
    private var adapter: CustomFilterCityAdapter? = null
    private lateinit var binding: FragmentFilterCityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterCityBinding.inflate(layoutInflater)
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getAllCities()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cities.observe(viewLifecycleOwner, cityObserver)

        hideConcerti()

        val linearLayout = binding.searchBar.getChildAt(0) as LinearLayout
        val linearLayout2 = linearLayout.getChildAt(2) as LinearLayout
        val linearLayout3 = linearLayout2.getChildAt(1) as LinearLayout
        val autoCompleteTextView = linearLayout3.getChildAt(0) as AutoCompleteTextView

        autoCompleteTextView.textSize = 42f

        val editText = binding.searchBar.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.weight = 16f

        editText.layoutParams = layoutParams
        editText.gravity = Gravity.CENTER
        editText.setHintTextColor(requireContext().getColor(R.color.colorWhite))
        editText.setTextColor(requireContext().getColor(R.color.colorWhite))

        binding.searchBar.queryHint = getString(R.string.search_city)
        binding.searchBar.setIconifiedByDefault(false)
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

    private val cityObserver = Observer<ArrayList<String>> {

        when (it.isNotEmpty()) {
            true -> {
                for (city in it) {
                    if (!cityList.contains(city))
                        cityList.add(city)
                }
                cityList.sortBy { it }
                cityAdapter = CityListAdapter(cityList) {
                    binding.filterHeaderCity.visibility = View.VISIBLE
                    binding.filterHeaderCityReset.visibility = View.VISIBLE
                    binding.filterHeaderCity.text = getString(R.string.filter_city_selected, org.apache.commons.lang3.StringEscapeUtils.unescapeJava(it))
                    binding.filterHeaderCityReset.text = getString(R.string.filter_city_reset)
                    GlobalScope.launch(Dispatchers.Main) {
                        viewModel.getAllByCity(it)
                    }
                    viewModel.concertiFilterCity.observe(viewLifecycleOwner, concertsObserver)
                }
                val layoutManager = LinearLayoutManager(
                    context,
                    RecyclerView.VERTICAL,
                    false)
                binding.filterCityList.layoutManager = layoutManager
                binding.filterCityList.adapter = cityAdapter

                binding.filterCityList.setAccessibility()
            }
            else -> {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private val concertsObserver = Observer<ArrayList<Concerto>> {

        when(it.isNotEmpty()) {
            true -> {
                showConcerti()

                val concerti = it.sortedBy { item -> item.time }
                val sortedByDateItems: ArrayList<Concerto?> = arrayListOf()
                concerti.forEach {
                    sortedByDateItems.add(it)
                }
                adapter = CustomFilterCityAdapter(
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
                binding.filterConcertCityList.setAccessibility()

                binding.filterHeaderCity.setOnClickListener {
                    hideConcerti()
                    binding.filterHeaderCity.visibility = View.GONE
                    binding.filterHeaderCityReset.visibility = View.GONE
                }

            }
            else -> {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showConcerti() {
        binding.filterCityList.visibility = View.GONE
        binding.filterHeaderCityContainer.visibility = View.GONE
        binding.filterConcertCityList.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        binding.filterCityList.visibility = View.VISIBLE
        binding.filterHeaderCityContainer.visibility = View.VISIBLE
        binding.filterConcertCityList.visibility = View.GONE
    }
}
