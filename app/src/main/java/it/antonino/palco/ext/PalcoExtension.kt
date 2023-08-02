package it.antonino.palco.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonElement
import it.antonino.palco.PalcoApplication
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.actualDateFormat
import it.antonino.palco.util.Constant.dateDateFormat
import it.antonino.palco.util.Constant.dateTimeDateFormat
import it.antonino.palco.util.Constant.densityPixelOffset
import it.antonino.palco.util.Constant.offsetDayMillis
import it.antonino.palco.util.Constant.shareDateFormat
import java.time.Instant
import java.util.*

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Int.dpToPixels(): Int {
    return (this * PalcoApplication.instance.resources.displayMetrics.density + densityPixelOffset).toInt()
}

fun String.getStringFromDate(): String {
    var date: Long = 0L
    try {
        date = dateTimeDateFormat.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        try {
            date = dateDateFormat.parse(this)?.time ?: 0L
        } catch (e: Exception) {
            date = shareDateFormat.parse(this)?.time ?: 0L
        }
    }
    return shareDateFormat.format(Date(date))
}

fun Date.compareDate(): Boolean {
    return this.before(Date(System.currentTimeMillis().minus(offsetDayMillis)))
}

fun JsonElement.checkObject(): Boolean {
    return (this.asJsonObject?.get("artist")?.asString?.isEmpty() == true
            || this.asJsonObject?.get("place")?.asString?.isEmpty() == true
            || this.asJsonObject?.get("city")?.asString?.isEmpty() == true
            || this.asJsonObject?.get("time")?.asString?.isEmpty() == true)
}

fun Concerto?.checkObject(): Boolean {
    return this?.getArtist().isNullOrEmpty()
            || this?.getCity().isNullOrEmpty()
            || this?.getPlace().isNullOrEmpty()
            || this?.getTime() == null
}

fun Date?.isActualMonth(): Boolean? {
    val currentDate = actualDateFormat.format(Date(System.currentTimeMillis()))
    return try {
        this?.let { actualDateFormat.format(it).equals(currentDate, true) }
    } catch (e: Exception) {
        return null
    }
}

fun Date?.getString(): String? {
    return try {
        this?.let { dateDateFormat.format(it) }
    } catch (e: Exception) {
        return null
    }
}

fun String?.getDate(): Date? {
    return try {
        this?.let {
            dateDateFormat.parse(it)
        }
    } catch (e: Exception) {
        return null
    }
}
