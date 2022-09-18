package it.antonino.palco.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import it.antonino.palco.R
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.ui.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.concerto_card_view.view.*
import kotlinx.android.synthetic.main.concerto_filter_view.view.*
import kotlinx.android.synthetic.main.concerto_filter_view.view.artist
import kotlinx.android.synthetic.main.concerto_filter_view.view.artist_image
import kotlinx.android.synthetic.main.concerto_filter_view.view.city
import kotlinx.android.synthetic.main.concerto_filter_view.view.place
import org.koin.java.KoinJavaComponent

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

private var artistThumb: String? = null
private var selectedItems = emptyArray<Int?>()
private var artistArray = arrayListOf<String>()

class CustomFilterAdapter(
    val artist: ArrayList<String>?,
    val place: ArrayList<String>?,
    val city: ArrayList<String>?,
    val times: ArrayList<String>? ,
    val listener: (ConcertRow) -> Unit) : RecyclerView.Adapter<FilterViewHolder>() {

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
        time.text = item.time

        viewModel.getArtistThumb(item.artist).observeForever {
            if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                artistThumb = it.get("results")?.asJsonArray?.get(0)?.asJsonObject?.get("cover_image")?.asString
                item.addArtistThumb(artistThumb)
                if (artistThumb?.contains(".gif") == true){
                    artist_image.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.placeholder_scheda, null))
                }else{
                    Glide
                        .with(this)
                        .load(artistThumb)
                        .transform(RoundedCorners(6))
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