package it.antonino.palco.viewmodel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import it.antonino.palco.workers.SecondBatchWorker
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
        val worker = TestListenableWorkerBuilder<SecondBatchWorker>(context).build()
        runBlocking {
            worker.doWork()
        }
    }

    @Test
    fun getByArtistsTest() {
        assert(viewModel.getAllByArtist(context, "test").isEmpty())
    }

    @Test
    fun getByCityTest() {
        assert(viewModel.getAllByCity(context, "test").isEmpty())
    }

    @Test
    fun getAllConcertsTest() {
        assertNotNull(viewModel.getAllConcerts(context))
    }

    @Test
    fun getAllArtistsTest() {
        assertNotNull(viewModel.getAllArtist(context))
    }

    @Test
    fun getAllCitiesTest() {
        assertNotNull(viewModel.getAllCities(context))
    }

    /*@Test
    fun containsConcertoTest() {
        assert(viewModel.containsSpecificJsonValues(file, Concerto("Test", "Test","Test", "Test")))
    }*/

    @Test
    fun getArtistInfoTest() {
        assertNotNull(viewModel.getArtistInfos("Bob Marley"))
    }

}