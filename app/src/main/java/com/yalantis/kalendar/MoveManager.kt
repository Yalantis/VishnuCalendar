package com.yalantis.kalendar

import android.animation.ValueAnimator
import android.view.MotionEvent

class MoveManager(private val viewProvider: ViewProvider) {

    var isBusy = false

    private val topLimit = viewProvider.getWeekBottom(2) + viewProvider.getWeeksMarginTop()

    private val bottomLimit = viewProvider.getViewBottom()

    // in range 2..6 where 1st week on screen equals to 2nd position here
    private var selectedWeek = 4

    private var selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)

    fun onTouch(event: MotionEvent): Boolean {
        return when (event.action) {

            MotionEvent.ACTION_UP -> {
                isBusy = false
                if (isNeedCollapse(event.y)) {
                    collapse()
                } else {
                    expand()
                }
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                isBusy = false
                true
            }

            MotionEvent.ACTION_MOVE -> {
                calculateOffsets(event.y)
                true
            }

            MotionEvent.ACTION_DOWN -> {
                isBusy = true
                // touched drag area
                val a = event.y
                val b = viewProvider.getDragViewTop()
                return true
//                a > b && a < b + DRAG_HEIGHT
            }
            else -> true
        }
    }

    private fun expand() {
        val anim = ValueAnimator
            .ofFloat(viewProvider.getDragViewTop(), bottomLimit.toFloat())
            .setDuration(500)
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.start()
    }

    private fun collapse() {
        val anim = ValueAnimator.ofFloat(viewProvider.getDragViewTop(), topLimit)
            .setDuration(500)
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.start()
    }

    private fun calculateOffsets(touchY: Float) {
        if (touchY >= topLimit) {
            viewProvider.changeViewBottom(touchY.toInt())
            viewProvider.changeDragTop(touchY - DRAG_HEIGHT)

            val dragViewTop = viewProvider.getDragViewTop()
            var weekHeight: Int
            var weekBottom: Float
            for (i in 2..6) {
                weekHeight = viewProvider.getWeekHeight(i)
                weekBottom = viewProvider.getWeekTop(i) + weekHeight
                if (i == selectedWeek) {

                    if (weekBottom >= dragViewTop && dragViewTop <= selectedWeekBottom) {
                        viewProvider.setWeekTop(i, touchY - weekHeight - DRAG_HEIGHT)
                    } else if (weekBottom <= dragViewTop && dragViewTop < selectedWeekBottom) {
                        viewProvider.setWeekTop(i, touchY - weekHeight - DRAG_HEIGHT)
                    }

                    if (weekBottom != selectedWeekBottom && dragViewTop >= selectedWeekBottom) {
                        viewProvider.setWeekTop(i, selectedWeekBottom - weekHeight)
                    }
                }
            }
        } else {
            viewProvider.changeViewBottom(topLimit.toInt())
            viewProvider.changeDragTop(topLimit - DRAG_HEIGHT)
        }
    }

    private fun isNeedCollapse(y: Float) = y < (bottomLimit + topLimit) / 2

    fun setSelectedWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
        selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)
    }
}