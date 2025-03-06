package it.antonino.palco.model

import androidx.fragment.app.FragmentManager
import it.antonino.palco.PalcoApplication
import it.antonino.palco.di.appModule
import it.antonino.palco.ui.EventsFragment
import it.antonino.palco.ui.FilterFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConcertiAdapterTest {

    @Mock
    private lateinit var fragmentManager: FragmentManager
    @Mock
    private lateinit var context: PalcoApplication

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
    fun concertiAdapterTest() {
        val concertiAdapter = ConcertiAdapter(fragmentManager)
        assert(concertiAdapter.count == 2)
        assert(concertiAdapter.getItem(0) is EventsFragment)
        assert(concertiAdapter.getItem(1) is FilterFragment)
        //assert(concertiAdapter.getPageTitle(0) == "Eventi")
        //assert(concertiAdapter.getPageTitle(1) == "Filtri")
    }

}