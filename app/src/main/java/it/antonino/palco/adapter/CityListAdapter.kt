package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.City
import kotlinx.android.synthetic.main.city_list.view.city
import java.util.*


class CityListAdapter(val city: ArrayList<City>?, val  listener: (String) -> Unit)
    : RecyclerView.Adapter<CityViewHolder>(), Filterable{

    val initialCityDataList = ArrayList<City>().apply {
        city?.let { addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(parent.inflate(R.layout.city_list))
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(city?.get(position)?.getCity()!!,listener)
    }

    override fun getItemCount(): Int = city?.size ?: 0

    override fun getFilter(): Filter {
        return cityFilter
    }

    private val cityFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<City?> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialCityDataList.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().toLowerCase()
                initialCityDataList.forEach {
                    if (it.getCity().toLowerCase(Locale.ROOT).contains(query)) {
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
                city?.addAll(results.values as ArrayList<City>)
                notifyDataSetChanged()
            }
        }
    }

}

class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: String, listener: (String) -> Unit) = with(itemView) {
        city.text = item
        setOnClickListener { listener(item) }
    }
}
