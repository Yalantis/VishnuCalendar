package com.yalantis.kalendar

interface ViewProvider {

    fun setViewHeight(newBottom: Int)

    fun viewHeight(): Int

    fun getTopLimit(): Int

    fun getBottomLimit(): Int

    fun getDragTop(): Float

    fun setDragTop(newDragTop: Float)

    fun getWeekBottom(position: Int): Float

    fun getWeekHeight(position: Int): Int

    fun getWeekTop(position: Int): Float

    fun setWeekTop(position: Int, newTop: Float)

    fun moveStateChanged(collapsed: Boolean)

    fun getViewTop(): Int

    fun setWeekBottom(i: Int, fl: Float)

    fun setWeekHeight(i: Int, weekHeight: Int)
}