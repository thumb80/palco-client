package it.antonino.palco.ext

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.gson.JsonObject
import it.antonino.palco.databinding.InfosDialogBinding

class CustomInfosDialog(private val artistInfos: String?): DialogFragment() {

    private lateinit var binding: InfosDialogBinding
    private lateinit var builder: AlertDialog.Builder


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = InfosDialogBinding.inflate(layoutInflater)
        builder = AlertDialog.Builder(activity)

        binding.infosText.text = artistInfos

        builder.setView(binding.root)
        val popupDialog = builder.create()
        return popupDialog

    }

}