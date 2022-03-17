package it.antonino.palco.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.filters.artist.FiltersArtistFragment
import it.antonino.palco.ui.filters.city.FiltersCityFragment
import it.antonino.palco.ui.filters.month.FiltersMonthFragment

class FiltersAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> FiltersMonthFragment()
            1 -> FiltersCityFragment()
            2 -> FiltersArtistFragment()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return PalcoApplication.instance.resources.getString(R.string.filters_month)
            1 -> return PalcoApplication.instance.resources.getString(R.string.filters_city)
            2 -> return PalcoApplication.instance.resources.getString(R.string.filters_artist)
        }
        return ""
    }

}