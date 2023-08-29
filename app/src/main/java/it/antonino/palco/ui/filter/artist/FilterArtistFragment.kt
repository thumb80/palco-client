package it.antonino.palco.ui.filter.artist

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
import it.antonino.palco.adapter.ArtistListAdapter
import it.antonino.palco.adapter.CustomFilterArtistAdapter
import it.antonino.palco.databinding.FragmentFilterArtistBinding
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
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

    private val artistObserver = Observer<ArrayList<String?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {

                it.sortBy { element-> element?.strip() }

                artistAdapter = ArtistListAdapter(it) { artist ->

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

                adapter = CustomFilterArtistAdapter(
                    it
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

                binding.filterHeaderArtist.setOnClickListener {
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
        binding.filterArtistList.visibility = View.GONE
        binding.searchBar.visibility = View.GONE
        binding.filterHeaderArtist.visibility = View.VISIBLE
        binding.filterHeaderArtist.background = AppCompatResources.getDrawable(requireContext(), R.drawable.edit_background_grey)
        binding.filterConcertArtistList.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        binding.filterArtistList.visibility = View.VISIBLE
        binding.searchBar.visibility = View.VISIBLE
        binding.filterHeaderArtist.visibility = View.GONE
        binding.filterConcertArtistList.visibility = View.GONE
    }

}
