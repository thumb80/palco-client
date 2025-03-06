package it.antonino.palco.ext

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.time.Instant
import java.util.Date
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)
class PalcoExtensionTest {

    private lateinit var dateString: String
    private lateinit var time: String
    private lateinit var date: Date
    private lateinit var actualDate: Date
    private var wrongFormatDate: Date? = null
    private var pixel: Int = 1
    @Mock
    private lateinit var context: PalcoApplication
    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dateString = "2025-02-12 00:00:00"
        time = "2025-02-12"
        date = Date.from(Instant.parse("2025-02-12T00:00:00.00Z"))
        actualDate = Date.from(Instant.now())
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
    fun getDateTest() {
        assertNotNull(dateString.getDate())
    }

    @Test
    fun getTimeTest() {
        assertNotNull(time.getTime())
    }

    @Test
    fun compareDateTest() {
        assert(date.compareDate())
    }

    @Test
    fun isActualMonthTest() {
        assert(actualDate.isActualMonth() == true)
    }

    @Test
    fun isActualMonthExceptionTest() {
        assertNull(wrongFormatDate.isActualMonth())
    }

    @Test
    fun getStringTest() {
        assert(date.getString() is String)
    }

    @Test
    fun toPxTest() {
        val resources = mock(Resources::class.java)
        mockStatic(Resources::class.java) { mockStatic ->
            `when`((Resources::getSystem)()).thenReturn(resources)
            assert(pixel.toPx() != 0)
        }
    }

    /*@Test
    fun getSharedTest() {
        mockStatic(Context::class.java) {
            `when`((context::getApplicationContext)())
                .thenReturn(mockContext)

        }
        val sharedPreferences = mock(SharedPreferences::class.java)
        mockStatic(SharedPreferences::class.java) {
            `when`((context::getSharedPreferences)("test_shared", Context.MODE_PRIVATE))
                .thenReturn(sharedPreferences)
        }
        assertNotNull(sharedPreferences.getShared(mockContext))
    }*/

    /*@Test
    fun setAccessibilityTest() {
        val mockRecycler = mock(RecyclerView::class.java)
        mockStatic(RecyclerView::class.java) {
            `when`(RecyclerView(context))
                .thenReturn(mockRecycler)
        }
        mockRecycler.adapter = recyclerAdapter


        mockRecycler.setAccessibility()
        assertNotNull(mockRecycler.accessibilityDelegate)
    }*/

}