package it.antonino.palco.ui.filters.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.adapter.MixedListAdapter
import it.antonino.palco.model.Artist
import it.antonino.palco.model.Concerto
import it.antonino.palco.ui.filters.FiltersViewModel
import kotlinx.android.synthetic.main.fragment_filter_artist.*
import kotlinx.android.synthetic.main.fragment_filter_month.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersArtistFragment : Fragment() {

    private val viewModel: FiltersViewModel by viewModel()
    private var artistsAdapter: CityListAdapter? = null
    private var artistMixedAdapter: MixedListAdapter? = null
    private var artists: ArrayList<Pair<String, Int>> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getNationalArtists().observe(viewLifecycleOwner, artistNazionaleObserver)
        viewModel.getStranieriArtists().observe(viewLifecycleOwner, artistEsteroObserver)

        return inflater.inflate(R.layout.fragment_filter_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)

        if (viewModel.artists.value == true) {
            artistMixedAdapter = MixedListAdapter(artists, {
                viewModel.getConcertiNazionaliByArtist(it)
                    .observe(viewLifecycleOwner, concertiNazionaliObserver)
            }, {
                viewModel.getConcertiStranieriByArtist(it)
                    .observe(viewLifecycleOwner, concertiStranieriObserver)
            })
            artist_recycler.layoutManager = layoutManager
            artist_recycler.adapter = artistMixedAdapter
        }

    }

    private val artistNazionaleObserver = Observer<ArrayList<Artist?>?> {
        if (!it.isNullOrEmpty()) {
            it.forEach {
                it?.getArtist()?.let { artist ->
                    if (artists.contains(Pair(artist, 0)))
                        artists.add(Pair(artist, 0))
                }
            }
        }
    }

    private val artistEsteroObserver = Observer<ArrayList<Artist?>?> {
        if (!it.isNullOrEmpty()) {
            it.forEach {
                it?.getArtist()?.let {
                        artist ->
                    if (artists.contains(Pair(artist, 1)))
                        artists.add(Pair(artist, 1))
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