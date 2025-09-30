package it.antonino.palco.model

import androidx.fragment.app.FragmentActivity
import it.antonino.palco.PalcoApplication
import it.antonino.palco.di.appModule
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
class ConcertsAdapterTest {

    @Mock
    private lateinit var fragmentActivity: FragmentActivity
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
    fun concertsAdapterTest() {
        val concertsAdapter = ConcertsAdapter(fragmentActivity)
        assert(concertsAdapter.itemCount == 2)
    }

}