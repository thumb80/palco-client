package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoCardViewBinding
import it.antonino.palco.ext.getDate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.util.Constant.itemDimension
import it.antonino.palco.util.Constant.nullItemDimension
import it.antonino.palco.util.Constant.roundRadius
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent.inject

private val viewModel: SharedViewModel by inject(SharedViewModel::class.java)

class CustomAdapter(
    val concerti: JsonArray,
    val listener: (ConcertRow) -> Unit) : RecyclerView.Adapter<CustomAdapter.ConcertiViewHolder>() {

    private var artistThumb: String? = null
    private lateinit var binding: ConcertoCardViewBinding

    inner class ConcertiViewHolder(val binding: ConcertoCardViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: JsonObject) {

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.width = itemDimension

            binding.mainContainer.layoutParams = layoutParams
            binding.mainContainer.visibility = View.VISIBLE

            val artist = concerto.get("artist")?.asString
            val place = concerto.get("place")?.asString
            val city = concerto.get("city")?.asString
            val time = concerto.get("time")?.asString?.getDate()

            binding.artist.text = artist
            binding.place.text = place
            binding.city.text = city

            viewModel.getArtistThumb(artist).observeForever {
                if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                    artistThumb = it.get("results")
                        ?.asJsonArray
                        ?.get(0)
                        ?.asJsonObject
                        ?.get("cover_image")?.asString
                    if (artistThumb?.contains(".gif") == true) {
                        binding.artistImage.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                PalcoApplication.instance.resources,
                                R.drawable.placeholder_scheda, null)
                        )
                    } else {
                        Glide
                            .with(PalcoApplication.instance)
                            .load(artistThumb)
                            .transform(RoundedCorners(roundRadius))
                            .error(ResourcesCompat.getDrawable(
                                PalcoApplication.instance.resources,
                                R.drawable.placeholder_scheda, null)
                            )
                            .into(binding.artistImage)
                    }
                }
                else {
                    artistThumb = null
                    binding.artistImage.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            PalcoApplication.instance.resources,
                            R.drawable.placeholder_scheda,
                            null
                        )
                    )
                }
            }

            binding.cardContainer.setOnClickListener {
                listener.invoke(
                    ConcertRow(
                        artist = artist,
                        place = place,
                        city = city,
                        time = time,
                        artistThumb = artistThumb
                    )
                )

            }
        }

        fun bindNullItem() {
            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.width = nullItemDimension

            binding.mainContainer.layoutParams = layoutParams
            binding.mainContainer.visibility = View.INVISIBLE
        }
    }

    override fun onBindViewHolder(holder: ConcertiViewHolder, position: Int) {

        if (position == 0) {
            holder.bindNullItem()
        }
        else {
            holder.bind(concerti[position].asJsonObject)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertiViewHolder {
        binding = ConcertoCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConcertiViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return concerti.size()
    }
}