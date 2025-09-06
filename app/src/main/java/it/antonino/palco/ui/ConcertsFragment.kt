package it.antonino.palco.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.antonino.palco.R
import it.antonino.palco.databinding.FragmentConcertiBinding
import it.antonino.palco.model.ConcertsAdapter

class ConcertsFragment: Fragment() {

    private lateinit var binding: FragmentConcertiBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConcertiBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tabTitles = listOf(getString(R.string.concerti_nazionali),getString(R.string.concerti_filter))

        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val concertsViewPagerAdapter = ConcertsAdapter(requireActivity())

        tabLayout.setTabTextColors(requireContext().resources.getColor(R.color.colorPrimary, null), requireContext().resources.getColor(R.color.colorWhite, null))
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

        viewPager.adapter = concertsViewPagerAdapter
        viewPager.setCurrentItem(0, true)
        viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }

    private fun setTabTypeface(tab: TabLayout.Tab?, typeface: Typeface?) {
        for (i in 0 until tab?.view?.childCount!!) {
            val tabViewChild = tab.view.getChildAt(i)
            if (tabViewChild is TextView) tabViewChild.typeface = typeface
        }
    }

}