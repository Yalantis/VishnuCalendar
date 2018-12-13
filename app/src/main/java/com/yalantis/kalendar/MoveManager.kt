package com.yalantis.kalendar

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.MotionEvent

class MoveManager(private val viewProvider: ViewProvider) {

    var isBusy = false

    var isCollapsed = false

    private var startPoint = 0f

    private val busyListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {
            isBusy = true
        }

        override fun onAnimationEnd(animation: Animator?) {
            isBusy = false
            isCollapsed = isCollapsed.not()
            viewProvider.moveStateChanged(isCollapsed)
        }
    }

    private val topLimit = viewProvider.getTopLimit()

    private val minHeight = viewProvider.viewMinHeight()

    private val viewTop = viewProvider.getViewTop()

    private val bottomLimit = viewProvider.getBottomLimit()

    private val totalHeight = bottomLimit - viewTop

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
                startPoint = event.y
                return true
            }
            else -> false
        }
    }

    fun expand() {
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
        val newHeight = totalHeight * (touchY / totalHeight)

        if (touchY >= topLimit) {

            viewProvider.setViewHeight(newHeight.toInt() + DRAG_HEIGHT)
            viewProvider.setDragTop(touchY) //viewProvider.getBottomLimit().toFloat() - DRAG_HEIGHT


            for (i in 2..6) {
                if (i == selectedWeek) {
                    val weekBottom = viewProvider.getWeekBottom(i)
                    if (weekBottom >= touchY && touchY <= selectedWeekBottom) {
                        viewProvider.moveWeek(i, touchY)

                    } else if (weekBottom <= touchY && touchY < selectedWeekBottom) {
                        viewProvider.moveWeek(i, touchY)
                    }

                    if (weekBottom != selectedWeekBottom && touchY >= selectedWeekBottom) {
                        viewProvider.moveWeek(i, selectedWeekBottom)
                    }
                }
            }
        } else {
            viewProvider.moveWeek(selectedWeek, topLimit.toFloat() )
            viewProvider.setViewHeight(minHeight + DRAG_HEIGHT)
            viewProvider.setDragTop(minHeight.toFloat())
        }
    }

    private fun isNeedCollapse(y: Float) = Math.abs(startPoint - y) > weekHeight && isCollapsed.not()

    fun setSelectedWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
        selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)
    }
}