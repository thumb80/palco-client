package it.antonino.palco.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoCardViewBinding
import it.antonino.palco.ext.getDate
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.util.Constant.itemDimension
import it.antonino.palco.util.Constant.itemDimensionTablet
import it.antonino.palco.util.Constant.itemDimensionTabletMax
import it.antonino.palco.util.Constant.roundRadius
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent.inject


private val viewModel: SharedViewModel by inject(SharedViewModel::class.java)

class CustomAdapter(
    val concerti: JsonArray,
    val listener: (ConcertRow) -> Unit
    ) : RecyclerView.Adapter<CustomAdapter.ConcertiViewHolder>() {

    private var artistThumb: String? = null
    private var artistInfo: String? = null
    private lateinit var binding: ConcertoCardViewBinding

    inner class ConcertiViewHolder(
        val binding: ConcertoCardViewBinding
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: JsonObject) {

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )

            layoutParams.width = when (isTablet(PalcoApplication.instance)) {
                0 -> {
                    itemDimensionTabletMax
                }
                1 -> {
                    itemDimensionTablet
                }
                else -> {
                    itemDimension
                }
            }

            binding.mainContainer.layoutParams = layoutParams
            binding.mainContainer.visibility = View.VISIBLE

            val artist = concerto.get("artist")?.asString
            val place = concerto.get("place")?.asString
            val city = concerto.get("city")?.asString
            val time = concerto.get("time")?.asString?.getDate()

            binding.artist.text = artist
            binding.place.text = place
            binding.city.text = city

            var concertRow = ConcertRow(
                artist = artist,
                place = place,
                city = city,
                time = time,
                artistThumb = null,
                artistInfo = null
            )

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
                        concertRow.addArtistThumb(artistThumb)
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
                else  {
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

            viewModel.getArtistInfos(artist).observeForever {
                if (it?.isJsonNull == false) {
                    var artistInfoExtract: JsonElement? = null
                    try {
                        artistInfoExtract = it.get("query")
                            ?.asJsonObject?.entrySet()?.iterator()?.next()
                            ?.value?.asJsonObject?.entrySet()?.first()?.value
                            ?.asJsonObject?.get("extract")
                        artistInfo = if (artistInfoExtract != null && artistInfoExtract.asString?.isNotEmpty() == true) artistInfoExtract.asString else null
                        concertRow.addArtistInfo(artistInfo)
                    } catch (e: Exception) {
                        artistInfo = null
                    }
                } else {
                    artistInfo = null
                }
            }

            binding.cardContainer.setOnClickListener {
                listener.invoke(concertRow)

            }
        }
    }

    override fun onBindViewHolder(holder: ConcertiViewHolder, position: Int) {
        holder.bind(concerti[position].asJsonObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConcertiViewHolder {
        binding = ConcertoCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConcertiViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return concerti.size()
    }

    fun isTablet(context: Context): Int? {
        val xlarge =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        val ret = if (xlarge) 0 else if (large) 1 else null
        return ret
    }
}