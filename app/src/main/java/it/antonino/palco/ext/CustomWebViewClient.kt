package it.antonino.palco.ext

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.ConcertiFragment
import kotlin.system.exitProcess


class CustomWebViewClient(
    private val progressBar: ProgressBar,
    private val okConsent: TextView,
    private val noConsent: TextView,
    private val adviseContainer: ConstraintLayout,
    private val childFragmentManager: FragmentManager
    ) : WebViewClient() {
    init {
        progressBar.visibility = View.VISIBLE
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        progressBar.visibility = View.GONE

        okConsent.setOnClickListener {
            adviseContainer.visibility = View.INVISIBLE
            PalcoApplication.sharedPreferences?.edit()?.putBoolean("ok_consent", true)?.apply()
            childFragmentManager.beginTransaction()
                .replace(R.id.second_container, ConcertiFragment.newInstance())
                .commit()
        }
        noConsent.setOnClickListener {
            exitProcess(0)
        }
    }

}