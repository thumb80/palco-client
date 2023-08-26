package it.antonino.palco.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoCardViewBinding
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.viewmodel.SharedViewModel
import it.antonino.palco.util.Constant.defaultDisplayFactor
import it.antonino.palco.util.Constant.densityPixelOffset
import it.antonino.palco.util.Constant.roundRadius
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import kotlin.collections.ArrayList

private val viewModel: SharedViewModel by inject(SharedViewModel::class.java)

private var artistThumb: String? = null
private var selectedItems = emptyArray<Int?>()
private var artistArray = arrayListOf<String>()

private lateinit var binding: ConcertoCardViewBinding

class CustomAdapter(
    val artist: ArrayList<String>?,
    val place: ArrayList<String>?,
    val city: ArrayList<String>?,
    val times: ArrayList<Date?>?,
    val listener: (ConcertRow) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    init {
        artistArray = artist!!
        selectedItems = arrayOfNulls(artist.size + 1)
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
                artistThumb)
            ,listener)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ConcertoCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

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
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.width = (260 * PalcoApplication.instance.resources.displayMetrics.density + densityPixelOffset).toInt()
        binding.mainContainer.layoutParams = layoutParams
        binding.mainContainer.visibility = View.VISIBLE

        binding.artist.text = item.artist
        binding.place.text = item.place
        binding.city.text = item.city

        viewModel.getArtistThumb(item.artist).observeForever {
            if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                artistThumb = it.get("results")
                    ?.asJsonArray
                    ?.get(0)
                    ?.asJsonObject
                    ?.get("cover_image")?.asString
                item.addArtistThumb(artistThumb)
                if (artistThumb?.contains(".gif") == true){
                    binding.artistImage.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.placeholder_scheda, null)
                    )
                }else{
                    Glide
                        .with(this)
                        .load(artistThumb)
                        .transform(RoundedCorners(roundRadius))
                        .error(ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.placeholder_scheda, null)
                        )
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
            if (selectedItems[artistArray.indexOf(item.artist)] == 1)
                listener(item)
        }
    }

    fun bindNullItem() = with(itemView) {
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.width = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                                                                            .defaultDisplay.width/defaultDisplayFactor
        binding.mainContainer.layoutParams = layoutParams
        binding.mainContainer.visibility = View.INVISIBLE
    }
}
