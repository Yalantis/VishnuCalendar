package com.yalantis.vishnu.interfaces

import com.yalantis.vishnu.view.MonthPage
import java.util.*

interface VishnuListener {
    /**
     * Invokes when user clicks on current page day view
     */
    fun onDayClick(date: Date)

    /**
     * Invokes when current page collapsed/expanded state changed
     */

    fun onStateChanged(isCollapsed: Boolean)

    /**
     * Invokes when current page height changed
     */

    fun onHeightChanged(newHeight: Int)

    /**
     * Invokes when clicks on next/previous month
     */

    fun onMonthChanged(forward: Boolean, date: Date? = null)

    /**
     * Invokes when current page size has been measured
     */

    fun onSizeMeasured(monthPage: MonthPage, collapsedHeight: Int, totalHeight: Int)
}
