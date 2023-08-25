package it.antonino.palco.ext

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.Instant
import java.util.Date

@RunWith(MockitoJUnitRunner::class)
class PalcoExtensionTest {

    val oldDateLong = 1693000076L
    val dateString = "Ago 26, 2023"

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

    @Test
    fun testisNotActualMonth() {
        val oldDate = Date(oldDateLong)
        assert(oldDate.isActualMonth() == false)
    }

    @Test
    fun testisActualMonth() {
        val actualDate = Date.from(Instant.now())
        assert(actualDate.isActualMonth() == true)
    }

    @Test
    fun testGetString() {
        val date  = Date.from(Instant.now())
        assert(date.getString() is String)
    }

    @Test
    fun testGetDate() {
        assert(dateString.getDate() is Date)
    }

}