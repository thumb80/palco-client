package it.antonino.palco.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import it.antonino.palco.PalcoApplication
import it.antonino.palco.databinding.ConcertoFilterViewBinding
import it.antonino.palco.ext.getString
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

class CustomFilterCityAdapter(
    val concerti: ArrayList<Concerto?>,
    val listener: (ConcertRow) -> Unit) : RecyclerView.Adapter<CustomFilterCityAdapter.FilterCityListViewHolder>() {

    private var artistThumb: String? = null
    private var artistInfo: String? = null
    private lateinit var binding: ConcertoFilterViewBinding

    inner class FilterCityListViewHolder(
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
            val mTime = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(time))

            binding.artist.text = mArtist
            binding.place.text = mPlace
            binding.city.text = mCity
            binding.time.text = mTime

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
                    artistThumb = it.get("results")?.asJsonArray?.get(0)?.asJsonObject?.get("cover_image")?.asString
                    concertRow.addArtistThumb(artistThumb)
                    if (artistThumb?.contains(".gif") == true){
                        binding.artistImage
                            .setImageDrawable(
                                ResourcesCompat.getDrawable(context.resources, R.drawable.placeholder_scheda, null)
                            )
                    }else{
                        Glide
                            .with(context)
                            .load(artistThumb)
                            .transform(RoundedCorners(roundRadius))
                            .error(ResourcesCompat.getDrawable(context.resources, R.drawable.placeholder_scheda, null))
                            .into(binding.artistImage)
                    }
                }
                else {
                    artistThumb = null
                    concertRow.addArtistThumb(artistThumb)
                    binding.artistImage.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCityListViewHolder {
        binding = ConcertoFilterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterCityListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterCityListViewHolder, position: Int) {
        holder.bind(ConcertRow(
            concerti[position]?.artist,
            concerti[position]?.place,
            concerti[position]?.city,
            concerti[position]?.time?.let { time ->
                SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).parse(time)
                    ?.let { date ->
                        Date(date.time)
                    }
            },
            artistThumb,
            artistInfo)
        )
    }

    override fun getItemCount(): Int = concerti.size
}