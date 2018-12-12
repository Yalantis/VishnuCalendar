package com.yalantis.kalendar

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.MotionEvent

class MoveManager(private val viewProvider: ViewProvider) {

    val TAG = "TAGGG"

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
        if (touchY >= topLimit) {

            val newHeight = totalHeight - Math.abs(touchY - startPoint)

            viewProvider.setViewHeight(newHeight.toInt())
            viewProvider.setDragTop(touchY)

            var weekBottom: Float
            for (i in 2..6) {
                weekBottom = viewProvider.getWeekBottom(i)

                if (i == selectedWeek) {
                    if (weekBottom >= touchY && touchY <= selectedWeekBottom) {
                        viewProvider.setWeekTop(i, touchY - weekHeight)
                        viewProvider.setWeekHeight(i, weekHeight)

                    } else if (weekBottom <= touchY && touchY < selectedWeekBottom) {
                        viewProvider.setWeekTop(i, touchY - weekHeight)
                    }

                    if (weekBottom != selectedWeekBottom && touchY >= selectedWeekBottom) {
                        viewProvider.setWeekTop(i, selectedWeekBottom - weekHeight)
                    }
                }
            }
        }
    }

    private fun isNeedCollapse(y: Float) = Math.abs(startPoint - y) > weekHeight && isCollapsed.not()

    fun setSelectedWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
        selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)
    }
}