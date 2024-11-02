package it.antonino.palco.ext

import android.content.SharedPreferences
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import it.antonino.palco.PalcoApplication
import it.antonino.palco.util.Constant
import it.antonino.palco.util.Constant.concertoDateFormat
import java.time.Instant
import java.util.Date

fun SharedPreferences?.getShared(): SharedPreferences {
    val masterKey = MasterKey.Builder(PalcoApplication.instance)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    return EncryptedSharedPreferences.create(
        PalcoApplication.instance,
        "palco_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

fun String?.getDate(): Date? {
    return try {
        this?.let {
            Constant.dateTimeDateFormat.parse(it)
        }
    } catch (e: Exception) {
        return null
    }
}

fun Date.compareDate(): Boolean {
    return this.before(Date(System.currentTimeMillis().minus(Constant.offsetDayMillis)))
}

fun Date?.isActualMonth(): Boolean? {
    val currentDate = Constant.actualDateFormat.format(Date(System.currentTimeMillis()))
    return try {
        this?.let { Constant.actualDateFormat.format(it).equals(currentDate, true) }
    } catch (e: Exception) {
        return null
    }
}

fun String?.getTime(): Date? {
    return this?.let { concertoDateFormat.parse(it) }
}

fun Date?.getString(): String? {
    return try {
        this?.let {
            val dateStringSplitted = Constant.shareDateFormat.format(it).split(" ")
            val day = dateStringSplitted[0].capitalize()
            val month = dateStringSplitted[2].capitalize()
            return day + " " + dateStringSplitted[1] + " " + month + " " + dateStringSplitted[3]
        }
    } catch (e: Exception) {
        return null
    }
}

fun RecyclerView.setAccessibility() {
    this.accessibilityDelegate = object : View.AccessibilityDelegate() {
        override fun onRequestSendAccessibilityEvent(
            host: ViewGroup,
            child: View,
            event: AccessibilityEvent
        ): Boolean {
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
                (this@setAccessibility.layoutManager as LinearLayoutManager).scrollToPosition(child.tag as Int)
            return super.onRequestSendAccessibilityEvent(host, child, event)
        }
    }
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()