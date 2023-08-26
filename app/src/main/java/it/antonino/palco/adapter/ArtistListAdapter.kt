package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.databinding.CityListBinding
import java.util.*
import kotlin.collections.ArrayList

private lateinit var binding: CityListBinding

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
        binding = CityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding.root)
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
        binding.city.text = item
        setOnClickListener { listener(item) }
    }
}
