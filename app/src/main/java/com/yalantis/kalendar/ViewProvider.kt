package com.yalantis.kalendar

interface ViewProvider {

    fun changeViewBottom(newBottom: Int)

    fun getViewBottom(): Int

    fun changeDragTop(newDragTop: Float)

    fun getDragViewTop(): Float

    fun getWeekBottom(position: Int): Float

    fun getWeekHeight(position: Int): Int

    fun getWeekTop(position: Int): Float

    fun setWeekTop(position: Int, newTop: Float)

    fun getWeeksMarginTop(): Int
}