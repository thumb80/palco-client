package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoFilterArtistViewBinding
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
        val binding: ConcertoFilterArtistViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: ConcertRow) {

            val artist = concerto.artist
            val place = concerto.place
            val city = concerto.city
            val time = concerto.time

            val mArtist = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(artist)
            val mPlace = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(place)
            val mCity = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(city)
            val mTime = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY).format(time))

            binding.place.text = mPlace
            binding.city.text = mCity
            binding.time.text = mTime

            val concertRow = ConcertRow(
                artist = mArtist,
                city = mCity,
                place = mPlace,
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

    private lateinit var binding: ConcertoFilterArtistViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterArtistListViewHolder {
        binding = ConcertoFilterArtistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterArtistListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterArtistListViewHolder, position: Int) {
        holder.bind(ConcertRow(
            concerti[position]?.artist,
            concerti[position]?.city,
            concerti[position]?.place,
            concerti[position]?.time?.let { time ->
                SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(time)
            },
            artistThumb,
            artistInfo
        ))
    }

    override fun getItemCount(): Int = concerti.size
}
