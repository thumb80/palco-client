package it.antonino.palco.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import it.antonino.palco.PalcoApplication.Companion.concerti
import it.antonino.palco.PalcoApplication.Companion.file
import it.antonino.palco.model.Concerto
import it.antonino.palco.workers.ScrapeGothWorker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class SharedViewModelTest: KoinTest {

    private lateinit var context: Context
    private val viewModel: SharedViewModel by inject()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        concerti = arrayListOf()
        val worker = TestListenableWorkerBuilder<ScrapeGothWorker>(context).build()
        runBlocking {
            worker.doWork()
        }
    }

    @Test
    fun getByArtistsTest() {
        assertNotNull(viewModel.getAllByArtist(concerti[0].artist))
    }

    @Test
    fun getByCityTest() {
        assertNotNull(viewModel.getAllByCity(concerti[0].city))
    }

    @Test
    fun getAllConcertiTest() {
        assertNotNull(viewModel.getAllConcerti())
    }

    @Test
    fun getAllArtistsTest() {
        assertNotNull(viewModel.getAllArtist())
    }

    @Test
    fun getAllCitiesTest() {
        assertNotNull(viewModel.getAllCities())
    }

    @Test
    fun containsConcertoTest() {
        assert(viewModel.containsSpecificJsonValues(file, Concerto("Test", "Test","Test", "Test")))
    }

    @Test
    fun getArtistInfoTest() {
        assertNotNull(viewModel.getArtistInfos("Bob Marley"))
    }

}