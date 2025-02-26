package it.antonino.palco.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import it.antonino.palco.PalcoApplication
import it.antonino.palco.di.appModule
import it.antonino.palco.model.Concerto
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertNotNull

@RunWith(MockitoJUnitRunner::class)
class SharedViewModelTest: KoinTest {

    @Mock
    private lateinit var context: PalcoApplication
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val viewModel: SharedViewModel by inject()

    @Before
    fun setUp() {
        startKoin {
            androidContext(context)
            modules(appModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun setIsNewDayTest() {
        viewModel.setIsNewDay(true)
        viewModel.isNewDay.value?.let { assert(it) }
    }

    @Test
    fun setBatchEndedTest() {
        viewModel.setBatchEnded(true)
        viewModel.batchEnded.value?.let { assert(it) }
    }

    @Test
    fun setConcertiTest() {
        val concerti = arrayListOf(Concerto("Test", "Test", "Test", "Test"))
        viewModel.setConcerti(concerti)
        assertNotNull(viewModel.concerti.value)
    }
}