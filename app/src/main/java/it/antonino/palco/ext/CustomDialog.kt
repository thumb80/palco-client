package it.antonino.palco.ext

import android.app.AlertDialog
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
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import it.antonino.palco.R
import it.antonino.palco.model.ConcertRow
import it.antonino.palco.ui.maps.MapsActivity
import it.antonino.palco.util.Constant.bitMapQuality
import kotlinx.android.synthetic.main.custom_dialog.view.*
import java.io.File
import java.io.FileOutputStream

class CustomDialog(private val concertRow: ConcertRow) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //POSITIVE
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

        dialogView.maps_button.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("place", concertRow.place)
            intent.putExtra("artist", concertRow.artist)
            intent.putExtra("city", concertRow.city)
            startActivity(intent)
        }

        dialogView.listen_button.setOnClickListener {
            val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)
                putExtra(MediaStore.EXTRA_MEDIA_ARTIST, concertRow.artist)
                putExtra(SearchManager.QUERY, concertRow.artist)
            }
            startActivity(intent)
            dismiss()
        }

        dialogView.back_button.setOnClickListener {
            dismiss()
        }

        dialogView.share_button.setOnClickListener {
            shareConcert()
        }

        builder.setView(dialogView)
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
                            concertRow.place,
                            concertRow.time?.getDateFromString())
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
                concertRow.place,
                concertRow.time?.getDateFromString())
            )
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            context?.startActivity(
                Intent.createChooser(intent, "Scegli con quale app vuoi condividere il concerto")
            )
            dismiss()
        }
    }
}