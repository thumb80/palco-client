package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import kotlinx.android.synthetic.main.city_list.view.city
import java.util.*
import kotlin.collections.ArrayList

class ArtistListAdapter(val artists: ArrayList<String?>, val  listener: (String) -> Unit)
    : RecyclerView.Adapter<ArtistViewHolder>(), Filterable {

    val initialCityDataList = ArrayList<String>().apply {
        artists.let {
            it.forEach {
                add(it!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(parent.inflate(R.layout.city_list))
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists.get(position)!!, listener)
    }

    override fun getItemCount(): Int = artists.size

    override fun getFilter(): Filter {
        return artistFilter
    }

    private val artistFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<String?> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialCityDataList.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().toLowerCase()
                initialCityDataList.forEach {
                    if (it.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is ArrayList<*>) {
                artists.clear()
                artists.addAll(results.values as Collection<String?>)
                notifyDataSetChanged()
            }
        }
    }

}

class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: String, listener: (String) -> Unit) = with(itemView) {
        city.text = item
        setOnClickListener { listener(item) }
    }
}
