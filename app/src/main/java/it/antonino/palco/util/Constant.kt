package it.antonino.palco.util

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

object Constant {

    const val connectTimeout = 45L
    const val readTimeout = 45L
    const val writeTimeout = 45L
    const val defaultDisplayFactor = 14
    const val maximumCardNumber = 24
    const val roundRadius = 6
    const val densityPixelOffset = 0.5f
    const val offsetDayMillis = 86400000L
    const val offscreenPageLimit = 2
    const val currentItem = 0
    const val redColorRGB = 241
    const val greenColorRGB = 90
    const val blueColorRGB = 36
    const val delayMillis = 1500L
    const val bitMapQuality = 100
    const val cacheSize = (5 * 1024 * 1024).toLong()
    val monthDateFormat = SimpleDateFormat("MMMM yyyy", Locale.ITALY)
    val dateDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    val shareDateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY)
    val dateTimeDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY)
    val actualDateFormat = SimpleDateFormat("yyyy-MM", Locale.ITALY)
    val dateTimeFormat = "yyyy-MM-dd HH:mm:ss"
    val gson = Gson()
        .newBuilder()
        .setDateFormat(dateTimeFormat)
        .create()
}
