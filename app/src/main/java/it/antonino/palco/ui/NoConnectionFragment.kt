package it.antonino.palco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.antonino.palco.PalcoActivity
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentBatchErrorBinding
import it.antonino.palco.databinding.FragmentNoConnectionBinding
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class NoConnectionFragment: Fragment() {

    private lateinit var noConnectionBinding: FragmentNoConnectionBinding
    private lateinit var batchErrorBinding: FragmentBatchErrorBinding

    val viewModel: SharedViewModel by activityViewModel<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (viewModel.batchEnded.value == false) {
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