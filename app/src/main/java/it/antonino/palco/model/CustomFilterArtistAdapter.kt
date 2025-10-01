package it.antonino.palco.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.JsonElement
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoFilterArtistViewBinding
import it.antonino.palco.ext.toPx
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.roundRadius
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import java.text.SimpleDateFormat
import java.util.Locale

private val viewModel: SharedViewModel by inject(SharedViewModel::class.java)

class CustomFilterArtistAdapter(
    val concerti: ArrayList<Concerto?>,
    val listener: (ConcertRow) -> Unit
) : RecyclerView.Adapter<CustomFilterArtistAdapter.FilterArtistListViewHolder>() {

    private val context: Context by inject(Context::class.java)
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

            val mArtist = org.apache.commons.text.StringEscapeUtils.unescapeJava(artist)
            val mPlace = org.apache.commons.text.StringEscapeUtils.unescapeJava(place)
            val mCity = org.apache.commons.text.StringEscapeUtils.unescapeJava(city)
            val mTime = org.apache.commons.text.StringEscapeUtils.unescapeJava(time?.let {
                SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY).format(
                    it
                )
            }).substring(0,1).uppercase() + org.apache.commons.text.StringEscapeUtils.unescapeJava(time?.let {
                SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALY).format(
                    it
                )
            }).substring(1)

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

            viewModel.getArtistThumb(concerto.artist).observeForever {
                if (it?.isJsonNull == false && it.get("results")?.asJsonArray?.size() != 0)  {
                    artistThumb = it.get("results")
                        ?.asJsonArray
                        ?.get(0)
                        ?.asJsonObject
                        ?.get("cover_image")?.asString

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

                    if (artistThumb?.contains(".gif") == true) {
                        binding.artistImage.setImageDrawable(
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
                            .error(ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.placeholder_scheda, null)
                            )
                            .into(binding.artistImage)
                    }
                }
                else  {
                    artistThumb = null
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
                SimpleDateFormat("dd-MM-yyyy", Locale.ITALY).parse(time)
            },
            artistThumb,
            artistInfo
        ))
    }

    override fun getItemCount(): Int = concerti.size
}
