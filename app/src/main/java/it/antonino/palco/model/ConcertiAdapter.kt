package it.antonino.palco.model

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import it.antonino.palco.PalcoApplication
import it.antonino.palco.R
import it.antonino.palco.ui.EventsFragment
import it.antonino.palco.ui.FilterFragment
import org.koin.java.KoinJavaComponent.inject

class ConcertiAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val context: Context by inject(Context::class.java)

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
            0 -> return context.getString(R.string.concerti_nazionali)
            1 -> return context.getString(R.string.concerti_filter)
        }
        return ""
    }

}