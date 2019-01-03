package com.yalantis.kalendar.view

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.yalantis.kalendar.*
import com.yalantis.kalendar.implementation.DateManagerImpl
import com.yalantis.kalendar.implementation.MoveManagerImpl
import com.yalantis.kalendar.interfaces.DateManager
import com.yalantis.kalendar.interfaces.DateView
import com.yalantis.kalendar.interfaces.MoveManager
import com.yalantis.kalendar.interfaces.ViewProvider
import com.yalantis.kalendar.model.*
import java.util.*

class Kalendar(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet),
    ViewProvider, Day.OnDayClickListener, DateView {

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

    var listener: KalendarListener? = null

    @DrawableRes
    var selectedDayDrawable: Int = R.drawable.day_background
        set(value) {
            field = value
            invalidate()
        }


    @ColorRes
    var dragColor = R.color.drag_color
        set(value) {
            field = value
            invalidate()
        }

    @ColorRes
    var dragTextColor = R.color.drag_text_color
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

    var kalendarBackground: Int = android.R.color.white
        set(value) {
            field = value
            setBackgroundResource(value)
            invalidate()
        }

    init {
        layoutTransition = LayoutTransition()
        obtainStylable(attributeSet)
    }

    private fun obtainStylable(attributeSet: AttributeSet?) {
        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.Kalendar)
        dragHeight = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragHeight, DEFAULT_DRAG_HEIGHT)
        dragColor = attrs.getColor(R.styleable.Kalendar_dragColor, ContextCompat.getColor(context, R.color.drag_color))
        dragText = attrs.getString(R.styleable.Kalendar_dragText) ?: EMPTY_STRING
        dragTextColor = attrs.getColor(
            R.styleable.Kalendar_dragTextColor,
            ContextCompat.getColor(context, R.color.drag_text_color)
        )
        dragTextSize = attrs.getDimensionPixelSize(R.styleable.Kalendar_dragTextSize, R.dimen.drag_text_size)
        selectedDayDrawable = attrs.getResourceId(R.styleable.Kalendar_selectedDayDrawable, R.drawable.day_background)
        attrs.recycle()
    }

    /**
     * Method allow you to force collapse view
     */

    fun collapse() {
        moveManager.collapse()
    }

    /**
     * Method allow you to force expand view
     */

    fun expand() {
        moveManager.expand()
    }


    /**
     * Method allow you to set date and will display it
     */

    fun setDate(date: Date) {
        dateManager.setDate(date)
    }

    /**
     * Method allow you to change current month by one forward or backward
     */

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

    /**
     * Method provide current selected date
     */

    fun getCurrentDate() = dateManager.getCurrentDate()

    /**
     * Method creates week with days inside
     */

    private fun createWeek(emptyDays: Int, emptyAtStart: Boolean): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            background = ContextCompat.getDrawable(context, R.drawable.unselected_week)
            attachDayToWeek(this, emptyAtStart, emptyDays)
        }
    }

    /**
     * Method creates day and add it to the current creating week
     */

    private fun attachDayToWeek(week: LinearLayout, emptyAtStart: Boolean, emptyDays: Int) {
        if (emptyAtStart) {
            for (i in 0 until emptyDays) {
                week.addView(createDay(true))
                dateManager.addDay()
            }
            for (i in 1..DAYS_IN_WEEK - emptyDays) {
                week.addView(createDay(false))
                dateManager.addDay()
            }
        } else {
            for (i in 1..DAYS_IN_WEEK - emptyDays) {
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
                    post { applyDaySelection(day) }
                    return
                }
            }
        }
    }

    /**
     * Method checks clicked day and make this day selected or post selection action in queue
     */

    private fun applyDaySelection(day: Day) {
        if (previousSelectedDay != day) {
            if (moveManager.isInAction.not() && moveManager.isCollapsed.not()) {
                changeDayColors(day)
                selectWeek(day)
            } else {
                actionQueue.add(KAction(ACTION_SELECT_DAY))
                moveManager.expand()
            }
            dateManager.setCurrentDate(day.date)
        }
    }

    /**
     * Method creates day view which will be later displayed
     */

    private fun createDay(isEmpty: Boolean): TextView {
        return Day(context).apply {
            typeface = dayTypeface
            labelColor = if (isEmpty) {
                androidx.core.content.ContextCompat.getColor(context, android.R.color.darker_gray)
            } else {
                ContextCompat.getColor(context, android.R.color.background_dark)
            }
            canClick = isEmpty.not()
            clickListener = this@Kalendar
            label = dateManager.getDayLabel()
            date = dateManager.getCurrentDate()
            size(daySize, daySize)
        }
    }

    override fun onNormalDayClick(day: Day) {
        applyDaySelection(day)
        listener?.onDayClick(day.date)
    }

    override fun onDisabledDayClick(day: Day) {
        selectDayAndSwitchMonth(day.date)
        listener?.onDayClick(day.date)
    }

    /**
     * Method switch to the next or previous month and set date from the day as current
     */

    private fun selectDayAndSwitchMonth(date: Date) {
        if (moveManager.isInAction.not() && moveManager.isCollapsed.not()) {
            if (date.time > dateManager.getCurrentDate().time) {
                dateManager.goNextMonth(date)
            } else {
                dateManager.goPreviousMonth(date)
            }
        } else {
            dateManager.setCurrentDate(date)
            actionQueue.add(KAction(ACTION_SELECT_DISABLED_DAY))
            moveManager.expand()
        }
    }

    /**
     * Method selects week which will not be collapsed
     */

    private fun selectWeek(selectedDay: View?) {
        selectedDay?.let {
            val parent = it.parent as View
            parent.setBackgroundResource(R.drawable.selected_week_back)
            val selectedWeek = indexOfChild(parent) - WEEK_OFFSET
            moveManager.selectWeek(selectedWeek)
        }
    }

    /**
     * Method change unselected day color to selected day color
     */

    private fun changeDayColors(newSelectedDay: View?) {
        newSelectedDay?.setBackgroundResource(selectedDayDrawable)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = layoutParams.apply { height = WRAP_CONTENT }
    }

    override fun onTouchEvent(event: MotionEvent) = moveManager.onTouch(event)

    override fun clearDate() {
        removeAllViews()
    }

    /**
     * Method creates whole view hierarchy
     */

    private fun createContent(daysBefore: Int, daysNormal: Int, daysAfter: Int) {
        orientation = VERTICAL
        createMonthSwitch()
        createWeekDays()
        createWeeks(daysBefore, daysNormal, daysAfter)
        createDragView()
        makeWrapContent()
    }

    /**
     * Method makes root view height as wrap content
     */

    private fun makeWrapContent() {
        post {
            var totHeight = 0
            for (i in 0 until childCount) {
                totHeight += getChildAt(i).height
            }
            this.layoutParams = this.layoutParams.apply { height = WRAP_CONTENT }
            moveManager.setCurrentMaxHeight(totHeight)
        }
    }

    /**
     * Method calculates total weeks amount and creates weeks depends on calculated count
     */

    private fun createWeeks(daysBefore: Int, daysNormal: Int, daysAfter: Int) {
        val totalDays = daysBefore + daysAfter + daysNormal
        val weeksAmount = totalDays / DAYS_IN_WEEK
        for (i in 1..weeksAmount) {
            when (i) {
                1 -> addView(createWeek(daysBefore, true))
                weeksAmount -> addView(createWeek(daysAfter, false))
                else -> addView(createWeek(0, false))
            }
        }
    }


    /**
     * Method creates view at the bottom of root view
     */

    private fun createDragView() {
        addView(TextView(context).apply {
            text = dragText
            textSize = dragTextSize.toFloat()
            setTextColor(dragTextColor)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setBackgroundColor(dragColor)
            this.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, dragHeight)
        })
        getChildAt(childCount - 1).layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, dragHeight).apply {
            gravity = Gravity.BOTTOM
        }
    }

    /**
     * Method creates days which able to switch month and implements month switch logic
     */

    private fun createMonthSwitch() {
        addView(LinearLayout(context).apply {
            isClickable = true
            isFocusable = true

            setPadding(
                resources.getDimension(R.dimen.small_padding).toInt(),
                resources.getDimension(R.dimen.medium_padding).toInt(),
                resources.getDimension(R.dimen.small_padding).toInt(),
                resources.getDimension(R.dimen.medium_padding).toInt()
            )

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
            textSize = DEFAULT_DAY_TEXT_SIZE
            click = clickListener
        }
    }

    /**
     * Method calculates total root size
     */

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        daySize = totalWidth / DAYS_IN_WEEK
    }

    /**
     * Method creates week day names container
     */

    private fun createWeekDays() {
        addView(LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            for (i in DAYS_IN_WEEK_RANGE) {
                addView(createWeekDay(dateManager.getWeekDayName(i)))
            }
        })
    }

    /**
     * Method creates week day name
     */

    private fun createWeekDay(label: String) =
        TextView(context).apply {
            text = label
            typeface = weekDayTypeface
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(ContextCompat.getColor(context, android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(daySize, daySize)
        }

    override fun setViewHeight(newBottom: Int) {
        layoutParams = layoutParams.apply {
            height = newBottom
        }
    }

    override fun displayDate(emptyBefore: Int, normal: Int, emptyAfter: Int) {
        createContent(emptyBefore, normal, emptyAfter)
    }


    override fun moveStateChanged(collapsed: Boolean, selectedWeek: Int) {

        invisibleDaysClick(collapsed.not(), selectedWeek)

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
                    ACTION_SELECT_DISABLED_DAY -> {
                        selectDayAndSwitchMonth(dateManager.getCurrentDate())
                    }
                }
                actionQueue.remove(it)
            }
        }
        listener?.onStateChanged(collapsed)
    }

    /**
     * Method make collapsed weeks inactive
     */

    private fun invisibleDaysClick(enabled: Boolean, selectedWeek: Int) {

        var weekView: ViewGroup
        val displayingWeek = selectedWeek + WEEK_OFFSET

        for (week in WEEK_OFFSET until childCount - 1) {
            if (week != displayingWeek) {
                weekView = getChildAt(week) as ViewGroup
                weekView.clicks(enabled)
                for (day in DAYS_IN_WEEK_RANGE) {
                    weekView.getChildAt(day).clicks(enabled)
                }
            }
        }
    }

    override fun applyAlpha(week: Int, alpha: Float) {
        getChildAt(week + WEEK_OFFSET).alpha = alpha
    }

    override fun getViewTotalHeight() = layoutParams.height

    override fun getViewTop() = top

    override fun getDragHeight() = dragHeight

    override fun getBottomLimit() = y.toInt() + height

    override fun getTopLimit() = getChildAt(WEEK_OFFSET).bottom

    override fun viewMinHeight() =
        getChildAt(0).height + getChildAt(1).height + getChildAt(2).height


    override fun getWeekCount() = childCount - WEEK_OFFSET - 1 // -1 cuz dragView

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