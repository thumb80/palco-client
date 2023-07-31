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

fun String.getDate(): Long {
    return dateDateFormat.parse(this)?.time ?: 0L
}

fun String.getDateFromString(): String? {
    val date = dateDateFormat.parse(this)?.time
    return date?.let { Date(it) }?.let { shareDateFormat.format(it) }
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

fun String.compareDate(): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = dateDateFormat.parse(this) as Date
    return calendar.time.before(Date(System.currentTimeMillis().minus(offsetDayMillis)))
}

fun JsonElement.checkObject(): Boolean {
    return (this.asJsonObject?.get("artist")?.asString?.isEmpty() == true
            || this.asJsonObject?.get("place")?.asString?.isEmpty() == true
            || this.asJsonObject?.get("city")?.asString?.isEmpty() == true
            || this.asJsonObject?.get("time")?.asString?.isEmpty() == true)
            || this.asJsonObject?.get("time")?.asString?.compareDate() == true
}

fun Concerto?.checkObject(): Boolean {
    return this?.getArtist().isNullOrEmpty()
            || this?.getCity().isNullOrEmpty()
            || this?.getPlace().isNullOrEmpty()
            || this?.getTime().isNullOrEmpty()
            || this?.getTime()?.compareDate() == true
}

fun Date?.isActualMonth(): Boolean? {
    val currentDate = actualDateFormat.format(Date(System.currentTimeMillis()))
    return this?.let { actualDateFormat.format(it).equals(currentDate, true) }
}
