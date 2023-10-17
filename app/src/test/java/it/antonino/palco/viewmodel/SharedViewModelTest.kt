package it.antonino.palco.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Date

@RunWith(MockitoJUnitRunner::class)
class SharedViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var networkAPI: NetworkAPI
    @Mock
    private lateinit var discogsAPI: DiscogsAPI
    @Mock
    private lateinit var wikiPediaAPI: WikiPediaAPI
    @Mock
    private lateinit var networkRepository: NetworkRepository
    @Mock
    private lateinit var sharedViewModel: SharedViewModel


    @Before
    fun setUp() {
        networkRepository = NetworkRepository(
            networkAPI, discogsAPI, wikiPediaAPI
        )
        sharedViewModel = SharedViewModel(networkRepository)
    }

    @Test
    fun testEmptyEvents() {
        sharedViewModel.setConcerti(null)
        assert(sharedViewModel.concerti.value == null)
    }

    @Test
    fun testFakeEvents() {
        val events = arrayListOf<Concerto?>()
        for (i in 0 until 10) {
            events.add(
                Concerto(
                artist = "Concerto$i",
                place = "Place$i",
                city = "City$i",
                time = Date(i.toLong())
                )
            )
        }
        sharedViewModel.setConcerti(events)
        for (i in 0 until  10) {
            assert(sharedViewModel.concerti.value?.contains(
                Concerto(
                    artist = "Concerto$i",
                    place = "Place$i",
                    city = "City$i",
                    time = Date(i.toLong())
                )
            ) == true)
        }
    }

}