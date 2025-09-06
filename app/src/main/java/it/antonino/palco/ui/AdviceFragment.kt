package it.antonino.palco.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import it.antonino.palco.PalcoActivity
import it.antonino.palco.PalcoApplication.Companion.sharedPreferences
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentAdviceBinding
import it.antonino.palco.util.Constant.checkNewDay
import kotlin.system.exitProcess

class AdviceFragment: Fragment() {

    private lateinit var binding: FragmentAdviceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdviceBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPreferences?.getBoolean("ok_consent", false) == true) {
            checkNewDay(requireContext())
            (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, ConcertsFragment())
                .commit()
        } else {
            val dialog = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom))
            dialog.setView(R.layout.dialog_advice)
            dialog.setPositiveButton(R.string.ok_consent, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.cancel()
                    checkNewDay(requireContext())
                    sharedPreferences?.edit()?.putBoolean("ok_consent", true)?.apply()
                    (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ConcertsFragment())
                        .commit()
                }

            })
            dialog.setNegativeButton(R.string.no_consent, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.cancel()
                    exitProcess(0)
                }
            })
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }
    }

}