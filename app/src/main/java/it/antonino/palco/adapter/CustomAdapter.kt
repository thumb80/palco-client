package it.antonino.palco.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
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
import it.antonino.palco.ext.dpToPixels
import it.antonino.palco.ext.inflate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.ui.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.concerto_card_view.view.*
import org.koin.java.KoinJavaComponent.inject

private val viewModel: SharedViewModel by inject(SharedViewModel::class.java)

private var artistThumb: String? = null
private var selectedItems = emptyArray<Int?>()
private var artistArray = arrayListOf<String>()


class CustomAdapter(val artist: ArrayList<String>?,val place: ArrayList<String>?, val city: ArrayList<String>?, val times: ArrayList<String>? ,/*val bill: ArrayList<String?>?,*/  val  listener: (ConcertRow) -> Unit)
    : RecyclerView.Adapter<ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == RecyclerView.NO_POSITION)
            return
        else if (position == 0)
            holder.bindNullItem()
        else
            holder.bind(ConcertRow(
                artist?.get(position),
                place?.get(position),
                city?.get(position),
                times?.get(position),
                //bill?.get(position),
                artistThumb)
            ,listener)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.concerto_card_view))

    }

    override fun getItemCount(): Int {
        return artist!!.size
    }

    fun setSelectedItem(position: Int) {
        for (i in selectedItems.indices) {
            if (i == position)
                selectedItems[i] = 1
            else
                selectedItems[i] = 0
        }
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: ConcertRow, listener: (ConcertRow) -> Unit) = with(itemView) {
        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams.width = 260.dpToPixels()
        mainContainer.layoutParams = layoutParams
        mainContainer.visibility = View.VISIBLE

        artist.text = item.artist
        place.text = item.place
        city.text = item.city

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
            if (selectedItems[artistArray.indexOf(item.artist)] == 1)
                listener(item)
        }
    }

    fun bindNullItem() = with(itemView) {
        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
        layoutParams.width = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width/14
        mainContainer.layoutParams = layoutParams
        mainContainer.visibility = View.INVISIBLE
    }

}