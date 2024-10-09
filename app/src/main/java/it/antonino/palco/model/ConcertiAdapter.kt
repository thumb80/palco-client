package it.antonino.palco.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.EventsFragment
import it.antonino.palco.ui.FilterFragment

class ConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return EventsFragment()
            1 -> return FilterFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return PalcoApplication.instance.resources.getString(R.string.concerti_nazionali)
            1 -> return PalcoApplication.instance.resources.getString(R.string.concerti_filter)
        }
        return ""
    }

}