package it.antonino.palco.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonElement
import it.antonino.palco.PalcoApplication
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant
import it.antonino.palco.util.Constant.densityPixelOffset
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Int.dpToPixels(): Int {
    return (this * PalcoApplication.instance.resources.displayMetrics.density + densityPixelOffset).toInt()
}

fun String.getDate(): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    return sdf.parse(this).time
}

fun String.compareDate(): Boolean {
    val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    val calendar = Calendar.getInstance()
    calendar.time = insdf.parse(this) as Date
    return calendar.time.before(
        DateTimeUtils.toDate(
            Instant.now()
        .minusMillis(Constant.offsetDayMillis)))
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


fun ArrayList<Concerto?>?.populateMonths() : ArrayList<String> {
    val months: ArrayList<String> = arrayListOf()
    val sdf = SimpleDateFormat("MMMM yyyy", Locale.ITALY)
    val insdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    this?.forEach {
        if (!months.contains(sdf.format(insdf.parse(it?.getTime()))))
            months.add(sdf.format(insdf.parse(it?.getTime())))
    }
    return months
}
