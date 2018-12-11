package com.yalantis.kalendar

interface ViewProvider {

    fun setViewBottom(newBottom: Int)

    fun getTopLimit(): Int

    fun getBottomLimit(): Int

    fun getDragTop(): Float

    fun setDragTop(newDragTop: Float)

    fun getWeekBottom(position: Int): Float

    fun getWeekHeight(position: Int): Int

    fun getWeekTop(position: Int): Float

    fun setWeekTop(position: Int, newTop: Float)

    fun moveStateChanged(collapsed: Boolean)
}