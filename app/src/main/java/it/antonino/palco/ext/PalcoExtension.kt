package it.antonino.palco.ext

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import it.antonino.palco.util.Constant
import it.antonino.palco.util.Constant.concertoDateFormat
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.Locale

fun SharedPreferences?.getShared(context: Context): SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    return EncryptedSharedPreferences.create(
        context,
        "palco_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

fun String.getGeoPosition(context: Context): Pair<Double, Double>? {

    val locationName = this
    val geocoder = Geocoder(context, Locale.getDefault())
    var ret: Pair<Double, Double>? = null

    runBlocking {
        try {
            val addressList = geocoder.getFromLocationName(locationName, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latitude = address.latitude
                val longitude = address.longitude

                ret = Pair<Double, Double>(latitude, longitude)

                Log.d("Geocoder", "Location: $locationName -> Lat: $latitude, Lon: $longitude")
            } else {
                Log.e("Geocoder", "Location not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return ret
}

fun String?.getDate(): Date? {
    return try {
        this?.let {
            Constant.concertoDateFormat.parse(it)
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