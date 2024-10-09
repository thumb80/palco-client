package it.antonino.palco.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoCardViewBinding
import it.antonino.palco.ext.getDate
import it.antonino.palco.util.Constant.itemDimension
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

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

            val artist = concerto.get("artist")?.asString
            val place = concerto.get("place")?.asString
            val city = concerto.get("city")?.asString
            val time = concerto.get("time")?.asString?.getDate()

            binding.artist.text = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist)
            binding.place.text = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(place)
            binding.city.text = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(city)

            val concertRow = ConcertRow(
                artist = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist),
                place = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(place),
                city = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(city),
                time = time,
                artistThumb = null,
                artistInfo = null
            )

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