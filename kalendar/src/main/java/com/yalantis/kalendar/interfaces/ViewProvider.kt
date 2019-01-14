package com.yalantis.kalendar.interfaces

interface ViewProvider {

    /**
     * Change root view total height
     */

    fun setViewHeight(newBottom: Int)

    /**
     * Return root view total height
     */

    fun getViewTotalHeight(): Int

    /**
     * Return scroll top limit
     */

    fun getTopLimit(): Int

    /**
     * Return return view min height
     */

    fun viewMinHeight(): Int

    /**
     * Return scroll bottom limit
     */

    fun getBottomLimit(): Int

    /**
     * Return drag area top side
     */

    fun getDragTop(): Float

    /**
     * Change drag area top side
     */

    fun setDragTop(newDragTop: Float)

    /**
     * Return week bottom position
     */

    fun getWeekBottom(position: Int): Float

    /**
     * Return week height
     */

    fun getWeekHeight(): Int

    /**
     * Return week top position
     */

    fun getWeekTop(position: Int): Float

    /**
     * Applying new top position to week
     */

    fun moveWeek(position: Int, newTop: Float)

    /**
     * Change current state of move
     */

    fun moveStateChanged(collapsed: Boolean, selectedWeek: Int)

    /**
     * Return root view top position
     */

    fun getViewTop(): Int

    /**
     * Change week height
     */

    fun setWeekHeight(i: Int, weekHeight: Int)

    /**
     * Return drag area height
     */

    fun getDragHeight(): Int

    /**
     * Return displaying weeks count
     */

    fun getWeekCount(): Int

    /**
     * Applies alpha to the week
     */

    fun applyAlpha(week: Int, alpha: Float)

    /**
     * Request for weeks default positions
     */

    fun getDefaultPositions(): ArrayList<Float>
}