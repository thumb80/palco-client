package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.ui.viewmodel.SharedViewModel
import it.antonino.palco.util.Constant.roundRadius
import kotlinx.android.synthetic.main.concerto_filter_view.view.artist
import kotlinx.android.synthetic.main.concerto_filter_view.view.artist_image
import kotlinx.android.synthetic.main.concerto_filter_view.view.place
import kotlinx.android.synthetic.main.concerto_filter_view.view.city
import kotlinx.android.synthetic.main.concerto_filter_view.view.time
import org.koin.java.KoinJavaComponent

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

private var placeThumb: String? = null
private var selectedItems = emptyArray<Int?>()
private var artistArray = arrayListOf<String>()

class CustomFilterArtistAdapter(
    val artist: ArrayList<String>?,
    val place: ArrayList<String>?,
    val city: ArrayList<String>?,
    val times: ArrayList<String>? ,
    val listener: (ConcertRow) -> Unit
) : RecyclerView.Adapter<FilterArtistViewHolder>() {

    init {
        artistArray = artist!!
        selectedItems = arrayOfNulls<Int>(artist.size + 1)
        for (i in selectedItems.indices) {
            if (i == 1)
                selectedItems[i] = 1
            else
                selectedItems[i] = 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterArtistViewHolder {
        return FilterArtistViewHolder(parent.inflate(R.layout.concerto_filter_view))
    }

    override fun onBindViewHolder(holder: FilterArtistViewHolder, position: Int) {
        holder.bind(ConcertRow(
            artist?.get(position),
            place?.get(position),
            city?.get(position),
            times?.get(position),
            placeThumb)
            ,listener)
    }

    override fun getItemCount(): Int = artist?.size!!
}

class FilterArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: ConcertRow, listener: (ConcertRow) -> Unit) = with(itemView) {

        artist.text = item.artist
        place.text = item.place
        city.text = item.city
        time.text = item.time

        viewModel.getPlacePhoto(item.city!!).observeForever {
            if (it?.isJsonNull == false) {
                placeThumb = it
                    .get("results")
                    ?.asJsonArray
                    ?.get(0)
                    ?.asJsonObject
                    ?.get("urls")
                    ?.asJsonObject
                    ?.get("thumb")?.asString
                item.addArtistThumb(placeThumb)
                Glide.with(this)
                    .load(placeThumb)
                    .transform(RoundedCorners(roundRadius))
                    .error(ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null))
                    .into(artist_image)
            }
            else
                artist_image
                    .setImageDrawable(
                        ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null)
                    )
        }


        setOnClickListener {
            listener(item)
        }
    }

}
