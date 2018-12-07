package com.yalantis.kalendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

const val DRAW_HEIGHT = 30
const val EXTRA_MARGIN = 20

class Kalendar(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet), ViewProvider {

    private var totalWidth: Int = 0

    private var totalHeight: Int = 0

    private var startDragY = 0f

    private var clickOffset = 0f

    private var isDragging = false

    private var dayContainerHeight = 0

    private var weeksMarginTop = 0

    private var isCreated = false

    private val touchListener = MoveManager(this)

    private var selectedWeek = 4

    val dragView = createDragView()

//    private lateinit var weekContainer: LinearLayout

    private fun createWeekContainer(): LinearLayout {
        val weekContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        return weekContainer
    }

    private fun createWeek(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL

            setBackgroundResource(android.R.color.holo_green_dark)

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(0, weeksMarginTop, 0, 0)
            }


            for (i in 0 until 7) {
                this.addView(createDay(i.toString()))
            }
        }
    }

    private fun createDay(label: String = "8"): TextView {
        return TextView(context).apply {
            setBackgroundResource(R.drawable.day_background)
            text = label
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(resources.getColor(android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(totalWidth / 7, dayContainerHeight)
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreated.not()) {
            calculateBounds(left, top, right, bottom)
            orientation = VERTICAL
            setBackgroundResource(android.R.color.holo_purple)
            setOnTouchListener(touchListener)
            addView(createMonthSwitch())
            addView(createWeekDays())
            for (i in 0 until 5) {
                addView(createWeek())
            }
            addView(dragView)

            isCreated = true
        }
    }

    private fun createMainContainer(): LinearLayout {
//        weekContainer = createWeekContainer()


        return LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            setBackgroundResource(android.R.color.darker_gray)
            setOnTouchListener(touchListener)
            addView(createMonthSwitch())
            addView(createWeekDays())
            for (i in 0 until 5) {
                addView(createWeek())
            }
            addView(dragView)
        }
    }

    private fun createDragView(): View {
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, DRAW_HEIGHT)
            setBackgroundColor(Color.GRAY)
        }
    }

    private fun createMonthSwitch(): LinearLayout {
        return LinearLayout(context).apply {
            addView(createLeftMonth("March"))
            addView(createCenterMonth("April"))
            addView(createRightMonth("May"))
        }
    }

    private fun createLeftMonth(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 1f
                gravity = Gravity.START
            }
        }
    }

    private fun createCenterMonth(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 3f
                gravity = Gravity.CENTER
            }
        }
    }

    private fun createRightMonth(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 1f
                gravity = Gravity.END
            }
        }
    }

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        dayContainerHeight = totalHeight / 8
        weeksMarginTop = totalHeight / 40
    }

    private fun createWeekDays(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(createDay("Mon"))
            addView(createDay("Tue"))
            addView(createDay("Wed"))
            addView(createDay("Thu"))
            addView(createDay("Fri"))
            addView(createDay("Sat"))
            addView(createDay("Sun"))
        }
    }


    override fun changeViewBottom(newBottom: Int) {
        bottom = newBottom
    }

    override fun changeDragTop(newDragTop: Float) {
        dragView.y = newDragTop
    }

    override fun getDragViewTop() = dragView.y

    override fun getWeekHeight(position: Int) = getChildAt(position).height

    override fun getWeekBottom(position: Int) = getChildAt(position).y + getChildAt(position).height

    override fun getWeekTop(position: Int) = getChildAt(position).y

    override fun setWeekTop(position: Int, newTop: Float) {
        getChildAt(position).y = newTop
    }

    //    private fun drawFifthRow(touchY: Float) {
//        val newY = touchY - (dayContainerHeight * 2)
//        weekContainer.getChildAt(4).y = newY
//    }
//
//    private fun drawFourthRow(touchY: Float) {
//        val newY = touchY - (dayContainerHeight * 3)
//        weekContainer.getChildAt(3).y = newY
//    }
//
//    private fun drawThirdRow(touchY: Float) {
//        val newY = touchY - (dayContainerHeight * 4)
//        weekContainer.getChildAt(2).y = newY
//    }
//
//    private fun drawSecondRow(touchY: Float) {
//        val newY = touchY - (dayContainerHeight * 5)
//        weekContainer.getChildAt(1).y = newY
//    }
//
//    private fun drawFirstRow(touchY: Float) {
//        val newY = touchY - (dayContainerHeight * 6)
//        weekContainer.getChildAt(0).y = newY
//    }


    /*

    OnTouchListener { _, event ->
        when (event.action) {

            MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                true
            }

            MotionEvent.ACTION_MOVE -> {
                isDragging = true



//                val special = weekContainer.getChildAt(2)

                bottom = event.y.toInt()

                dragView.y = event.y - DRAW_HEIGHT

//                if (bottom <= special.y + special.height) {
//                    special.y = event.y - special.height
//                }

                var week: View
                var weekBottom = 0f
                for (i in 2..6) {
                    week = getChildAt(i)
                    weekBottom = week.y + week.height

                    if (i == selectedWeek && weekBottom >= dragView.y) {
                        week.y = event.y - week.height - DRAW_HEIGHT
                    }

                }


//                drawFirstRow(event.y + clickOffset)
//                drawSecondRow(event.y + clickOffset)
//                drawThirdRow(event.y + clickOffset)
//                drawFourthRow(event.y + clickOffset)
//                drawFifthRow(event.y + clickOffset)

                true
            }

            MotionEvent.ACTION_DOWN -> {
                // touched draw area
                event.y > dragView.y && event.y < dragView.y + DRAW_HEIGHT
            }
            else -> false
        }
    }

     */

}