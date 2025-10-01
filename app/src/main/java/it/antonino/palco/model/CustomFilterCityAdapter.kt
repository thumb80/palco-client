package it.antonino.palco.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.JsonElement
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoFilterCityViewBinding
import it.antonino.palco.util.Constant.roundRadius
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

class CustomFilterCityAdapter(
    val concerti: ArrayList<Concerto?>,
    val listener: (ConcertRow) -> Unit) : RecyclerView.Adapter<CustomFilterCityAdapter.FilterCityListViewHolder>() {

    private var artistThumb: String? = null
    private var artistInfo: String? = null
    private lateinit var binding: ConcertoFilterCityViewBinding

    private val context: Context by inject(Context::class.java)

    inner class FilterCityListViewHolder(
        val binding: ConcertoFilterCityViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: ConcertRow) {

            val artist = concerto.artist
            val place = concerto.place
            val time = concerto.time

            val mArtist = org.apache.commons.text.StringEscapeUtils.unescapeJava(artist)
            val mPlace = org.apache.commons.text.StringEscapeUtils.unescapeJava(place)
            val mTime = org.apache.commons.text.StringEscapeUtils.unescapeJava(time?.let {
                SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY).format(
                    it
                )
            }).substring(0,1).uppercase() + org.apache.commons.text.StringEscapeUtils.unescapeJava(time?.let {
                SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY).format(
                    it
                )
            }).substring(1)
            binding.artist.text = mArtist
            binding.place.text = mPlace
            binding.time.text = mTime

            val concertRow = ConcertRow(
                artist = mArtist,
                city = null,
                place = mPlace,
                time = concerto.time,
                artistThumb = null,
                artistInfo = null
            )

            viewModel.getArtistThumb(concerto.artist).observeForever {
                if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                    artistThumb = it.get("results")?.asJsonArray?.get(0)?.asJsonObject?.get("cover_image")?.asString
                    concertRow.addArtistThumb(artistThumb)

                    val labelId = it.get("results")
                        ?.asJsonArray
                        ?.get(0)
                        ?.asJsonObject
                        ?.get("resource_url")?.asString?.substringAfterLast("/")

                    viewModel.getArtistInfos(labelId).observeForever {
                        if (it?.isJsonNull == false) {
                            try {
                                artistInfo = it.get("profile")?.asString
                                concertRow.addArtistInfo(artistInfo)
                            } catch (e: Exception) {
                                artistInfo = null
                            }
                        } else {
                            artistInfo = null
                        }
                    }

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
            }

            binding.root.setOnClickListener {
                listener.invoke(concertRow)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCityListViewHolder {
        binding = ConcertoFilterCityViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterCityListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterCityListViewHolder, position: Int) {
        holder.bind(ConcertRow(
            concerti[position]?.artist,
            null,
            concerti[position]?.place,
            concerti[position]?.time?.let { time ->
                SimpleDateFormat("dd-MM-yyyy", Locale.ITALY).parse(time)
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