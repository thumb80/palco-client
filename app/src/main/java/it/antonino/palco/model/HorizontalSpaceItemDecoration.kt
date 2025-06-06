package it.antonino.palco.model

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = space

        // Optional: Add space to the left of the first item
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = space
        }
    }
}