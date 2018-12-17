package com.yalantis.kalendar.implementation

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import com.yalantis.kalendar.EMPTY_INT
import com.yalantis.kalendar.interfaces.MoveManager
import com.yalantis.kalendar.interfaces.ViewProvider

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
            animation?.removeAllListeners()
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

    private val defaultPositions = ArrayList<Float>()

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
            .setDuration(500)
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener {
            calculateOffsets(it.animatedValue as Float)
        }
        anim.addListener(busyListener)
        anim.start()
    }

    override fun collapse() {
        val anim = ValueAnimator.ofFloat(viewProvider.getDragTop(), topLimit.toFloat())
            .setDuration(500)
        anim.interpolator = DecelerateInterpolator()
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

            var weekBottom: Float

            for (week in 0 until weekCount) {
                weekBottom = viewProvider.getWeekBottom(week)
                moveWeek(week, weekBottom, touchY, defaultPositions[week])
                checkForDefaultPlace(touchY)
            }

        } else {
            viewProvider.moveWeek(selectedWeek, topLimit.toFloat())
            viewProvider.setViewHeight(minHeight + dragHeight)
            viewProvider.setDragTop(minHeight.toFloat())
            checkForDefaultPlace(touchY)
        }
    }

    private fun applyAlpha(week: Int, touchY: Float, defaultBottom: Float) {
        if (week != selectedWeek) {
            val alpha = (1 - (Math.abs(defaultBottom - touchY) / weekHeight))
            if (alpha in 0f..1f) {
                when (alpha) {
                    in 0f..0.3f -> viewProvider.applyAlpha(week, 0f)
                    in 0.7f..1f -> viewProvider.applyAlpha(week, 1f)
                    else -> viewProvider.applyAlpha(week, alpha)
                }
            }
        }
    }

    private fun checkForDefaultPlace(touchY: Float) {
        var defaultBottom: Float
        var currentBottom: Float

        for (week in 0 until weekCount) {
            currentBottom = viewProvider.getWeekBottom(week)
            defaultBottom = defaultPositions[week]
            if (touchY < defaultBottom - weekHeight && week != selectedWeek) {
                viewProvider.applyAlpha(week, 0f)
            } else if (touchY > defaultBottom && currentBottom != defaultBottom) {
                moveWeek(week, currentBottom, touchY, defaultPositions[week])
            }
        }
    }

    private fun moveWeek(which: Int, weekBottom: Float, touchY: Float, defaultPosition: Float) {
        when {
            weekBottom != defaultPosition && touchY > defaultPosition -> {
                viewProvider.moveWeek(which, defaultPosition)
            }
            weekBottom > touchY -> {
                viewProvider.moveWeek(which, touchY)
                applyAlpha(which, touchY, defaultPosition)
            }
            weekBottom <= touchY && touchY < defaultPosition -> {
                viewProvider.moveWeek(which, touchY)
                applyAlpha(which, touchY, defaultPosition)
            }
        }
    }

    private fun isNeedCollapse(y: Float) = Math.abs(startPoint - y) > weekHeight && isCollapsed.not()

    override fun selectWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
        this.weekCount = viewProvider.getWeekCount()
        refreshDefaultPositions()
    }

    private fun refreshDefaultPositions() {
        //weeks 0-5 (if displayed 6)
        for (i in 0 until weekCount) {
            defaultPositions.add(viewProvider.getWeekBottom(i))
        }
    }
}