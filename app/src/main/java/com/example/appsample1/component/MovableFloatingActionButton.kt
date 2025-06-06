package com.example.appsample1.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.MarginLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class MovableFloatingActionButton : FloatingActionButton, OnTouchListener {
    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val layoutParams = view.layoutParams as MarginLayoutParams

        val action = motionEvent.action
        if (action == MotionEvent.ACTION_DOWN) {
            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY
            dX = view.x - downRawX
            dY = view.y - downRawY

            return true // Consumed
        } else if (action == MotionEvent.ACTION_MOVE) {
            val viewWidth = view.width
            val viewHeight = view.height

            val viewParent = view.parent as View
            val parentWidth = viewParent.width
            val parentHeight = viewParent.height

            var newX = motionEvent.rawX + dX
            newX = max(
                layoutParams.leftMargin.toDouble(),
                newX.toDouble()
            ).toFloat() // Don't allow the FAB past the left hand side of the parent
            newX = min(
                (parentWidth - viewWidth - layoutParams.rightMargin).toDouble(),
                newX.toDouble()
            ).toFloat() // Don't allow the FAB past the right hand side of the parent

            var newY = motionEvent.rawY + dY
            newY = max(
                layoutParams.topMargin.toDouble(),
                newY.toDouble()
            ).toFloat() // Don't allow the FAB past the top of the parent
            newY = min(
                (parentHeight - viewHeight - layoutParams.bottomMargin).toDouble(),
                newY.toDouble()
            ).toFloat() // Don't allow the FAB past the bottom of the parent

            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()

            return true // Consumed
        } else if (action == MotionEvent.ACTION_UP) {
            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY

            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY

            return if (abs(upDX.toDouble()) < CLICK_DRAG_TOLERANCE && abs(upDY.toDouble()) < CLICK_DRAG_TOLERANCE) { // A click
                 performClick()
            } else { // A drag
                true // Consumed
            }
        } else {
            return super.onTouchEvent(motionEvent)
        }
    }

    companion object {
        private const val CLICK_DRAG_TOLERANCE =
            10f // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    }
}