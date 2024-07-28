package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.databinding.ArtistListBinding
import it.antonino.palco.ext.toPx
import java.util.*
import kotlin.collections.ArrayList


class ArtistListAdapter(val artists: ArrayList<String?>, val  listener: (String) -> Unit)
    : RecyclerView.Adapter<ArtistListAdapter.ArtistListViewHolder>(), Filterable {

    private lateinit var binding: ArtistListBinding
    var artistsFiltered: ArrayList<String?>? = null

    val initialArtistDataList = ArrayList<String>().apply {
        artists.let {
            it.forEach {
                add(it!!)
            }
        }
    }

    inner class ArtistListViewHolder(
        val binding: ArtistListBinding
    ): RecyclerView.ViewHolder(binding.root) {


        fun bind(artist: String) {
            binding.artistItem.text = artist
            binding.root.setOnClickListener {
                listener.invoke(artist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistListViewHolder {
        binding = ArtistListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.height = parent.context.resources.getDimension(R.dimen.dp_64).toInt().toPx()
        binding.artistItem.layoutParams = layoutParams
        return ArtistListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistListViewHolder, position: Int) {
        holder.bind(artists[position]!!)
    }

    override fun getItemCount(): Int = artists.size

    override fun getFilter(): Filter {
        return artistFilter
    }

    private val artistFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<String?> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialArtistDataList.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().toLowerCase()
                initialArtistDataList.forEach {
                    if (it.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            artistsFiltered?.addAll(filteredList)
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is ArrayList<*>) {
                artists.clear()
                artists.addAll(results.values as ArrayList<String>)
                notifyDataSetChanged()
            }
        }
    }

}
