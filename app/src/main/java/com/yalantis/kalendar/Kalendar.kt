package com.yalantis.kalendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*
import java.util.Calendar.*

const val DRAG_HEIGHT = 45

class Kalendar(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet), ViewProvider {

    private var totalWidth: Int = 0

    private var totalHeight: Int = 0

    private var dayContainerHeight = 0

    private var weeksMarginTop = 0

    private var previousSelectedDay: View? = null

    private var isCreated = false

    private val dragView by lazy { createDragView() }

    private val moveManager by lazy { MoveManager(this) }

    private val calendar = Calendar.getInstance()

    private var monthCurrent = calendar[MONTH] + 1

    private var monthPrev = monthCurrent - 1

    private var monthNext = monthCurrent + 1



//    private fun createTouchContainer(touchListener: OnTouchListener) {
//        addView(LinearLayout(context).apply {
//            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
//            setOnTouchListener(touchListener)
//        })
//    }

    private fun createWeek(emptyDays: Int, emptyAtStart: Boolean): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(0, weeksMarginTop, 0, 0)
            }
            attachDayToWeek(this, emptyAtStart, emptyDays)
        }
    }

    private fun attachDayToWeek(week: LinearLayout, emptyAtStart: Boolean, emptyDays: Int) {
        if (emptyAtStart) {
            for (i in 0 until emptyDays) {
                week.addView(createDay(true))
                calendar.add(DAY_OF_MONTH, 1)
            }
            for (i in 1..7 - emptyDays) {
                week.addView(createDay(false))
                calendar.add(DAY_OF_MONTH, 1)
            }
        } else {
            for (i in 1..7 - emptyDays) {
                week.addView(createDay(false))
                calendar.add(DAY_OF_MONTH, 1)
            }
            for (i in 0 until emptyDays) {
                week.addView(createDay(true))
                calendar.add(DAY_OF_MONTH, 1)
            }
        }
    }

    fun setDate(from: Date) {
        calendar.time = from
        monthCurrent = calendar[Calendar.MONTH]
        monthPrev = monthCurrent - 1
        monthNext = monthCurrent + 1

        // find count of normal days
        var prevDay = calendar[DAY_OF_MONTH]
        var currDay: Int
        var daysNormal = 0
        for (i in 0..31) {
            currDay = calendar[DAY_OF_MONTH]
            if (currDay < prevDay) {
                daysNormal = prevDay
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                break
            } else {
                prevDay = currDay
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val daysAfter = calendar.getDaysAfter()

        //scroll back to first day of month
        for (i in 1..31) {
            // mb day of week
            if (calendar[DAY_OF_MONTH] != 1) {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            } else break
        }

        val daysBefore = calendar.getDaysBefore()

        calendar.add(DAY_OF_YEAR, -daysBefore)
        createContent(daysBefore, daysNormal, daysAfter)
    }

    private fun createDay(isEmpty: Boolean): TextView {
        return TextView(context).apply {
            if (isEmpty) {
                setTextColor(resources.getColor(android.R.color.darker_gray))
            } else {
                setTextColor(resources.getColor(android.R.color.background_dark))
                setOnClickListener {
                    performDayClick(it)
                }
            }
            isClickable = true
            isFocusable = true
            text = calendar[DAY_OF_MONTH].toString()
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            layoutParams = LinearLayout.LayoutParams(totalWidth / 7, dayContainerHeight)
        }
    }

    private fun performDayClick(day: View) {
        if (moveManager.isBusy.not()) {
            changeColors(day)
            selectWeek(day)
        }
    }

    private fun selectWeek(selectedDay: View?) {
        selectedDay?.let {
            val parent = it.parent as View
            val selectedWeek = indexOfChild(parent)
            moveManager.setSelectedWeek(selectedWeek)
        }
    }

    private fun changeColors(newSelectedDay: View?) {
        newSelectedDay?.setBackgroundResource(R.drawable.day_background)
        previousSelectedDay?.background = null
        previousSelectedDay = newSelectedDay
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreated.not()) {
            calculateBounds(left, top, right, bottom)
            setBackgroundResource(android.R.color.holo_purple)
            setDate(calendar.time)
            isCreated = true
        }
    }

    override fun onTouchEvent(event: MotionEvent) = moveManager.onTouch(event)

    private fun createContent(daysBefore: Int, daysNormal: Int, daysAfter: Int) {
        orientation = VERTICAL
        addView(createMonthSwitch())
        addView(createWeekDays())
        createWeeks(daysBefore, daysNormal, daysAfter)
        addView(dragView)
    }

    private fun createWeeks(daysBefore: Int, daysNormal: Int, daysAfter: Int) {
        val totalDays = daysBefore + daysAfter + daysNormal
        val weeksAmount = totalDays / 7
        for (i in 1..weeksAmount) {
            when (i) {
                1 -> addView(createWeek(daysBefore, true))
                weeksAmount -> addView(createWeek(daysAfter, false))
                else -> addView(createWeek(0, false))
            }
        }
    }

    private fun createDragView(): View {
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, DRAG_HEIGHT).apply {
                setGravity(Gravity.BOTTOM)
            }
            setBackgroundColor(Color.GRAY)
        }
    }

    private fun createMonthSwitch(): LinearLayout {
        return LinearLayout(context).apply {
            isClickable = true
            isFocusable = true
            addView(createMonthDay("March"))
            addView(createMonthDay("April"))
            addView(createMonthDay("May"))
        }
    }


    private fun createMonthDay(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 1f
            }
        }
    }

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        dayContainerHeight = totalHeight / 10
        weeksMarginTop = totalHeight / 40
    }

    private fun createWeekDays(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            addView(createWeekDay("Sun"))
            addView(createWeekDay("Mon"))
            addView(createWeekDay("Tue"))
            addView(createWeekDay("Wed"))
            addView(createWeekDay("Thu"))
            addView(createWeekDay("Fri"))
            addView(createWeekDay("Sat"))
        }
    }

    private fun createWeekDay(label: String) =
        TextView(context).apply {
            text = label
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(resources.getColor(android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(totalWidth / 7, dayContainerHeight)
        }

    override fun setViewBottom(newBottom: Int) {
        bottom = newBottom
    }

    override fun getBottomLimit() = bottom

    override fun getTopLimit() = getChildAt(2).bottom

    override fun setDragTop(newDragTop: Float) {
        dragView.y = newDragTop
    }

    override fun getDragTop() = dragView.y

    override fun getWeekHeight(position: Int) = getChildAt(position).height

    override fun getWeekBottom(position: Int) = getChildAt(position).y + getChildAt(position).height

    override fun getWeekTop(position: Int) = getChildAt(position).y

    override fun setWeekTop(position: Int, newTop: Float) {
        getChildAt(position).y = newTop
    }

}