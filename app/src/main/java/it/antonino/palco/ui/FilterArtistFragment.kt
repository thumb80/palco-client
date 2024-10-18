package it.antonino.palco.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.R
import it.antonino.palco.adapter.CustomFilterArtistAdapter
import it.antonino.palco.databinding.FragmentFilterArtistBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.setAccessibility
import it.antonino.palco.ext.toPx
import it.antonino.palco.model.ArtistListAdapter
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FilterArtistFragment : Fragment() {


    val viewModel: SharedViewModel by activityViewModel<SharedViewModel>()
    private var artistAdapter: ArtistListAdapter? = null
    private var adapter: CustomFilterArtistAdapter? = null
    private lateinit var binding: FragmentFilterArtistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterArtistBinding.inflate(layoutInflater)
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getAllArtist()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.artists.observe(viewLifecycleOwner, artistObserver)

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
        editText.gravity = Gravity.CENTER
        editText.setHintTextColor(requireContext().getColor(R.color.colorWhite))
        editText.setTextColor(requireContext().getColor(R.color.colorWhite))

        binding.searchBar.queryHint = getString(R.string.search_artist)
        binding.searchBar.setIconifiedByDefault(false)
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                artistAdapter?.filter?.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                artistAdapter?.filter?.filter(newText)
                return true
            }
        })
    }

    private val artistObserver = Observer<ArrayList<String>> {

        when (it.isNotEmpty()) {
            true -> {

                it.sortBy { element-> element }

                artistAdapter = ArtistListAdapter(it as kotlin.collections.ArrayList) { artist ->
                    binding.filterHeaderArtist.visibility = View.VISIBLE
                    binding.filterHeaderArtistReset.visibility = View.VISIBLE
                    binding.filterHeaderArtist.text = getString(R.string.filter_artist_selected, org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist))
                    binding.filterHeaderArtistReset.text = getString(R.string.filter_artist_reset)
                    GlobalScope.launch(Dispatchers.Main) {
                        viewModel.getAllByArtist(artist)
                    }
                    viewModel.concertiFilterArtist.observe(viewLifecycleOwner, concertsObserver)
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

    private val concertsObserver = Observer<ArrayList<Concerto>> {

        when(it.isNotEmpty()) {
            true -> {
                showConcerti()

                val concerti = it.sortedBy { item -> item.time }
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
                    binding.filterHeaderArtistReset.visibility = View.GONE
                }

            }
            else -> {
                binding.filterHeaderArtist.visibility = View.GONE
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
