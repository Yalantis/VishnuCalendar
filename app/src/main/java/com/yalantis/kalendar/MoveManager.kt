package com.yalantis.kalendar

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.MotionEvent

class MoveManager(private val viewProvider: ViewProvider) {

    var isBusy = false

    private val busyListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {
            isBusy = true
        }
        override fun onAnimationEnd(animation: Animator?) {
            isBusy = false
        }
    }

    private val topLimit = viewProvider.getTopLimit()

    private val bottomLimit = viewProvider.getBottomLimit()

    private val weekHeight = viewProvider.getWeekHeight(2)

    // in range 2..6 where 1st week on screen equals to 2nd position here
    private var selectedWeek = 4

    private var selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)

    fun onTouch(event: MotionEvent): Boolean {
        return when (event.action) {

            MotionEvent.ACTION_UP -> {
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
                return true
            }
            else -> false
        }
    }

    private fun expand() {
        val anim = ValueAnimator
            .ofFloat(viewProvider.getDragTop(), bottomLimit.toFloat())
            .setDuration(300)
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.addListener(busyListener)
        anim.start()
    }

    private fun collapse() {
        val anim = ValueAnimator.ofFloat(viewProvider.getDragTop(), topLimit.toFloat())
            .setDuration(300)
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.addListener(busyListener)
        anim.start()
    }

    private fun calculateOffsets(touchY: Float) {
        if (touchY >= topLimit) {
            viewProvider.setViewBottom(touchY.toInt() + DRAG_HEIGHT)
            viewProvider.setDragTop(touchY)

            val dragViewTop = viewProvider.getDragTop()
            var weekBottom: Float
            for (i in 2..6) {
                weekBottom = viewProvider.getWeekBottom(i)
                if (i == selectedWeek) {

                    if (weekBottom >= dragViewTop && dragViewTop <= selectedWeekBottom) {
                        viewProvider.setWeekTop(i, touchY - weekHeight)
                    } else if (weekBottom <= dragViewTop && dragViewTop < selectedWeekBottom) {
                        viewProvider.setWeekTop(i, touchY - weekHeight)
                    }

                    if (weekBottom != selectedWeekBottom && dragViewTop >= selectedWeekBottom) {
                        viewProvider.setWeekTop(i, selectedWeekBottom - weekHeight)
                    }
                }
            }
        } else {
            viewProvider.setViewBottom(topLimit + DRAG_HEIGHT)
            viewProvider.setDragTop(topLimit.toFloat() - DRAG_HEIGHT)
        }
    }

    private fun isNeedCollapse(y: Float) = y < (bottomLimit + topLimit) / 2

    fun setSelectedWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
        selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)
    }
}