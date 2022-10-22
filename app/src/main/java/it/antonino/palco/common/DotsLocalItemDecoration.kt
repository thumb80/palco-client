package it.antonino.palco.common

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.util.Constant.defaultDisplayFactor
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
            outRect.right = (parent.context.getSystemService(
                Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.width/defaultDisplayFactor
    }
}
