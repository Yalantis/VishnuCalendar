package com.yalantis.kalendar

import android.view.MotionEvent

class MoveManager(private val viewProvider: ViewProvider) {


    private var firstWeekBottom = 0f

    // in range 2..6 where 1st week on screen equals to 2nd position here
    private var selectedWeek = 4

    private var isNeedToCollapse = false

    private val selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)


    fun onTouch(event: MotionEvent): Boolean {
        return when (event.action) {

            MotionEvent.ACTION_CANCEL -> {
                false
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.y >= firstWeekBottom) {
                    viewProvider.changeViewBottom(event.y.toInt())
                    viewProvider.changeDragTop(event.y - DRAG_HEIGHT)

                    val dragViewTop = viewProvider.getDragViewTop()
                    var weekHeight: Int
                    var weekBottom: Float
                    for (i in 2..6) {
                        weekHeight = viewProvider.getWeekHeight(i)
                        weekBottom = viewProvider.getWeekTop(i) + weekHeight
                        if (i == selectedWeek) {

                            if (weekBottom >= dragViewTop && dragViewTop <= selectedWeekBottom) {
                                viewProvider.setWeekTop(i, event.y - weekHeight - DRAG_HEIGHT)
                            } else if (weekBottom <= dragViewTop && dragViewTop < selectedWeekBottom) {
                                viewProvider.setWeekTop(i, event.y - weekHeight - DRAG_HEIGHT)
                            }

                            if (weekBottom != selectedWeekBottom && dragViewTop >= selectedWeekBottom) {
                                viewProvider.setWeekTop(i, selectedWeekBottom - weekHeight)
                            }
                        }
                    }
                } else {
                    viewProvider.changeViewBottom(firstWeekBottom.toInt())
                    viewProvider.changeDragTop(firstWeekBottom - DRAG_HEIGHT)
                }
                true
            }

            MotionEvent.ACTION_DOWN -> {
                firstWeekBottom = viewProvider.getWeekBottom(2)
                // touched drag area
                val a = event.y
                val b = viewProvider.getDragViewTop()
                a > b && a < b + DRAG_HEIGHT


            }
            else -> false
        }

    }

    fun setSelectedWeek(selectedWeek: Int) {
        this.selectedWeek = selectedWeek
    }
}