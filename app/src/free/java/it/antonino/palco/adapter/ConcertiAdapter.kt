package it.antonino.palco.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.europe.EuropeFragment
import it.antonino.palco.ui.filter.FilterFragment
import it.antonino.palco.ui.local.LocalFragment
import it.antonino.palco.ui.national.NationalFragment

class ConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return NationalFragment()
            1 -> return LocalFragment()
            //2 -> return EuropeFragment()
            2 -> return FilterFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return PalcoApplication.instance.resources.getString(R.string.concerti_nazionali)
            1 -> return PalcoApplication.instance.resources.getString(R.string.concerti_locali)
            //2 -> return PalcoApplication.instance.resources.getString(R.string.concerti_internazionali)
            2 -> return PalcoApplication.instance.getString(R.string.concerti_filter)
        }
        return ""
    }

}