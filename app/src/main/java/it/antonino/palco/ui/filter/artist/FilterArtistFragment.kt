package it.antonino.palco.ui.filter.artist

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
import it.antonino.palco.BuildConfig
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.ArtistListAdapter
import it.antonino.palco.adapter.CustomFilterArtistAdapter
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.model.Artist
import it.antonino.palco.model.Concerto
import it.antonino.palco.ui.viewmodel.SharedViewModel
import it.antonino.palco.util.PalcoUtils
import kotlinx.android.synthetic.main.filter_artist_fragment.search_bar
import kotlinx.android.synthetic.main.filter_artist_fragment.filter_header_artist
import kotlinx.android.synthetic.main.filter_artist_fragment.filter_artist_list
import kotlinx.android.synthetic.main.filter_artist_fragment.filter_concert_artist_list
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterArtistFragment : Fragment() {

    private val viewModel: SharedViewModel by viewModel()
    private var artistList = ArrayList<Artist>()
    private var artistAdapter: ArtistListAdapter? = null
    private var adapter: CustomFilterArtistAdapter? = null
    var artisti = ArrayList<String>()
    var places = ArrayList<String>()
    var times = ArrayList<String>()
    val cities = ArrayList<String>()

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

        hideConcerti()

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private val artistObserver = Observer<ArrayList<Artist?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {
                for (artist in it) {
                    val myArtist = artist?.getArtist()?.split("+")?.get(0)
                    if (checkArtistList(artist = Artist(myArtist!!), artistList = artistList))
                        myArtist.let { it_artist ->
                            artistList.add(Artist(it_artist))
                        }
                }
                artistList.sortBy { it.getArtist() }
                artistAdapter = ArtistListAdapter(artistList) {

                    filter_header_artist.text = getString(R.string.filter_artist_selected, it)

                    viewModel.getNationalConcertsByArtist(it).observe(viewLifecycleOwner, concertsObserver)
                    viewModel.getInternationalConcertsByArtist(it).observe(viewLifecycleOwner, concertsObserver)

                }
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                filter_artist_list.layoutManager = layoutManager
                filter_artist_list.adapter = artistAdapter
            }
            else -> {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private val concertsObserver = Observer<ArrayList<Concerto?>?> {

        when(!it.isNullOrEmpty()) {
            true -> {
                (activity as MainActivity).hideProgress()
                showConcerti()

                for (concerto in it) {
                    if (!PalcoUtils.checkObject(concerto)) {
                        artisti.add(concerto?.getArtist()!!)
                        places.add(concerto.getPlace())
                        cities.add(concerto.getCity())
                        times.add(concerto.getTime())
                    }
                }

                adapter = CustomFilterArtistAdapter(
                    artisti,
                    places,
                    cities,
                    times
                ) {
                    if (!BuildConfig.BUY_TICKET) {
                        val dialog = CustomDialog(it)
                        dialog.show(childFragmentManager,null)
                    }
                    else {
                        Toast.makeText(context, "Ops c'è stato un problema", Toast.LENGTH_LONG).show()
                    }
                }

                val dividerItemDecoration = DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
                val layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
                filter_concert_artist_list.layoutManager = layoutManager
                filter_concert_artist_list.adapter = adapter
                filter_concert_artist_list.addItemDecoration(dividerItemDecoration)

                filter_header_artist.setOnClickListener {
                     hideConcerti()
                }

            }
            else -> {
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun checkArtistList(artist: Artist, artistList: ArrayList<Artist>): Boolean {
        artistList.forEach {
            if (it.getArtist().equals(artist.getArtist(), ignoreCase = true))
                return false
        }
        return true
    }

    private fun showConcerti() {
        filter_artist_list.visibility = View.GONE
        search_bar.visibility = View.GONE
        filter_header_artist.visibility = View.VISIBLE
        filter_concert_artist_list.visibility = View.VISIBLE
    }

    private fun hideConcerti() {
        filter_artist_list.visibility = View.VISIBLE
        search_bar.visibility = View.VISIBLE
        filter_header_artist.visibility = View.GONE
        filter_concert_artist_list.visibility = View.GONE
    }

}
