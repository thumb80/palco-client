package it.antonino.palco.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import it.antonino.palco.R
import it.antonino.palco.ext.getString
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.viewmodel.SharedViewModel
import it.antonino.palco.util.Constant.roundRadius
import kotlinx.android.synthetic.main.concerto_filter_view.view.artist
import kotlinx.android.synthetic.main.concerto_filter_view.view.artist_image
import kotlinx.android.synthetic.main.concerto_filter_view.view.place
import kotlinx.android.synthetic.main.concerto_filter_view.view.time
import kotlinx.android.synthetic.main.concerto_filter_view.view.city
import org.koin.java.KoinJavaComponent
import java.util.*
import kotlin.collections.ArrayList

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

private var artistThumb: String? = null
private var selectedItems = emptyArray<Int?>()
private var artistArray: ArrayList<String?>? = null

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
        return FilterViewHolder(parent.inflate(R.layout.concerto_filter_view))
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

        artist.text = item.artist
        place.text = item.place
        city.text = item.city
        time.text = item.time?.getString()

        viewModel.getArtistThumb(item.artist).observeForever {
            if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                artistThumb = it.get("results")?.asJsonArray?.get(0)?.asJsonObject?.get("cover_image")?.asString
                item.addArtistThumb(artistThumb)
                if (artistThumb?.contains(".gif") == true){
                    artist_image
                        .setImageDrawable(
                            ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null)
                        )
                }else{
                    Glide
                        .with(this)
                        .load(artistThumb)
                        .transform(RoundedCorners(roundRadius))
                        .error(ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null))
                        .into(artist_image)
                }
            }
            else {
                artistThumb = null
                item.addArtistThumb(artistThumb)
                artist_image.setImageDrawable(
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
