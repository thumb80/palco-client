package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import kotlinx.android.synthetic.main.city_list.view.*

class MixedListAdapter(val city: ArrayList<Pair<String, Int>>, val  listener1: (String) -> Unit, val  listener2: (String) -> Unit) : RecyclerView.Adapter<MixedViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MixedViewHolder {
        return MixedViewHolder(parent.inflate(R.layout.city_list))
    }

    override fun onBindViewHolder(holder: MixedViewHolder, position: Int) {
        holder.bind(city.get(position).first, city.get(position).second, listener1, listener2)
    }

    override fun getItemCount(): Int = city.size


}

class MixedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: String, type: Int, listener1: (String) -> Unit, listener2: (String) -> Unit) = with(itemView) {
        city.text = item
        setOnClickListener {
            if (type == 0)
                listener1(item)
            else
                listener2(item)
        }
    }
}