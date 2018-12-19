package com.yalantis.kalendar.interfaces

import android.view.MotionEvent

interface MoveManager {

    var isInAction: Boolean

    var isCollapsed: Boolean

    /**
     * Perform expand action
     */

    fun expand()

    /**
     * Perform collapse action
     */

    fun collapse()

    /**
     * Change current selected week
     */

    fun selectWeek(selectedWeek: Int)

    /**
     * Handling touch events
     */

    fun onTouch(event: MotionEvent): Boolean

    /**
     * Change current max height
     */

    fun setCurrentMaxHeight(totHeight: Int)

}