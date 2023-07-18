package it.antonino.palco.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.filter.artist.FilterArtistFragment
import it.antonino.palco.ui.filter.city.FilterCityFragment

class FilterConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return FilterCityFragment()
            1 -> return FilterArtistFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return PalcoApplication.instance.resources.getString(R.string.filter_city)
            1 -> return PalcoApplication.instance.resources.getString(R.string.filter_artist)
        }
        return ""
    }

}
