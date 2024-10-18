package it.antonino.palco.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import it.antonino.palco.R
import it.antonino.palco.adapter.FilterConcertiAdapter
import it.antonino.palco.databinding.FragmentFilterBinding
import it.antonino.palco.util.Constant

class FilterFragment : Fragment()  {

    private lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewPager = view.findViewById<ViewPager>(R.id.filter_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.filter_tab_layout)
        val filterConcertiViewPagerAdapter = FilterConcertiAdapter(childFragmentManager)

        tabLayout.setTabTextColors(requireContext().getColor(R.color.colorPrimary), requireContext().getColor(R.color.colorWhite))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                setTabTypeface(tab, ResourcesCompat.getFont(requireContext(), R.font.gotham_bold))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setTabTypeface(tab, ResourcesCompat.getFont(requireContext(), R.font.gotham_medium))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        viewPager.adapter = filterConcertiViewPagerAdapter
        viewPager.setCurrentItem(Constant.currentItem,true)
        viewPager.offscreenPageLimit = Constant.offscreenPageLimit
        tabLayout.setupWithViewPager(viewPager, true)

    }

    private fun setTabTypeface(tab: TabLayout.Tab?, typeface: Typeface?) {
        for (i in 0 until tab?.view?.childCount!!) {
            val tabViewChild = tab.view.getChildAt(i)
            if (tabViewChild is TextView) tabViewChild.typeface = typeface
        }
    }

}
