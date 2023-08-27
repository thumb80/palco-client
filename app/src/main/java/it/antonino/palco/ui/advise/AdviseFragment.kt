package it.antonino.palco.ui.advise

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.antonino.palco.MainActivity
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentAdviseBinding
import it.antonino.palco.ui.ConcertiFragment
import java.io.IOException
import java.io.InputStream
import kotlin.system.exitProcess


class AdviseFragment: Fragment() {

    private lateinit var builder: StringBuilder
    private lateinit var binding: FragmentAdviseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    ): View {
        binding = FragmentAdviseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PalcoApplication.instance.sharedPreferences?.getBoolean("ok_consent", false) == true) {
            binding.adviseContainer.visibility = View.INVISIBLE
            childFragmentManager.beginTransaction()
                .replace(R.id.second_container, ConcertiFragment.newInstance())
                .commit()
        } else {
            binding.adviseText.text = builder.toString()

            binding.noConsent.setOnClickListener {
                exitProcess(0)
            }

            binding.okConsent.setOnClickListener {
                binding.adviseContainer.visibility = View.INVISIBLE
                PalcoApplication.instance.sharedPreferences?.edit()?.putBoolean("ok_consent", true)?.apply()
                childFragmentManager.beginTransaction()
                    .replace(R.id.second_container, ConcertiFragment.newInstance())
                    .commit()
            }
        }
    }
}