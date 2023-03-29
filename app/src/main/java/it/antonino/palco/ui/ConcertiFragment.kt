package it.antonino.palco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import it.antonino.palco.R
import it.antonino.palco.adapter.ConcertiAdapter
import it.antonino.palco.ext.CustomViewPager

class ConcertiFragment: Fragment()  {

    companion object {
        fun newInstance() = ConcertiFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_concerti, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewPager = view.findViewById<CustomViewPager>(R.id.pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val concertiViewPagerAdapter = ConcertiAdapter(childFragmentManager)

        viewPager.setPagingEnabled(false)
        viewPager.adapter = concertiViewPagerAdapter
        viewPager.setCurrentItem(0,true)
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager, true)

    }

}