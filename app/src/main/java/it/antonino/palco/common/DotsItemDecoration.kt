package it.antonino.palco.common

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.antonino.palco.ext.dpToPixels
import org.jetbrains.annotations.NotNull

class DotsItemDecoration(
    radius: Int,
    padding: Int,
    indicatorHeight: Int,
    @ColorInt colorInactive: Int,
    @ColorInt colorActive: Int
) : RecyclerView.ItemDecoration() {

    private val indicatorHeight: Int
    private val indicatorItemPadding: Int
    private val radius: Int
    private val inactivePaint = Paint()
    private val activePaint = Paint()

    var activePosition = 0

    override fun onDrawOver(
        @NotNull c: Canvas,
        @NotNull parent: RecyclerView,
        @NotNull state: RecyclerView.State
    ) {
        super.onDrawOver(c, parent, state)
        val adapter = parent.adapter ?: return
        val itemCount = adapter.itemCount

        // center horizontally, calculate width and subtract half from center
        val totalLength = (radius * 2 * itemCount).toFloat()
        val paddingBetweenItems = (Math.max(0, itemCount - 2) * indicatorItemPadding).toFloat()
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2f
        // center vertically in the allotted space
        val indicatorPosY = parent.y + indicatorItemPadding.toFloat()

        if (itemCount == 2) {
            return
        }
        else if (itemCount > 24) {
            val indicatorStart = parent.width / 2f
            drawActive(c, indicatorStart, indicatorPosY, itemCount)
        }

        else {
            drawInactiveDots(c, indicatorStartX, indicatorPosY, itemCount - 1)

            activePosition =
                if ((parent.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() < 0)
                    (parent.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
                else
                    (parent.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

            if (activePosition == RecyclerView.NO_POSITION) {
                return
            }

            // find offset of active page if the user is scrolling
            val activeChild = parent.layoutManager!!.findViewByPosition(activePosition) ?: return
            if (activePosition == 0)
                drawActiveDot(c, indicatorStartX, indicatorPosY, activePosition)
            else
                drawActiveDot(c, indicatorStartX, indicatorPosY, activePosition - 1)
        }
    }

    private fun drawInactiveDots(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        // width of item indicator including padding
        val itemWidth = (radius * 2 + indicatorItemPadding).toFloat()
        var start = indicatorStartX + radius
        for (i in 0 until itemCount) {
            c.drawCircle(start, indicatorPosY, radius.toFloat(), inactivePaint)
            start += itemWidth
        }
    }

    private fun drawActiveDot(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        highlightPosition: Int
    ) {
        // width of item indicator including padding
        val itemWidth = (radius * 2 + indicatorItemPadding).toFloat()
        val highlightStart = indicatorStartX + radius + itemWidth * highlightPosition
        c.drawCircle(highlightStart, indicatorPosY, radius.toFloat(), activePaint)
    }

    private fun drawActive(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        val paint = Paint()
        paint.color = activePaint.color
        paint.style = Paint.Style.FILL
        paint.textSize = indicatorHeight.toFloat()
        paint.textAlign = Paint.Align.CENTER
        c.drawText("Ci sono $itemCount concerti", indicatorStartX, indicatorPosY, paint)
    }

    override fun getItemOffsets(
        @NotNull outRect: Rect,
        @NotNull view: View,
        @NotNull parent: RecyclerView,
        @NotNull state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,RecyclerView.LayoutParams.MATCH_PARENT)

        if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount!! - 1)
            outRect.right = (parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width/14
        /*if (parent.getChildAdapterPosition(view) == 0) {
            // Invalidate decorations when this view width has changed
            // If we have more items, use the spacing provided
            outRect.centerX()
            if (parent.adapter?.itemCount!! > 1) {
                outRect.left = 24.dpToPixels()
                outRect.right = 24.dpToPixels()
            }
        } else {
            outRect.right = 24.dpToPixels()
        }*/
    }

    init {
        val strokeWidth = Resources.getSystem().displayMetrics.density * 1
        this.radius = radius
        inactivePaint.strokeCap = Paint.Cap.ROUND
        inactivePaint.strokeWidth = strokeWidth
        inactivePaint.style = Paint.Style.STROKE
        inactivePaint.isAntiAlias = true
        inactivePaint.color = colorInactive
        activePaint.strokeCap = Paint.Cap.ROUND
        activePaint.strokeWidth = strokeWidth
        activePaint.style = Paint.Style.FILL
        activePaint.isAntiAlias = true
        activePaint.color = colorActive
        indicatorItemPadding = padding
        this.indicatorHeight = indicatorHeight
    }
}