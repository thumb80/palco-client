package it.antonino.palco.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import it.antonino.palco.PalcoApplication
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class ScrapeWorkersTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        PalcoApplication.concerti = arrayListOf()
    }

    @Test
    fun scrapeGothTest() {
        val worker = TestListenableWorkerBuilder<ScrapeGothWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertNotNull(result)
        }
    }

    @Test
    fun scrapeRockolTest() {
        val worker = TestListenableWorkerBuilder<ScrapeRockolWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertNotNull(result)
        }
    }

}