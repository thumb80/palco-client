package it.antonino.palco.util

import com.google.gson.JsonElement
import it.antonino.palco.model.Concerto
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*

class PalcoUtils {

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
        return concerto.asJsonObject?.get("artist")?.asString?.isEmpty() == true
                || concerto.asJsonObject?.get("place")?.asString?.isEmpty() == true
                || concerto.asJsonObject?.get("city")?.asString?.isEmpty() == true
                || concerto.asJsonObject?.get("time")?.asString?.isEmpty() == true
    }

}