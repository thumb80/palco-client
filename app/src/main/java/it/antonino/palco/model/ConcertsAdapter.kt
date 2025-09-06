package it.antonino.palco.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.antonino.palco.ui.EventsFragment
import it.antonino.palco.ui.FilterFragment

class ConcertsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return EventsFragment()
            1 -> return FilterFragment()
        }
        return Fragment()
    }

}