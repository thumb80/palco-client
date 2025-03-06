package it.antonino.palco.model

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ArtistListAdapterTest {

    val artistListAdapter = ArtistListAdapter(
        arrayListOf("Vasco Rossi","Francesco Guccini","Giorgia"), {

        }
    )

    @Test
    fun artistListAdapterTest() {
        assert(!artistListAdapter.artists.isEmpty())
        assert(artistListAdapter.artistsFiltered == null)
        assert(artistListAdapter.itemCount == 3)
    }

}