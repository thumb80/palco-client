package it.antonino.palco.common

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.ext.dpToPixels
import org.jetbrains.annotations.NotNull

class DotsLocalItemDecoration() : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        @NotNull outRect: Rect,
        @NotNull view: View,
        @NotNull parent: RecyclerView,
        @NotNull state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount!! - 1)
            outRect.right = (parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width/14
        /*if (parent.getChildAdapterPosition(view) == 0) {
            // Invalidate decorations when this view width has changed
            outRect.left = 24.dpToPixels()
            // If we have more items, use the spacing provided
            if (parent.adapter?.itemCount!! > 1) {
                outRect.right = 12.dpToPixels()
            } else {
                // Otherwise, make sure this to fill the whole width with the decoration
                outRect.right = outRect.left
            }
        } else if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount!! - 1) {
            // Invalidate decorations when this view width has changed
            outRect.right = 24.dpToPixels()
            outRect.left = 12.dpToPixels()
        } else {
            outRect.left = 12.dpToPixels()
            outRect.right = 12.dpToPixels()
        }*/

    }

}