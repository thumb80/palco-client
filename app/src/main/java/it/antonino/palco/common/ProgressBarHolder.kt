package it.antonino.palco.common

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import it.antonino.palco.R
import it.antonino.palco.ext.dpToPixels

class ProgressBarHolder(
    context: Context,
    imageView: LottieAnimationView,
    progressBarLayout: LinearLayout
) {

    private val animationView: LottieAnimationView = imageView
    private val overlayLayout: RelativeLayout
    private val imageViewLayout: LinearLayout = progressBarLayout
    private val mainContent: ViewGroup
    private val matchParent: ViewGroup.LayoutParams
    private val matchParentOverlay: RelativeLayout.LayoutParams

    init {
        this.imageViewLayout.addView(this.animationView)
        this.matchParent = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.matchParentOverlay = RelativeLayout.LayoutParams(matchParent)
        this.matchParentOverlay.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        this.matchParentOverlay.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        this.mainContent = (context as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup
        this.overlayLayout = RelativeLayout(context)
        this.overlayLayout.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                overlayLayout.addView(v)
            }

            override fun onViewDetachedFromWindow(v: View) {
                overlayLayout.removeView(progressBarLayout)
            }
        })
    }

    fun show(activity: Activity) {
        activity.runOnUiThread {
            if (imageViewLayout.parent != null) (imageViewLayout.parent as ViewGroup).removeView(imageViewLayout)
            mainContent.addView(imageViewLayout, matchParent)
        }
    }

    fun hide(activity: Activity) {
        activity.runOnUiThread {
            mainContent.removeView(imageViewLayout)
        }
    }

    class Builder {
        private var lottieView: LottieAnimationView? = null
        private var layout: LinearLayout? = null
        private var layoutBackColor: Int? = null
        private var indeterminateColor: Int? = null

        fun setLayoutBackColor(color: Int): Builder {
            this.layoutBackColor = color
            return this
        }

        fun setIndeterminateColor(color: Int): Builder {
            this.indeterminateColor = color
            return this
        }

        fun build(context: Context): ProgressBarHolder {

            if (lottieView == null) {
                lottieView = LottieAnimationView(context)
                lottieView?.layoutParams = LinearLayout.LayoutParams(100.dpToPixels(), 100.dpToPixels())
                lottieView?.apply{
                    setAnimation(R.raw.ic_music)
                    repeatCount = LottieDrawable.INFINITE
                    playAnimation()
                }
                lottieView?.setBackgroundColor(Color.TRANSPARENT)
            }

            if (layout == null) {
                layout = LinearLayout(context)
                layout!!.gravity = Gravity.CENTER
                layout!!.isClickable = true
            }

            if (layoutBackColor != null) {
                layout!!.setBackgroundColor(layoutBackColor!!)
            }

            return ProgressBarHolder(context, lottieView!!, layout!!)
        }
    }

}
