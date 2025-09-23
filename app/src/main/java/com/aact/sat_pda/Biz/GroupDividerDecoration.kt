package com.aact.sat_pda.Biz

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.apply
import kotlin.ranges.until

class GroupDividerDecoration(
    private val dividerHeightDp: Int = 2,
    private val dividerColor: Int = Color.WHITE
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = dividerColor
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter ?: return

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) { // 마지막 아이템은 제외
            val view = parent.getChildAt(i)
            val params = view.layoutParams as RecyclerView.LayoutParams

            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val top = view.bottom + params.bottomMargin
            val bottom = top + dpToPx(parent, dividerHeightDp)

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }

    private fun dpToPx(view: View, dp: Int): Int {
        val density = view.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
