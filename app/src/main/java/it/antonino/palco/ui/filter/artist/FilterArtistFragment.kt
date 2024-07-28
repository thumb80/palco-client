package it.antonino.palco.ui.filter.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.AccessibilityDelegate
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.ArtistListAdapter
import it.antonino.palco.adapter.CustomFilterArtistAdapter
import it.antonino.palco.databinding.FragmentFilterArtistBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.setAccessibility
import it.antonino.palco.ext.toDp
import it.antonino.palco.ext.toPx
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.collections.ArrayList

class FilterArtistFragment : Fragment() {

    private val viewModel: SharedViewModel by sharedViewModel()
    private var artistAdapter: ArtistListAdapter? = null
    private var adapter: CustomFilterArtistAdapter? = null
    private lateinit var binding: FragmentFilterArtistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterArtistBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNationalArtists().observe(viewLifecycleOwner, artistObserver)

        hideConcerti()

        val linearLayout = binding.searchBar.getChildAt(0) as LinearLayout
        val linearLayout2 = linearLayout.getChildAt(2) as LinearLayout
        val linearLayout3 = linearLayout2.getChildAt(1) as LinearLayout
        val autoCompleteTextView = linearLayout3.getChildAt(0) as AutoCompleteTextView

        autoCompleteTextView.textSize = 42f

        val editText = binding.searchBar.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 16f

        editText.layoutParams = layoutParams

        binding.searchBar.queryHint = getString(R.string.search_artist)
        binding.searchBar.setIconifiedByDefault(false)
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutParams.topMargin = resources.getDimension(R.dimen.dp_48).toInt().toPx()
                layoutParams.bottomMargin = resources.getDimension(R.dimen.dp_48).toInt().toPx()
                binding.filterArtistList.layoutParams = layoutParams
                artistAdapter?.filter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                layoutParams.topMargin = resources.getDimension(R.dimen.dp_48).toInt().toPx()
                layoutParams.bottomMargin = resources.getDimension(R.dimen.dp_48).toInt().toPx()
                binding.filterArtistList.layoutParams = layoutParams
                artistAdapter?.filter?.filter(newText)
                return true
            }
        })

        binding.searchBar

    }

    private val artistObserver = Observer<ArrayList<String?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {

                it.sortBy { element-> element }

                artistAdapter = ArtistListAdapter(it) { artist ->
                    binding.filterHeaderArtist.visibility = View.VISIBLE
                    binding.filterHeaderArtist.text = getString(R.string.filter_artist_selected, artist)
                    (activity as MainActivity).progressBar?.visibility = View.VISIBLE
                    viewModel.getNationalConcertsByArtist(artist).observe(viewLifecycleOwner, concertsObserver)
                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                binding.filterArtistList.layoutManager = layoutManager
                binding.filterArtistList.adapter = artistAdapter

                binding.filterArtistList.setAccessibility()
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
                adapter = CustomFilterArtistAdapter(
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
                binding.filterConcertArtistList.layoutManager = layoutManager
                binding.filterConcertArtistList.adapter = adapter
                binding.filterConcertArtistList.addItemDecoration(dividerItemDecoration)
                binding.filterConcertArtistList.setAccessibility()

                binding.filterHeaderArtist.setOnClickListener {
                    hideConcerti()
                    binding.filterHeaderArtist.visibility = View.GONE
                }

            }
            else -> {
                binding.filterHeaderArtist.visibility = View.GONE
                (activity as MainActivity).progressBar?.visibility = View.INVISIBLE
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showConcerti() {
        binding.filterArtistList.visibility = View.GONE
        binding.filterHeaderArtistContainer.visibility = View.GONE
        binding.filterConcertArtistList.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        binding.filterArtistList.visibility = View.VISIBLE
        binding.filterHeaderArtistContainer.visibility = View.VISIBLE
        binding.filterConcertArtistList.visibility = View.GONE
    }

}
