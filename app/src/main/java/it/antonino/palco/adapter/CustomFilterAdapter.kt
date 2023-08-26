package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoFilterViewBinding
import it.antonino.palco.ext.getString
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.viewmodel.SharedViewModel
import it.antonino.palco.util.Constant.roundRadius
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import kotlin.collections.ArrayList

private val viewModel: SharedViewModel by inject(SharedViewModel::class.java)

private var artistThumb: String? = null
private var selectedItems = emptyArray<Int?>()
private var artistArray: ArrayList<String?>? = null

private lateinit var binding: ConcertoFilterViewBinding

class CustomFilterAdapter(
    val artist: ArrayList<String?>?,
    val place: ArrayList<String?>?,
    val city: ArrayList<String?>?,
    val times: ArrayList<Date?>?,
    val listener: (ConcertRow) -> Unit) : RecyclerView.Adapter<FilterViewHolder>() {

    init {
        val selectedItemSize = artist?.size?.plus(1) ?: 0
        artistArray = artist
        selectedItems = arrayOfNulls<Int>(selectedItemSize)
        for (i in selectedItems.indices) {
            if (i == 1)
                selectedItems[i] = 1
            else
                selectedItems[i] = 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        binding = ConcertoFilterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(ConcertRow(
            artist?.get(position),
            place?.get(position),
            city?.get(position),
            times?.get(position),
            artistThumb)
            ,listener)
    }

    override fun getItemCount(): Int = artist?.size!!


}


class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: ConcertRow, listener: (ConcertRow) -> Unit) = with(itemView) {

        binding.artist.text = item.artist
        binding.place.text = item.place
        binding.city.text = item.city
        binding.time.text = item.time?.getString()

        viewModel.getArtistThumb(item.artist).observeForever {
            if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                artistThumb = it.get("results")?.asJsonArray?.get(0)?.asJsonObject?.get("cover_image")?.asString
                item.addArtistThumb(artistThumb)
                if (artistThumb?.contains(".gif") == true){
                    binding.artistImage
                        .setImageDrawable(
                            ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null)
                        )
                }else{
                    Glide
                        .with(this)
                        .load(artistThumb)
                        .transform(RoundedCorners(roundRadius))
                        .error(ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null))
                        .into(binding.artistImage)
                }
            }
            else {
                artistThumb = null
                item.addArtistThumb(artistThumb)
                binding.artistImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.placeholder_scheda,
                        null
                    )
                )
            }
        }


        setOnClickListener {
            listener(item)
        }
    }

}
