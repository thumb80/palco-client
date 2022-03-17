package it.antonino.palco.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import it.antonino.palco.BuildConfig
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomAdapter
import it.antonino.palco.adapter.FiltersAdapter
import it.antonino.palco.common.CustomSnapHelper
import it.antonino.palco.common.DotsItemDecoration
import it.antonino.palco.databinding.FragmentFiltersBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.CustomViewPager
import it.antonino.palco.model.Concerto
import kotlinx.android.synthetic.main.fragment_filters.*
import kotlinx.android.synthetic.main.fragment_national.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersFragment : Fragment() {

    private val viewModel: FiltersViewModel by viewModel()

    private var adapter: CustomAdapter? = null

    private var concertiLayoutManager: LinearLayoutManager? = null
    private var position : Int? = null
    private var dotsItemDecoration: DotsItemDecoration? = null

    private var _view: View? = null

    private lateinit var binding: FragmentFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFiltersBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner {
            lifecycle
        }
        _view = binding.root

        dotsItemDecoration = DotsItemDecoration(
            resources.getDimension(R.dimen.dp_4).toInt(),
            resources.getDimension(R.dimen.dp_6).toInt(),
            resources.getDimension(R.dimen.dp_8).toInt(),
            ResourcesCompat.getColor(resources, R.color.colorWhite, null),
            ResourcesCompat.getColor(resources, R.color.colorAccent, null)
        )

        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewPager = view.findViewById<CustomViewPager>(R.id.filters_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.filters_tab_layout)
        val concertiViewPagerAdapter = FiltersAdapter(childFragmentManager)

        viewPager.setPagingEnabled(false)
        viewPager.adapter = concertiViewPagerAdapter
        viewPager.setCurrentItem(0,true)
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager, true)

        binding.viewModel?.uiState?.value?.isFilledConcerts

    }

    private val observer = Observer<Boolean> {
        if (it) {
            binding.noDataFilters.visibility = View.GONE
            val dividerItemDecoration = DividerItemDecoration(
                context,
                LinearLayoutManager.HORIZONTAL
            )
            dividerItemDecoration.setDrawable(resources?.getDrawable(R.drawable.card_view_divider)!!)

            concertiLayoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )


            val snapHelper = CustomSnapHelper()
            snapHelper.attachToRecyclerView(concerti_recycler)

            binding.concertiFiltersRecycler.setHasFixedSize(true)
            binding.concertiFiltersRecycler.addItemDecoration(dividerItemDecoration)
            dotsItemDecoration?.let { concerti_recycler?.addItemDecoration(it) }

            binding.concertiFiltersRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val view = snapHelper.findSnapView(recyclerView.layoutManager)
                    val cardContainer = view?.findViewById<LinearLayout>(R.id.cardContainer)
                    position = view?.let { recyclerView.getChildAdapterPosition(it) }
                    position?.let {
                        adapter?.setSelectedItem(it)
                    }

                }
            })

            val artisti: ArrayList<String> = ArrayList(viewModel.concertsSize ?: 0)
            val places: ArrayList<String> = ArrayList(viewModel.concertsSize ?: 0)
            val cities: ArrayList<String> = ArrayList(viewModel.concertsSize ?: 0)
            val times: ArrayList<String> = ArrayList(viewModel.concertsSize ?: 0)

            //TODO fill concerti
            viewModel.concerts.value?.forEach {
                it?.getArtist()?.let { artist -> artisti.add(artist) }
                it?.getCity()?.let { city -> cities.add(city) }
                it?.getPlace()?.let { place -> places.add(place) }
                it?.getTime()?.let { time -> times.add(time) }
            }

            adapter = CustomAdapter(artisti, places, cities, times) { concertRow ->

                if (!BuildConfig.BUY_TICKET) {
                    val dialog = CustomDialog(concertRow)
                    dialog.show(childFragmentManager,null)
                }
                else {
                    //val dialog = CustomDialog(concertRow)
                    //dialog.show(childFragmentManager,null)
                    Toast.makeText(context, "Ops c'è stato un problema", Toast.LENGTH_LONG).show()
                }

            }
            binding.concertiFiltersRecycler.layoutManager = concertiLayoutManager
            binding.concertiFiltersRecycler.adapter = adapter
        }
        else
            binding.noDataFilters.visibility = View.VISIBLE
    }

}