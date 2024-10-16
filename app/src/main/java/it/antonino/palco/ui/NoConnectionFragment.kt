package it.antonino.palco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.antonino.palco.PalcoActivity
import it.antonino.palco.PalcoApplication.Companion.isBatchError
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentBatchErrorBinding
import it.antonino.palco.databinding.FragmentNoConnectionBinding

class NoConnectionFragment: Fragment() {

    private lateinit var noConnectionBinding: FragmentNoConnectionBinding
    private lateinit var batchErrorBinding: FragmentBatchErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isBatchError == true) {
            batchErrorBinding = FragmentBatchErrorBinding.inflate(layoutInflater)
            batchErrorBinding.noConnectionHint.setOnClickListener {
                (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AdviceFragment())
                    .commit()
            }
            return batchErrorBinding.root
        } else {
            noConnectionBinding = FragmentNoConnectionBinding.inflate(layoutInflater)
            noConnectionBinding.noConnectionHint.setOnClickListener {
                (activity as PalcoActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AdviceFragment())
                    .commit()
            }
            return noConnectionBinding.root
        }
    }
}