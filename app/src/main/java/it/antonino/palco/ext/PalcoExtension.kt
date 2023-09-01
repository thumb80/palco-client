package it.antonino.palco.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.antonino.palco.util.Constant.actualDateFormat
import it.antonino.palco.util.Constant.dateTimeDateFormat
import it.antonino.palco.util.Constant.offsetDayMillis
import it.antonino.palco.util.Constant.shareDateFormat
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import java.util.*

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Date.compareDate(): Boolean {
    return this.before(Date(System.currentTimeMillis().minus(offsetDayMillis)))
}

fun Date?.isActualMonth(): Boolean? {
    val currentDate = actualDateFormat.format(DateTimeUtils.toDate(Instant.now()))
    return try {
        this?.let { actualDateFormat.format(it).equals(currentDate, true) }
    } catch (e: Exception) {
        return null
    }
}

fun Date?.getString(): String? {
    return try {
        this?.let { shareDateFormat.format(it) }
    } catch (e: Exception) {
        return null
    }
}

fun String?.getDate(): Date? {
    return try {
        this?.let {
            dateTimeDateFormat.parse(it)
        }
    } catch (e: Exception) {
        return null
    }
}
