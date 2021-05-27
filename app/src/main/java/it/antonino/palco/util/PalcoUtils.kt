package it.antonino.palco.util

import com.google.gson.JsonElement
import it.antonino.palco.model.Concerto
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*

class PalcoUtils {

    private fun getDate(time: String): Date? {
        val insdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ITALY)
        val time = time.plus("T23:59:59")
        return insdf.parse(time)
    }

    private fun getOnlyDate(time: String): Date? {
        val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        return insdf.parse(time)
    }

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
                || concerto?.getTime().isNullOrEmpty()
    }

    fun checkObject(concerto: JsonElement): Boolean {
        return concerto.asJsonObject.get("artist").asString.isNullOrEmpty()
                || concerto.asJsonObject.get("place").asString.isNullOrEmpty()
                || concerto.asJsonObject.get("city").asString.isNullOrEmpty()
                || concerto.asJsonObject.get("time").asString.isNullOrEmpty()
    }

}