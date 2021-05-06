package it.antonino.palco.ext

import android.app.AlertDialog
import android.app.Dialog
import android.app.SearchManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.IntDef
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import it.antonino.palco.R
import it.antonino.palco.model.ConcertRow
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.koin.ext.scope
import java.io.File
import java.io.FileOutputStream

class CustomDialog(private val concertRow: ConcertRow) : DialogFragment() {

    private val onConfirmedListener: OnConfirmedListener?
        get() {
            return if (parentFragment != null) {
                parentFragment as OnConfirmedListener
            } else {
                activity as OnConfirmedListener
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //POSITIVE
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

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

            Glide.with(requireContext()).asBitmap().load(concertRow.artistThumb)
                .into(object: CustomTarget<Bitmap>() {

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // do your stuff, you can load placeholder image here
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {


                        val cachePath = File(requireContext().cacheDir, "images")
                        cachePath.mkdirs() // don't forget to make the directory
                        val stream = FileOutputStream(cachePath.toString() + "/image.png") // overwrites this image every time
                        resource.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.close()

                        val imagePath = File(requireContext().cacheDir, "images")
                        val newFile = File(imagePath, "image.png")
                        val contentUri: Uri = FileProvider.getUriForFile(requireContext(), "it.antonino.palco.provider", newFile)

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Palco")
                        intent.putExtra(Intent.EXTRA_TEXT, "C'è un concerto \n ${concertRow.artist} a ${concertRow.city} \n clicca su link per maggiori dettagli https://palco.accesscam.org:3000/home")
                        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
                        context?.startActivity(Intent.createChooser(intent, "Choose..."))
                        dismiss()
                    }
                })
        }

        builder.setView(dialogView)
        val popupDialog = builder.create()
        popupDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return popupDialog
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        DialogInterface.BUTTON_POSITIVE,
        DialogInterface.BUTTON_NEUTRAL
    )
    annotation class ConfirmDialogButton

    interface OnConfirmedListener {
        fun onConfirmation(extra: String?, link: String?, @ConfirmDialogButton buttonClicked: Int)
    }

    class Builder {

        fun build(concertRow: ConcertRow): CustomDialog {
            val fragment = CustomDialog(concertRow = concertRow)
            return fragment
        }
    }
}