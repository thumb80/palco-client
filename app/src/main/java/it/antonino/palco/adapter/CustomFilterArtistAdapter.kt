package it.antonino.palco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.ConcertoFilterViewBinding
import it.antonino.palco.ext.getString
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.model.Concerto
import it.antonino.palco.util.Constant.roundRadius
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import java.util.*

private val viewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

private var artistThumb: String? = null

class CustomFilterArtistAdapter(
    val concerti: ArrayList<Concerto?>,
    val listener: (ConcertRow) -> Unit
) : RecyclerView.Adapter<CustomFilterArtistAdapter.FilterArtistListViewHolder>() {

    inner class FilterArtistListViewHolder(
        val binding: ConcertoFilterViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(concerto: ConcertRow) {

            binding.artist.text = concerto.artist
            binding.place.text = concerto.place
            binding.city.text = concerto.city
            binding.time.text = concerto.time?.getString()

            val concertRow = ConcertRow(
                artist = concerto.artist,
                place = concerto.place,
                city = concerto.city,
                time = concerto.time,
                artistThumb = null
            )

            viewModel.getArtistThumb(concerto.artist).observeForever {
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

            binding.root.setOnClickListener {
                listener.invoke(concertRow)
            }
        }
    }

    private lateinit var binding: ConcertoFilterViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterArtistListViewHolder {
        binding = ConcertoFilterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilterArtistListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterArtistListViewHolder, position: Int) {
        holder.bind(ConcertRow(
            concerti[position]?.getArtist(),
            concerti[position]?.getPlace(),
            concerti[position]?.getCity(),
            concerti[position]?.getTime(),
            artistThumb
        ))
    }

    override fun getItemCount(): Int = concerti.size
}
