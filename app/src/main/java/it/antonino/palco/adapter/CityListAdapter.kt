package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import kotlinx.android.synthetic.main.city_list.view.*


class CityListAdapter(val city: ArrayList<String>?, val  listener: (String) -> Unit) : RecyclerView.Adapter<CityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(parent.inflate(R.layout.city_list))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(city?.get(position)!!,listener)
    }

    override fun getItemCount(): Int = city!!.size


}

class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: String, listener: (String) -> Unit) = with(itemView) {
        city.text = item
        setOnClickListener { listener(item) }
    }
}