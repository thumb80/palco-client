package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.databinding.CityListBinding
import it.antonino.palco.ext.inflate
import java.util.*


class CityListAdapter(val city: ArrayList<String>?, val  listener: (String) -> Unit)
    : RecyclerView.Adapter<CityListAdapter.CityListViewHolder>(), Filterable{


    private lateinit var binding: CityListBinding

    val initialCityDataList = ArrayList<String>().apply {
        city?.let { addAll(it) }
    }

    inner class CityListViewHolder(
        val binding: CityListBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(city: String) {

            binding.cityItem.text = city
            binding.root.setOnClickListener {
                listener.invoke(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListViewHolder {
        binding = CityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityListViewHolder, position: Int) {
        holder.bind(city?.get(position)!!)
    }

    override fun getItemCount(): Int = city?.size ?: 0

    override fun getFilter(): Filter {
        return cityFilter
    }

    private val cityFilter = object : Filter() {
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
                city?.clear()
                city?.addAll(results.values as ArrayList<String>)
                notifyDataSetChanged()
            }
        }
    }

}
