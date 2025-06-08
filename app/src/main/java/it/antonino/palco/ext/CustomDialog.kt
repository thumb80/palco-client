package it.antonino.palco.ext

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.DialogFragment
import it.antonino.palco.R
import it.antonino.palco.databinding.CustomDialogBinding
import it.antonino.palco.maps.MapsActivity
import it.antonino.palco.model.ConcertRow


class CustomDialog(
    private val concertRow: ConcertRow
    ) : DialogFragment() {

    private lateinit var binding: CustomDialogBinding
    private lateinit var builder: Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = CustomDialogBinding.inflate(layoutInflater)

        builder = Builder(activity)

        binding.listenButton.setOnClickListener {
            val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)
                putExtra(MediaStore.EXTRA_MEDIA_ARTIST, concertRow.artist)
                putExtra(SearchManager.QUERY, concertRow.artist)
            }
            startActivity(intent)
            dismiss()
        }

        if (concertRow.artistInfo != null) {
            binding.infoButton.setOnClickListener {
                val dialog = CustomInfosDialog(concertRow.artistInfo)
                activity?.supportFragmentManager?.let { frManager -> dialog.show(frManager, null) }
            }
        } else {
            binding.infoButton.background = getDrawable(resources, R.drawable.dialog_background_grey, null)
            binding.infoButton.setTextColor(getColor(resources, R.color.colorWhite, null))
        }

        binding.shareButton.setOnClickListener {
            shareConcert()
        }

        binding.mapsButton.setOnClickListener {
            val locationName = concertRow.place + ", " + concertRow.city
            val coordinates = locationName.getGeoPosition(requireContext())
            val intent = Intent(requireContext(), MapsActivity::class.java)
            intent.putExtra("latitude", coordinates?.first)
            intent.putExtra("longitude", coordinates?.second)
            intent.putExtra("locationName", locationName)
            startActivity(intent)
        }

        builder.setView(binding.root)
        val popupDialog = builder.create()
        popupDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return popupDialog
    }

    private fun shareConcert() {
        val pm = requireContext().packageManager
        val isInstalled: Boolean = isPackageInstalled("com.whatsapp", pm)

        if (isInstalled) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/*"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.putExtra(
                Intent.EXTRA_TEXT, getString(
                    R.string.share_concert_string,
                    concertRow.artist,
                    concertRow.city,
                    concertRow.time?.getString(),
                    concertRow.place
                )
            )
            intent.setPackage("com.whatsapp")
            context?.startActivity(intent)
            dismiss()
        } else {
            Toast.makeText(context, R.string.no_share_string, Toast.LENGTH_LONG).show()
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}