package com.yalantis.kalendar

import android.view.MotionEvent

interface MoveManager {

    var isBusy: Boolean

    var isCollapsed: Boolean

    fun expand()

    fun collapse()

    fun selectWeek(selectedWeek: Int)

    fun onTouch(event: MotionEvent): Boolean

    fun setCurrentMaxHeight(totHeight: Int)

}