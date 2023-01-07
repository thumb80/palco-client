package it.antonino.palco.ext

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager


class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private var myEnabled = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (myEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (myEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.myEnabled = enabled
    }

    override fun getCurrentItem(): Int {
        return super.getCurrentItem()
    }

}
