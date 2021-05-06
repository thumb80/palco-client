package it.antonino.palco.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.antonino.palco.PalcoApplication
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.SequenceInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Stream

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Int.dpToPixels(): Int {
    return (this * PalcoApplication.instance.resources.displayMetrics.density + 0.5f).toInt()
}

fun String.getDate() : Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    return sdf.parse(this).time
}