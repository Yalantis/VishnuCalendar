package com.yalantis.kalendar

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

const val DRAG_HEIGHT = 45

class Kalendar(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet),
    ViewProvider, Day.OnDayClickListener, DateView {

    private var totalWidth: Int = 0

    private var totalHeight: Int = 0

    private var dayContainerHeight = 0

    private var previousSelectedDay: View? = null

    private var isCreated = false

    private val dragView by lazy { createDragView() }

    private val moveManager by lazy { MoveManager(this) }

    private val dateManager: DateManager by lazy { DateManagerImpl(this) }

    private val actionQueue = ArrayList<KAction>()

    private fun createWeek(emptyDays: Int, emptyAtStart: Boolean): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            background = ContextCompat.getDrawable(context, R.drawable.week_back)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setPadding(0, 0, 0, dp(5))
            }
            attachDayToWeek(this, emptyAtStart, emptyDays)
        }
    }

    private fun attachDayToWeek(week: LinearLayout, emptyAtStart: Boolean, emptyDays: Int) {
        if (emptyAtStart) {
            for (i in 0 until emptyDays) {
                week.addView(createDay(true))
                dateManager.addDay()
            }
            for (i in 1..7 - emptyDays) {
                week.addView(createDay(false))
                dateManager.addDay()
            }
        } else {
            for (i in 1..7 - emptyDays) {
                week.addView(createDay(false))
                dateManager.addDay()
            }
            for (i in 0 until emptyDays) {
                week.addView(createDay(true))
                dateManager.addDay()
            }
        }
    }

    fun setDate(date: Date) {
        dateManager.setDate(date)
    }


    override fun selectDay(date: Date) {
        var week: ViewGroup
        var day: Day
        for (i in 2 until childCount) {
            week = getChildAt(i) as ViewGroup
            for (j in 0 until week.childCount) {
                day = week.getChildAt(j) as Day
                if (day.date == date) {
                    post { onDayClick(day) }
                    return
                }
            }
        }
    }

    private fun createDay(isEmpty: Boolean): TextView {
        return Day(context).apply {
            labelColor = if (isEmpty) {
                resources.getColor(android.R.color.darker_gray)
            } else {
                resources.getColor(android.R.color.background_dark)
            }
            canClick = isEmpty.not()
            clickListener = this@Kalendar
            label = dateManager.getDayLabel()
            date = dateManager.getCurrentDate()
            size(totalWidth / 7, dayContainerHeight)
        }
    }

    override fun onDayClick(day: Day) {
        if (moveManager.isBusy.not() && moveManager.isCollapsed.not()) {
            changeColors(day)
            selectWeek(day)
        } else {
            actionQueue.add(KAction(ACTION_SELECT_DAY))
            moveManager.expand()
        }
        dateManager.setCurrentDate(day.date)
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
            setBackgroundResource(android.R.color.white)
            setDate(dateManager.getCurrentDate())
            isCreated = true
        }
    }

    override fun onTouchEvent(event: MotionEvent) = moveManager.onTouch(event)

    override fun clearDate() {
        removeAllViews()
    }

    private fun createContent(daysBefore: Int, daysNormal: Int, daysAfter: Int) {
        orientation = VERTICAL
        addView(createMonthSwitch())
        addView(createWeekDays())
        createWeeks(daysBefore, daysNormal, daysAfter)
        addView(dragView)
        dragView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, DRAG_HEIGHT).apply {
            gravity = Gravity.BOTTOM
        }
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

            setPadding(dp(8), dp(8), dp(8), dp(8))

            addView(createMonthDay(Month.TYPE_LEFT, dateManager.getPreviousMonthLabel()) {
                scrollMonth(false)
            })
            addView(createMonthDay(Month.TYPE_MID, dateManager.getCurrentMonthLabel()))

            addView(createMonthDay(Month.TYPE_RIGHT, dateManager.getNextMonthLabel()) {
                scrollMonth(true)
            })
        }
    }


    fun scrollMonth(forward: Boolean = true) {
        if (moveManager.isCollapsed) {
            if (forward) {
                actionQueue.add(KAction((ACTION_NEXT_MONTH)))
            } else {
                actionQueue.add(KAction((ACTION_PREV_MONTH)))
            }
            moveManager.expand()
        } else {
            applyTransition {
                if (forward) {
                    dateManager.goNextMonth()
                } else {
                    dateManager.goPreviousMonth()
                }
            }
        }
    }

//
//    private fun createMonthActionButton(isNext: Boolean): View? {
//        return if (isNext) {
//            createMonthDay(Month.TYPE_RIGHT, dateManager.getNextMonthLabel()) {
//                dateManager.goNextMonth()
//            }
//        } else {
//            createMonthDay(Month.TYPE_LEFT, dateManager.getPreviousMonthLabel()) {
//                dateManager.goPreviousMonth()
//            }
//        }
//    }

    private fun createMonthDay(type: Int, label: String, clickListener: (() -> Unit)? = null): View? {
        return Month(context).apply {
            this.type = type
            this.label = label
            textSize = 18f
            click = clickListener
        }
    }

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        dayContainerHeight = totalHeight / 10
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

    override fun displayDate(emptyBefore: Int, normal: Int, emptyAfter: Int) {
        createContent(emptyBefore, normal, emptyAfter)
    }


    override fun moveStateChanged(collapsed: Boolean) {
        actionQueue.firstOrNull()?.let {
            applyTransition {
                when (it.type) {
                    ACTION_NEXT_MONTH -> {
                        dateManager.goNextMonth()
                    }
                    ACTION_PREV_MONTH -> {
                        dateManager.goPreviousMonth()
                    }
                    ACTION_SELECT_DAY -> {
                        selectDay(dateManager.getCurrentDate())
                    }
                }
                actionQueue.remove(it)
            }
        }
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