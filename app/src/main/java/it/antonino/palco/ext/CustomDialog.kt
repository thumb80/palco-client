package it.antonino.palco.ext

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.app.SearchManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.CustomDialogBinding
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.ui.maps.MapsActivity
import it.antonino.palco.util.Constant.bitMapQuality
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.java.KoinJavaComponent
import java.io.File
import java.io.FileOutputStream

class CustomDialog(private val concertRow: ConcertRow) : DialogFragment() {

    private lateinit var binding: CustomDialogBinding
    private lateinit var builder: Builder
    private val sharedViewModel: SharedViewModel by KoinJavaComponent.inject(SharedViewModel::class.java)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = CustomDialogBinding.inflate(layoutInflater)

        builder = Builder(activity)

        binding.mapsButton.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("place", concertRow.place)
            intent.putExtra("artist", concertRow.artist)
            intent.putExtra("city", concertRow.city)
            startActivity(intent)
        }

        binding.listenButton.setOnClickListener {
            val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)
                putExtra(MediaStore.EXTRA_MEDIA_ARTIST, concertRow.artist)
                putExtra(SearchManager.QUERY, concertRow.artist)
            }
            startActivity(intent)
            dismiss()
        }

        binding.infoButton.setOnClickListener {
            sharedViewModel.getArtistInfos(concertRow.artist).observe(this) {
                if (it?.isJsonNull == false) {
                    val artistInfo = it?.get("query")
                        ?.asJsonObject?.entrySet()?.iterator()?.next()
                        ?.value?.asJsonObject?.entrySet()?.first()?.value
                        ?.asJsonObject?.get("extract")?.asString
                    val dialog = CustomInfosDialog(artistInfo)
                    activity?.supportFragmentManager?.let { frManager -> dialog.show(frManager, null) }
                } else {
                    Toast.makeText(activity, resources.getString(R.string.server_error), Toast.LENGTH_LONG).show()
                }
            }

        }

        binding.shareButton.setOnClickListener {
            shareConcert()
        }

        builder.setView(binding.root)
        val popupDialog = builder.create()
        popupDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return popupDialog
    }

    private fun shareConcert() {
        if (concertRow.artistThumb != null)
            Glide.with(requireContext()).asBitmap().load(concertRow.artistThumb)
                .into(object: CustomTarget<Bitmap>() {

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                        val cachePath = File(requireContext().cacheDir, "images")
                        cachePath.mkdirs()
                        val stream = FileOutputStream("$cachePath/image.png") // overwrites this image every time
                        resource.compress(Bitmap.CompressFormat.PNG, bitMapQuality, stream)
                        stream.close()

                        val imagePath = File(requireContext().cacheDir, "images")
                        val newFile = File(imagePath, "image.png")
                        val contentUri: Uri = FileProvider.getUriForFile(requireContext(), "it.antonino.palco.provider", newFile)

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/*"
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name))
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        intent.putExtra(Intent.EXTRA_TEXT, getString(
                            R.string.share_concert_string,
                            concertRow.artist,
                            concertRow.city,
                            concertRow.time?.getString(),
                            concertRow.place)
                        )
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
                        context?.startActivity(
                            Intent.createChooser(intent, "Scegli con quale app vuoi condividere il concerto")
                        )
                        dismiss()
                    }
                })
        else {
            val cachePath = File(requireContext().cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.png") // overwrites this image every time
            BitmapFactory.decodeResource(context?.resources, R.drawable.placeholder_scheda).compress(Bitmap.CompressFormat.PNG, bitMapQuality, stream)
            stream.close()

            val imagePath = File(requireContext().cacheDir, "images")
            val newFile = File(imagePath, "image.png")
            val contentUri: Uri = FileProvider.getUriForFile(requireContext(), "it.antonino.palco.provider", newFile)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/*"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, getString(
                R.string.share_concert_string,
                concertRow.artist,
                concertRow.city,
                concertRow.time?.getString(),
                concertRow.place)
            )
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            context?.startActivity(
                Intent.createChooser(intent, PalcoApplication.instance.getString(R.string.share_text))
            )
            dismiss()
        }
    }
}