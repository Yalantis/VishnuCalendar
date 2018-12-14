package com.yalantis.kalendar

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
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

class Kalendar(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet),
    ViewProvider, Day.OnDayClickListener, DateView {

    var listener: KalendarListener? = null

    @DrawableRes
    var selectedDayColor: Int = R.drawable.day_background
        set(value) {
            field = value
            invalidate()
        }

    @ColorRes
    var dragColor = android.R.color.holo_blue_dark
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var dragTextColor = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    var dayTypeface: Typeface = Typeface.MONOSPACE
        set(value) {
            field = value
            invalidate()
        }

    var weekDayTypeface: Typeface = Typeface.MONOSPACE

    var monthTypeface: Typeface = Typeface.DEFAULT_BOLD
        set(value) {
            field = value
            invalidate()
        }

    private var dragTextSize = EMPTY_INT

    private var totalWidth = EMPTY_INT

    private var totalHeight = EMPTY_INT

    private var daySize = EMPTY_INT

    private var previousSelectedDay: View? = null

    private var dragHeight = EMPTY_INT

    private var dragText = EMPTY_STRING

    private var isCreated = false

    private val moveManager: MoveManager by lazy { MoveManagerImpl(this) }

    private val dateManager: DateManager by lazy { DateManagerImpl(this) }

    private val actionQueue = ArrayList<KAction>()

    init {
        layoutTransition = LayoutTransition()
        obtainStylable(attributeSet)
    }


    private fun obtainStylable(attributeSet: AttributeSet) {
        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.Kalendar)
        if (attrs.hasValue(R.styleable.Kalendar_dragHeight)) {
            dragHeight = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragHeight, 0)
            dragText = attrs.getString(R.styleable.Kalendar_dragText) ?: EMPTY_STRING
            dragTextSize = attrs.getInt(R.styleable.Kalendar_dragTextSize, 15)
        }
        attrs.recycle()
    }

    fun setDate(date: Date) {
        dateManager.setDate(date)
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

    fun getCurrentDate() = dateManager.getCurrentDate()

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

    override fun selectDay(date: Date) {
        var week: ViewGroup
        var day: Day
        for (i in WEEK_OFFSET until childCount) {
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
            typeface = dayTypeface
            labelColor = if (isEmpty) {
                resources.getColor(android.R.color.darker_gray)
            } else {
                resources.getColor(android.R.color.background_dark)
            }
            canClick = isEmpty.not()
            clickListener = this@Kalendar
            label = dateManager.getDayLabel()
            date = dateManager.getCurrentDate()
            size(daySize, daySize)
        }
    }

    override fun onDayClick(day: Day) {
        if (previousSelectedDay != day) {
            if (moveManager.isBusy.not() && moveManager.isCollapsed.not()) {
                changeColors(day)
                selectWeek(day)
            } else {
                actionQueue.add(KAction(ACTION_SELECT_DAY))
                moveManager.expand()
            }
            dateManager.setCurrentDate(day.date)
        }
        listener?.onDayClick(day.date)
    }

    private fun selectWeek(selectedDay: View?) {
        selectedDay?.let {
            val parent = it.parent as View
            val selectedWeek = indexOfChild(parent) - WEEK_OFFSET
            moveManager.selectWeek(selectedWeek)
        }
    }

    private fun changeColors(newSelectedDay: View?) {
        newSelectedDay?.setBackgroundResource(selectedDayColor)
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
        createMonthSwitch()
        createWeekDays()
        createWeeks(daysBefore, daysNormal, daysAfter)
        createDragView()
        makeWrapContent()
    }

    private fun makeWrapContent() {
        post {
            var totHeight = 0
            for (i in 0 until childCount) {
                totHeight += getChildAt(i).height
            }
            layoutParams = layoutParams.apply { height = WRAP_CONTENT }
            moveManager.setCurrentMaxHeight(totHeight)
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

    private fun createDragView() {
        addView(TextView(context).apply {
            text = dragText
            textSize = dragTextSize.toFloat()
            setTextColor(dragTextColor)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setBackgroundResource(dragColor)
        })
        getChildAt(childCount - 1).layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, dragHeight).apply {
            gravity = Gravity.BOTTOM
        }
    }

    private fun createMonthSwitch() {
        addView(LinearLayout(context).apply {
            isClickable = true
            isFocusable = true

            setPadding(dp(8), dp(16), dp(8), dp(16))

            addView(createMonthDay(Month.TYPE_LEFT, dateManager.getPreviousMonthLabel()) {
                scrollMonth(false)
            })
            addView(createMonthDay(Month.TYPE_MID, dateManager.getCurrentMonthLabel()))

            addView(createMonthDay(Month.TYPE_RIGHT, dateManager.getNextMonthLabel()) {
                scrollMonth(true)
            })
        })
    }

    private fun createMonthDay(type: Int, label: String, clickListener: (() -> Unit)? = null): View? {
        return Month(context).apply {
            this.type = type
            this.label = label
            typeface = monthTypeface
            textSize = 18f
            click = clickListener
        }
    }

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        daySize = totalWidth / 7
    }

    private fun createWeekDays() {
        addView(LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            addView(createWeekDay(dateManager.getWeekDayName(0)))
            addView(createWeekDay(dateManager.getWeekDayName(1)))
            addView(createWeekDay(dateManager.getWeekDayName(2)))
            addView(createWeekDay(dateManager.getWeekDayName(3)))
            addView(createWeekDay(dateManager.getWeekDayName(4)))
            addView(createWeekDay(dateManager.getWeekDayName(5)))
            addView(createWeekDay(dateManager.getWeekDayName(6)))
        })
    }

    private fun createWeekDay(label: String) =
        TextView(context).apply {
            text = label
            typeface = weekDayTypeface
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(resources.getColor(android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(daySize, daySize)
        }

    override fun setViewHeight(newBottom: Int) {
        layoutParams = layoutParams.apply {
            height = newBottom
        }
        setDragTop(newBottom - dragHeight.toFloat())
        invalidate()
    }

    override fun displayDate(emptyBefore: Int, normal: Int, emptyAfter: Int) {
        createContent(emptyBefore, normal, emptyAfter)
    }


    override fun moveStateChanged(collapsed: Boolean) {

        invisibleDaysClick(collapsed.not())

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
        listener?.onStateChanged(collapsed)
    }

    private fun invisibleDaysClick(enabled: Boolean) {
        val week = getChildAt(BLOCKING_TOUCH_WEEK) as ViewGroup
        week.clicks(enabled)
        for (i in DAYS_IN_WEEK) {
            (week.getChildAt(i) as Day).clicks(enabled)
        }
    }

    override fun viewHeight() = layoutParams.height

    override fun getViewTop() = top

    override fun getDragHeight() = dragHeight

    override fun getBottomLimit() = y.toInt() + height

    override fun getTopLimit() = getChildAt(WEEK_OFFSET).bottom

    override fun viewMinHeight() =
        getChildAt(0).height + getChildAt(1).height + getChildAt(2).height


    override fun getWeekCount() = childCount - WEEK_OFFSET

    override fun setDragTop(newDragTop: Float) {
        getChildAt(childCount - 1).y = newDragTop
    }

    override fun setWeekHeight(i: Int, weekHeight: Int) {
        val week = getChildAt(i)
        week.layoutParams = week.layoutParams.apply { height = weekHeight }
    }

    override fun getDragTop() = getChildAt(childCount - 1).y

    override fun getWeekHeight() = getChildAt(WEEK_OFFSET).height

    override fun getWeekBottom(position: Int): Float {
        val week = getChildAt(position + WEEK_OFFSET)
        return week.y + week.height
    }

    override fun getWeekTop(position: Int) = getChildAt(position).y

    override fun moveWeek(position: Int, newTop: Float) {
        val week = getChildAt(position + WEEK_OFFSET)
        week.y = newTop - week.height
        week.layoutParams = week.layoutParams.apply { height = week.height }
    }

    interface KalendarListener {
        fun onDayClick(date: Date)
        fun onStateChanged(isCollapsed: Boolean)
    }

}