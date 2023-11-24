package it.antonino.palco.ui.advice

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import it.antonino.palco.BuildConfig
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentAdviceBinding
import it.antonino.palco.ext.CustomWebViewClient
import it.antonino.palco.ui.ConcertiFragment
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PalcoApplication.sharedPreferences?.getBoolean("ok_consent", false) == true) {
            binding.adviseContainer.visibility = View.INVISIBLE
            childFragmentManager.beginTransaction()
                .replace(R.id.second_container, ConcertiFragment.newInstance())
                .commit()
        } else {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setView(R.layout.dialog_advice)
            dialog.setPositiveButton(R.string.ok_consent, object : OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.cancel()
                }

            })
            dialog.setNegativeButton(R.string.no_consent, object : OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    exitProcess(0)
                }
            })
            dialog.create()
            dialog.show()

            binding.webview.webViewClient = CustomWebViewClient(
                binding.privacyProgress,
                binding.okConsent,
                binding.noConsent,
                binding.adviseContainer,
                childFragmentManager
            )
            binding.webview.settings.javaScriptEnabled = true
            binding.webview.loadUrl(BuildConfig.privacyUrl)

        }
    }
}