package it.antonino.palco.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import it.antonino.palco.R
import it.antonino.palco.adapter.FilterConcertiAdapter
import it.antonino.palco.ext.CustomViewPager
import it.antonino.palco.util.Constant.currentItem
import it.antonino.palco.util.Constant.offscreenPageLimit

class FilterFragment : Fragment()  {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewPager = view.findViewById<CustomViewPager>(R.id.filter_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.filter_tab_layout)
        val filterConcertiViewPagerAdapter = FilterConcertiAdapter(childFragmentManager)

        viewPager.setPagingEnabled(false)
        viewPager.adapter = filterConcertiViewPagerAdapter
        viewPager.setCurrentItem(currentItem,true)
        viewPager.offscreenPageLimit = offscreenPageLimit
        tabLayout.setupWithViewPager(viewPager, true)

    }

}
