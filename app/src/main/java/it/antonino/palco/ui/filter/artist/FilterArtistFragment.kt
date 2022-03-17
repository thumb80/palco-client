package it.antonino.palco.ui.filter.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.R
import it.antonino.palco.adapter.CityListAdapter
import it.antonino.palco.model.Artist
import it.antonino.palco.ui.filter.FilterViewModel
import kotlinx.android.synthetic.main.filter_artist_fragment.*
import kotlinx.android.synthetic.main.filter_city_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterArtistFragment : Fragment() {

    private val viewModel: FilterViewModel by viewModel()
    private var artistList = ArrayList<String>()
    private var artistAdapter: CityListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_artist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getNationalArtists().observe(viewLifecycleOwner, artistObserver)
        viewModel.getInternationalArtists().observe(viewLifecycleOwner, artistObserver)

    }

    private val artistObserver = Observer<ArrayList<Artist?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {
                for (artist in it) {
                    if (!artistList.contains(artist?.getArtist()))
                        artist?.getArtist()?.let { it_artist -> artistList.add(it_artist) }
                }
                artistList.sortBy { it }
                artistAdapter = CityListAdapter(artistList) {

                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                filter_artist_list.layoutManager = layoutManager
                filter_artist_list.adapter = artistAdapter
            }
        }

    }

}