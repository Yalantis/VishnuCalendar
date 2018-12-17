package com.yalantis.kalendar.interfaces

interface ViewProvider {

    fun setViewHeight(newBottom: Int)

    fun viewHeight(): Int

    fun getTopLimit(): Int

    fun viewMinHeight(): Int

    fun getBottomLimit(): Int

    fun getDragTop(): Float

    fun setDragTop(newDragTop: Float)

    fun getWeekBottom(position: Int): Float

    fun getWeekHeight(): Int

    fun getWeekTop(position: Int): Float

    fun moveWeek(position: Int, newTop: Float)

    fun moveStateChanged(collapsed: Boolean)

    fun getViewTop(): Int

    fun setWeekHeight(i: Int, weekHeight: Int)

    fun getDragHeight(): Int

    fun getWeekCount(): Int

    fun applyAlpha(week: Int, alpha: Float)
}