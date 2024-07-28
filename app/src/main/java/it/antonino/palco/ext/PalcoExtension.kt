package it.antonino.palco.ext

import android.content.SharedPreferences
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.AccessibilityDelegate
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import it.antonino.palco.PalcoApplication
import it.antonino.palco.util.Constant.actualDateFormat
import it.antonino.palco.util.Constant.dateTimeDateFormat
import it.antonino.palco.util.Constant.offsetDayMillis
import it.antonino.palco.util.Constant.shareDateFormat
import java.time.Instant
import java.util.Date

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Date.compareDate(): Boolean {
    return this.before(Date(System.currentTimeMillis().minus(offsetDayMillis)))
}

fun Date?.isActualMonth(): Boolean? {
    val currentDate = actualDateFormat.format(Date.from(Instant.now()))
    return try {
        this?.let { actualDateFormat.format(it).equals(currentDate, true) }
    } catch (e: Exception) {
        return null
    }
}

fun Date?.getString(): String? {
    return try {
        this?.let {
            val dateStringSplitted = shareDateFormat.format(it).split(" ")
            val day = dateStringSplitted[0].capitalize()
            val month = dateStringSplitted[2].capitalize()
            return day + " " + dateStringSplitted[1] + " " + month + " " + dateStringSplitted[3]
        }
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

fun SharedPreferences?.getShared() : SharedPreferences {
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

fun RecyclerView.setAccessibility() {
    this.accessibilityDelegate = object : AccessibilityDelegate() {
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

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

