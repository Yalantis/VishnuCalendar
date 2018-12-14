package com.yalantis.kalendar

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.MotionEvent

class MoveManagerImpl(private val viewProvider: ViewProvider) : MoveManager {

    override var isBusy = false

    override var isCollapsed = false

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

    private val dragHeight = viewProvider.getDragHeight()

    private val minHeight = viewProvider.viewMinHeight()

    private val viewTop = viewProvider.getViewTop()

    private var weekCount = EMPTY_INT

    private var bottomLimit = viewProvider.getBottomLimit()

    private var totalHeight = bottomLimit - viewTop

    private val weekHeight = viewProvider.getWeekHeight()

    private var selectedWeek = EMPTY_INT

    private var selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)

    override fun onTouch(event: MotionEvent): Boolean {
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
                weekCount = viewProvider.getWeekCount()
                return true
            }
            else -> false
        }
    }

    override fun setCurrentMaxHeight(totHeight: Int) {
        bottomLimit = totHeight
        totalHeight = bottomLimit - topLimit
    }

    override fun expand() {
        val anim = ValueAnimator
            .ofFloat(viewProvider.getDragTop(), bottomLimit + dragHeight.toFloat())
            .setDuration(300)
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.addListener(busyListener)
        anim.start()
    }

    override fun collapse() {
        val anim = ValueAnimator.ofFloat(viewProvider.getDragTop(), topLimit.toFloat())
            .setDuration(300)
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.addListener(busyListener)
        anim.start()
    }

    private fun calculateOffsets(touchY: Float) {
        val newHeight = (totalHeight * (touchY / totalHeight)) + dragHeight

        if (touchY >= topLimit) {

            viewProvider.setViewHeight(newHeight.toInt())
            viewProvider.setDragTop(touchY)


            for (i in 0..weekCount) {
                if (i == selectedWeek) {
                    val weekBottom = viewProvider.getWeekBottom(i)

                    if (weekBottom > touchY) {
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
            viewProvider.setViewHeight(minHeight + dragHeight)
            viewProvider.setDragTop(minHeight.toFloat())
        }
    }

    private fun isNeedCollapse(y: Float) = Math.abs(startPoint - y) > weekHeight && isCollapsed.not()

    override fun selectWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
        selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)
    }
}