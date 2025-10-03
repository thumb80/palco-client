package it.antonino.palco.ext

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.Instant
import java.util.Date
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PalcoExtensionTest: KoinTest {

    private lateinit var dateString: String
    private lateinit var time: String
    private lateinit var date: Date
    private lateinit var actualDate: Date
    private var wrongFormatDate: Date? = null
    private var pixel: Int = 1
    private lateinit var sharedPreferences: SharedPreferences
    private val context: Context by inject()
    private lateinit var recyclerView: RecyclerView

    @Before
    fun setUp() {
        dateString = "2025-02-12 00:00:00"
        time = "2025-02-12"
        date = Date.from(Instant.parse("2025-02-12T00:00:00.00Z"))
        actualDate = Date.from(Instant.now())
        sharedPreferences = context.getSharedPreferences("test_shared", Context.MODE_PRIVATE)
        recyclerView = RecyclerView(context)

    }

    @Test
    fun getDateTest() {
        assertNotNull(dateString.getDate())
    }

    /*@Test
    fun getDateExceptionTest() {
        assertNull(time.getDate())
    }*/

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
        assert(pixel.toPx() != 0)
    }

    @Test
    fun getSharedTest() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(getShared(appContext))
    }

    @Test
    fun setAccessibilityTest() {
        recyclerView.setAccessibility()
        assertNotNull(recyclerView.accessibilityDelegate)
    }
    
}