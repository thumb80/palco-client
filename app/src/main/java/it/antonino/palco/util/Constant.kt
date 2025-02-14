package it.antonino.palco.util

import android.content.Context
import com.google.gson.Gson
import it.antonino.palco.PalcoApplication
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Constant {

    private const val dateTimeFormat = "yyyy-MM-dd HH:mm:ss"
    const val defaultDisplayFactor = 14
    const val maximumCardNumber = 24
    const val offsetDayMillis = 86400000L
    const val offscreenPageLimit = 2
    const val currentItem = 0
    const val redColorRGB = 241
    const val greenColorRGB = 90
    const val blueColorRGB = 36
    const val cacheSize = (5 * 1024 * 1024).toLong()
    val monthDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALY)
    val shareDateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY)
    val dateTimeDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALY)
    val actualDateFormat = SimpleDateFormat("yyyy-MM", Locale.ITALY)
    val concertoDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ITALY)
    val gson: Gson = Gson()
        .newBuilder()
        .setDateFormat(dateTimeFormat)
        .create()

    fun checkNewDay(context: Context) {
        val prefs = context.getSharedPreferences("dailyTaskPrefs", Context.MODE_PRIVATE)
        val lastExecutionDate = prefs.getLong("lastExecutionDate", 0L)
        val now = Calendar.getInstance().timeInMillis
        val threshold = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 4)
            set(Calendar.MINUTE, 20)
        }.timeInMillis

        if (lastExecutionDate < threshold && !prefs.getBoolean("isNewDay", false)) {
            // Update last execution date
            prefs.edit().putLong("lastExecutionDate", now).apply()
            prefs.edit().putBoolean("isNewDay", true).apply()
        } else {
            prefs.edit().putBoolean("isNewDay", false).apply()
        }
    }
}