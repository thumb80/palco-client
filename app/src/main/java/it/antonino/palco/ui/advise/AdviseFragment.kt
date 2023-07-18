package it.antonino.palco.ui.advise

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.antonino.palco.R
import it.antonino.palco.ui.ConcertiFragment
import kotlinx.android.synthetic.main.fragment_advise.*
import java.io.IOException
import java.io.InputStream


class AdviseFragment: Fragment() {

    private lateinit var builder: StringBuilder
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val `in`: InputStream = resources.openRawResource(R.raw.advise)

        try {
            var count = 0
            val bytes = ByteArray(32768)
            builder = StringBuilder()
            while (`in`.read(bytes, 0, 32768).also { count = it } > 0) {
                builder.append(String(bytes, 0, count))
            }
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        advise_text.text = builder.toString()

        no_consent.setOnClickListener {
            System.exit(0)
        }

        ok_consent.setOnClickListener {
            sharedPreferences?.edit()?.putBoolean("ok_consent", true)?.apply()
            advise_container.visibility = View.INVISIBLE
            childFragmentManager.beginTransaction()
                .replace(R.id.second_container, ConcertiFragment.newInstance())
                .commit()
        }
    }
}