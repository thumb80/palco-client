package it.antonino.palco.util

import com.google.gson.JsonElement
import it.antonino.palco.model.Concerto
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*

class PalcoUtils {

    companion object {

        fun getDateTime(time: String): Date? {
            val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
            val calendar = Calendar.getInstance()
            calendar.time = insdf.parse(time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            return calendar.time
        }

        fun checkObject(concerto: Concerto?): Boolean {
            return concerto?.getArtist().isNullOrEmpty()
                    || concerto?.getCity().isNullOrEmpty()
                    || concerto?.getPlace().isNullOrEmpty()
                    || concerto?.getTime().isNullOrEmpty() || compareDate(concerto?.getTime() ?: "")
        }

        fun checkObject(concerto: JsonElement): Boolean {
            return (concerto.asJsonObject?.get("artist")?.asString?.isEmpty() == true
                    || concerto.asJsonObject?.get("place")?.asString?.isEmpty() == true
                    || concerto.asJsonObject?.get("city")?.asString?.isEmpty() == true
                    || concerto.asJsonObject?.get("time")?.asString?.isEmpty() == true) || compareDate(concerto.asJsonObject?.get("time")?.asString ?: "")
        }

        fun compareDate(date: String): Boolean {
            val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
            val calendar = Calendar.getInstance()
            calendar.time = insdf.parse(date) as Date
            return calendar.time.before(DateTimeUtils.toDate(Instant.now().minusMillis(86400000)))
        }

        fun compareLastDayOfMonth(date: String): Boolean {
            val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
            val calendar = Calendar.getInstance()
            val calendarLastDayOfMonth = Calendar.getInstance()

            calendar.time = insdf.parse(date) as Date
            calendarLastDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
            calendarLastDayOfMonth.set(Calendar.MONTH, calendar.time.month + 2)
            calendarLastDayOfMonth.set(Calendar.YEAR, calendar.time.year)
            return calendar.time.equals(DateTimeUtils.toDate(Instant.ofEpochMilli(calendarLastDayOfMonth.timeInMillis)))
        }

    }

}