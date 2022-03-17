package it.antonino.palco.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.filters.FiltersFragment
import it.antonino.palco.ui.international.InternationalFragment
import it.antonino.palco.ui.local.LocalFragment
import it.antonino.palco.ui.national.NationalFragment

class ConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return NationalFragment()
            1 -> return LocalFragment()
            2 -> return InternationalFragment()
            3 -> return FiltersFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int = 4

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return PalcoApplication.instance.resources.getString(R.string.concerti_nazionali)
            1 -> return PalcoApplication.instance.resources.getString(R.string.concerti_locali)
            2 -> return PalcoApplication.instance.resources.getString(R.string.concerti_internazionali)
            3 -> return PalcoApplication.instance.resources.getString(R.string.filters)
        }
        return ""
    }

}