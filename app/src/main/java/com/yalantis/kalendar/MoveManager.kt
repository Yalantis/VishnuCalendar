package com.yalantis.kalendar

import android.view.MotionEvent
import android.view.View

class MoveManager(private val viewProvider: ViewProvider) : View.OnTouchListener {

    private var isDragging = false

    private var selectedWeekBottom = 0f

    // in range 2..6 where 1st week on screen equals to 2nd position here
    private var selectedWeek = 4


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return when (event.action) {

            MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                true
            }

            MotionEvent.ACTION_MOVE -> {

                isDragging = true

                viewProvider.changeViewBottom(event.y.toInt())


                viewProvider.changeDragTop(event.y - DRAW_HEIGHT)


                val dragViewTop = viewProvider.getDragViewTop()
                var weekHeight: Int
                var weekBottom: Float
                for (i in 2..6) {
                    weekHeight = viewProvider.getWeekHeight(i)
                    weekBottom = viewProvider.getWeekTop(i) + weekHeight
                    if (i == selectedWeek) {

                        if (weekBottom >= dragViewTop && dragViewTop <= selectedWeekBottom) {
                            viewProvider.setWeekTop(i, event.y - weekHeight - DRAW_HEIGHT)
                        } else if (weekBottom <= dragViewTop && dragViewTop < selectedWeekBottom) {
                            viewProvider.setWeekTop(i, event.y - weekHeight - DRAW_HEIGHT)
                        }

                        if (weekBottom != selectedWeekBottom && dragViewTop >= selectedWeekBottom) {
                            viewProvider.setWeekTop(i, selectedWeekBottom - weekHeight)
                        }
                    }
                }

                true
            }

            MotionEvent.ACTION_DOWN -> {
                selectedWeekBottom = viewProvider.getWeekBottom(selectedWeek)
                // touched drag area
                event.y > viewProvider.getDragViewTop() && event.y < viewProvider.getDragViewTop() + DRAW_HEIGHT


            }
            else -> false
        }

    }
}