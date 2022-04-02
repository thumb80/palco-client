package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.Artist
import it.antonino.palco.model.City
import it.antonino.palco.model.Months
import kotlinx.android.synthetic.main.city_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class MonthListAdapter(val month: ArrayList<String>?, val  listener: (String) -> Unit)
    : RecyclerView.Adapter<MonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        return MonthViewHolder(parent.inflate(R.layout.city_list))
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(month?.get(position)!!, listener)
    }

    override fun getItemCount(): Int = month?.size ?: 0


}

class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: String, listener: (String) -> Unit) = with(itemView) {
        city.text = item
        setOnClickListener { listener(item) }
    }
}