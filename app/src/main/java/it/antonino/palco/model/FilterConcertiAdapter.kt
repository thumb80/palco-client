package it.antonino.palco.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.FilterArtistFragment
import it.antonino.palco.ui.FilterCityFragment
import org.koin.java.KoinJavaComponent.inject

class FilterConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val context: Context by inject(Context::class.java)

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
            0 -> return context.getString(R.string.filter_city)
            1 -> return context.getString(R.string.filter_artist)
        }
        return ""
    }

}
