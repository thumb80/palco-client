package it.antonino.palco.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.antonino.palco.ui.FilterArtistFragment
import it.antonino.palco.ui.FilterCityFragment

class FilterConcertsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return FilterCityFragment()
            1 -> return FilterArtistFragment()
        }
        return Fragment()
    }
}
