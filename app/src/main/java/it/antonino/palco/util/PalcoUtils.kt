package it.antonino.palco.util

import com.google.gson.JsonElement
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.offsetDayMillis
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

object PalcoUtils {

    fun getDateTime(time: String): Date? {
        val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        val calendar = Calendar.getInstance()
        calendar.time = insdf.parse(time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return calendar.time
    }

    fun getDateTimeString(time: String): String {
        val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        val outsdf = SimpleDateFormat("EEEE dd MMMM y", Locale.ITALY)
        val calendar = Calendar.getInstance()
        calendar.time = insdf.parse(time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return outsdf.format(calendar.time)
    }

    fun checkObject(concerto: Concerto?): Boolean {
        return concerto?.getArtist().isNullOrEmpty()
                || concerto?.getCity().isNullOrEmpty()
                || concerto?.getPlace().isNullOrEmpty()
                || concerto?.getTime().isNullOrEmpty()
                || compareDate(concerto?.getTime() ?: "")
    }

    fun checkObject(concerto: JsonElement): Boolean {
        return (concerto.asJsonObject?.get("artist")?.asString?.isEmpty() == true
                || concerto.asJsonObject?.get("place")?.asString?.isEmpty() == true
                || concerto.asJsonObject?.get("city")?.asString?.isEmpty() == true
                || concerto.asJsonObject?.get("time")?.asString?.isEmpty() == true)
                || compareDate(concerto.asJsonObject?.get("time")?.asString ?: "")
    }

    fun compareDate(date: String): Boolean {
        val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        val calendar = Calendar.getInstance()
        calendar.time = insdf.parse(date) as Date
        return calendar.time.before(DateTimeUtils.toDate(Instant.now()
            .minusMillis(offsetDayMillis)))
    }

    fun compareLastDayOfMonth(date: String): Boolean {
        return if (date != "") {
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var parsedDate = LocalDate.parse(date, dateTimeFormatter)
            var lastDayOfMonthDate = LocalDate.parse(date, dateTimeFormatter)
            lastDayOfMonthDate = lastDayOfMonthDate
                .withDayOfMonth(lastDayOfMonthDate.month.length(lastDayOfMonthDate.isLeapYear))
            return Math.abs(
                lastDayOfMonthDate.toEpochDay() - parsedDate.toEpochDay()
            ) == (0).toLong()
        } else
            false
    }

}
