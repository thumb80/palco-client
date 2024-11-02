package it.antonino.palco.util

import com.google.gson.Gson
import it.antonino.palco.PalcoApplication
import java.text.SimpleDateFormat
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
    val dateTimeDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ITALY)
    val actualDateFormat = SimpleDateFormat("yyyy-MM", Locale.ITALY)
    val concertoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    val gson: Gson = Gson()
        .newBuilder()
        .setDateFormat(dateTimeFormat)
        .create()
}