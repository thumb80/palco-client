package it.antonino.palco.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.antonino.palco.PalcoApplication
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.densityPixelOffset
import java.text.SimpleDateFormat
import java.util.*

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Int.dpToPixels(): Int {
    return (this * PalcoApplication.instance.resources.displayMetrics.density + densityPixelOffset).toInt()
}

fun String.getDate() : Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
    return sdf.parse(this).time
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
