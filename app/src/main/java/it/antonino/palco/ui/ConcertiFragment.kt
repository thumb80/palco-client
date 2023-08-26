package it.antonino.palco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import it.antonino.palco.R
import it.antonino.palco.adapter.ConcertiAdapter
import it.antonino.palco.databinding.FragmentConcertiBinding
import it.antonino.palco.ext.CustomViewPager
import it.antonino.palco.util.Constant.offscreenPageLimit

class ConcertiFragment: Fragment()  {

    private lateinit var binding: FragmentConcertiBinding

    companion object {
        fun newInstance() = ConcertiFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConcertiBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewPager = view.findViewById<CustomViewPager>(R.id.pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val concertiViewPagerAdapter = ConcertiAdapter(childFragmentManager)

        viewPager.setPagingEnabled(false)
        viewPager.adapter = concertiViewPagerAdapter
        viewPager.setCurrentItem(0,true)
        viewPager.offscreenPageLimit = offscreenPageLimit
        tabLayout.setupWithViewPager(viewPager, true)

    }

}