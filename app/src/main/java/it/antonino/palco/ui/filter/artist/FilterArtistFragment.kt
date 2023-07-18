package it.antonino.palco.ui.filter.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.adapter.ArtistListAdapter
import it.antonino.palco.adapter.CustomFilterArtistAdapter
import it.antonino.palco.ext.CustomDialog
import it.antonino.palco.ext.checkObject
import it.antonino.palco.ext.getDateString
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import it.antonino.palco.util.PalcoUtils
import kotlinx.android.synthetic.main.filter_artist_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterArtistFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private var artistAdapter: ArtistListAdapter? = null
    private var adapter: CustomFilterArtistAdapter? = null

    var artisti = ArrayList<String>()
    var places = ArrayList<String>()
    var times = ArrayList<String>()
    var cities = ArrayList<String>()

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

    private val artistObserver = Observer<ArrayList<String?>?> {

        when (!it.isNullOrEmpty()) {
            true -> {

                it.sortBy { element-> element?.strip() }

                artistAdapter = ArtistListAdapter(it) { artist ->

                    artisti = arrayListOf()
                    places = arrayListOf()
                    cities = arrayListOf()
                    times = arrayListOf()

                    filter_header_artist.text = getString(R.string.filter_artist_selected, artist)
                    viewModel.getNationalConcertsByArtist(artist).observe(viewLifecycleOwner, concertsObserver)
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
                showConcerti()

                for (concerto in it) {
                    if (concerto?.checkObject() == false) {
                        artisti.add(concerto.getArtist())
                        places.add(concerto.getPlace())
                        cities.add(concerto.getCity())
                        times.add(
                            PalcoUtils.getDateTimeString(
                                concerto.getTime().substringBefore(" ")
                            )
                        )
                    }
                }

                adapter = CustomFilterArtistAdapter(
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
