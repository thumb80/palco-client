package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoFilterViewBinding
import it.antonino.palco.ext.getString
import it.antonino.palco.ext.toPx
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.model.Concerto
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import java.text.SimpleDateFormat
import java.util.Locale

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

class CustomFilterArtistAdapter(
    val concerti: ArrayList<Concerto?>,
    val listener: (ConcertRow) -> Unit
) : RecyclerView.Adapter<CustomFilterArtistAdapter.FilterArtistListViewHolder>() {

    private var artistThumb: String? = null
    private var artistInfo: String? = null

    inner class FilterArtistListViewHolder(
        val binding: ConcertoFilterViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: ConcertRow) {

            val artist = concerto.artist
            val place = concerto.place
            val city = concerto.city
            val time = concerto.time

            val mArtist = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist)
            val mPlace = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(place)
            val mCity = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(city)

            binding.artist.text = mArtist
            binding.place.text = mPlace
            binding.city.text = mCity

            val concertRow = ConcertRow(
                artist = mArtist,
                place = mPlace,
                city = mCity,
                time = concerto.time,
                artistThumb = null,
                artistInfo = null
            )

            /*viewModel.getArtistThumb(concerto.artist).observeForever {
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
            }*/

            viewModel.getArtistInfos(mArtist).observeForever {
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

            binding.root.setOnClickListener {
                listener.invoke(concertRow)
            }
        }
    }

    private lateinit var binding: ConcertoFilterViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterArtistListViewHolder {
        binding = ConcertoFilterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.height = parent.context.resources.getDimension(R.dimen.dp_64).toInt().toPx()
        binding.mainFilterContainer.layoutParams = layoutParams
        return FilterArtistListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterArtistListViewHolder, position: Int) {
        holder.bind(ConcertRow(
            concerti[position]?.artist,
            concerti[position]?.place,
            concerti[position]?.city,
            concerti[position]?.time?.let { time ->
                SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(time)
            },
            artistThumb,
            artistInfo
        ))
    }

    override fun getItemCount(): Int = concerti.size
}
