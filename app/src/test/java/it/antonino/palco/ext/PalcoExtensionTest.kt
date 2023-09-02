package it.antonino.palco.ext

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.Instant
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PalcoExtensionTest {

    val oldDateLong = 1693000076L

    @Test
    fun testCompareDateBefore() {
        val oldDate = Date(oldDateLong)
        assert(oldDate.compareDate())
    }

    @Test
    fun testCompareDateActually() {
        val date = Date.from(Instant.now())
        assert(!date.compareDate())
    }
}