package com.example.vocabmanager.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.R

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.Callback() {
    private var mClearPaint: Paint
    private var mBackground: ColorDrawable = ColorDrawable()
    private var backgroundColor: Int? = null
    private var deleteDrawable: Drawable
    private var intrinsicWidth: Int
    private var intrinsicHeight: Int

    init {
        backgroundColor = ContextCompat.getColor(context, R.color.course_swipe)
        mClearPaint = Paint()
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_trash)!!
        intrinsicWidth = deleteDrawable.intrinsicWidth
        intrinsicHeight = deleteDrawable.intrinsicHeight
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView: View = viewHolder.itemView
        val itemHeight: Int = itemView.height

        val isCancelled: Boolean = dX == 0f && !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(
                c, itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        mBackground.color = backgroundColor!!
        mBackground.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        mBackground.draw(c)

        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        if(mBackground.bounds.left <= deleteIconLeft) {
            deleteDrawable.setBounds(
                deleteIconLeft,
                deleteIconTop,
                deleteIconRight,
                deleteIconBottom
            )
            deleteDrawable.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        canvas.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
}