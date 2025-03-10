package it.antonino.palco.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoCardViewBinding
import it.antonino.palco.ext.getDate
import it.antonino.palco.util.Constant.roundRadius
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

class CustomAdapter(
    val concerti: JsonArray,
    val listener: (ConcertRow) -> Unit
    ) : RecyclerView.Adapter<CustomAdapter.ConcertiViewHolder>() {

    private var artistThumb: String? = null
    private var artistInfo: String? = null
    private lateinit var binding: ConcertoCardViewBinding
    private val context: Context by inject(Context::class.java)

    inner class ConcertiViewHolder(
        val binding: ConcertoCardViewBinding
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: JsonObject) {

            val artist = concerto.get("artist")?.asString
            val place = concerto.get("place")?.asString
            val city = concerto.get("city")?.asString
            val time = concerto.get("time")?.asString?.getDate()

            binding.artist.text = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist)
            binding.place.text = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(place)
            binding.city.text = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(city)

            val concertRow = ConcertRow(
                artist = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist),
                city = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(city),
                place = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(place),
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
                        binding.roundedImageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.placeholder_scheda, null)
                        )
                    } else {
                        concertRow.addArtistThumb(artistThumb)
                        Glide
                            .with(context)
                            .load(artistThumb)
                            .transform(RoundedCorners(roundRadius))
                            .error(
                                ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.placeholder_scheda, null)
                            )
                            .into(binding.roundedImageView)
                    }
                }
                else  {
                    artistThumb = null
                    binding.roundedImageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.placeholder_scheda,
                            null
                        )
                    )
                }
            }

            viewModel.getArtistInfos(org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist)).observeForever {
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

}