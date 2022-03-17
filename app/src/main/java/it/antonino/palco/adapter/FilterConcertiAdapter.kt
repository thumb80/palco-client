package it.antonino.palco.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.filter.artist.FilterArtistFragment
import it.antonino.palco.ui.filter.city.FilterCityFragment
import it.antonino.palco.ui.filter.month.FilterMonthFragment

class FilterConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return FilterMonthFragment()
            1 -> return FilterCityFragment()
            2 -> return FilterArtistFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return PalcoApplication.instance.resources.getString(R.string.filter_month)
            1 -> return PalcoApplication.instance.resources.getString(R.string.filter_city)
            2 -> return PalcoApplication.instance.resources.getString(R.string.filter_artist)
        }
        return ""
    }

}