package com.yalantis.kalendar.implementation

import com.yalantis.kalendar.*
import com.yalantis.kalendar.interfaces.DateManager
import com.yalantis.kalendar.interfaces.DateView
import java.util.*
import java.util.Calendar.*

class DateManagerImpl(private val dateView: DateView) : DateManager {

    private val calendar = Calendar.getInstance()

    private var currentMonthLabel = EMPTY_STRING
    private var nextMonthLabel = EMPTY_STRING
    private var previousMonthLabel = EMPTY_STRING

    override fun addDay() {
        calendar.add(DAY_OF_MONTH, 1)
    }

    override fun setDate(date: Date) {
        dateView.clearDate()
        calendar.time = date
        currentMonthLabel = calendar.currentMonthName()
        nextMonthLabel = calendar.nextMonthName()
        previousMonthLabel = calendar.previousMonthName()

        // find count of normal days
        val daysNormal = findNormalDaysCount()

        val daysAfter = calendar.getDaysAfter()

        // scroll back to first day of month
        calendar.set(DAY_OF_MONTH, 1)

        val daysBefore = calendar.getDaysBefore()

        // offset to displayed days start
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)

        dateView.displayDate(daysBefore, daysNormal, daysAfter)

        // restore selected date
        calendar.time = date
        // select day on screen
        dateView.selectDay(date)
    }

    /**
     * Count days only in current month
     */

    private fun findNormalDaysCount(): Int {
        var prevDay = calendar[DAY_OF_MONTH]
        var currDay: Int
        for (i in 0..31) {
            currDay = calendar[DAY_OF_MONTH]
            if (currDay < prevDay) {
                calendar.add(DAY_OF_MONTH, -1)
                return prevDay
            } else {
                prevDay = currDay
                calendar.add(DAY_OF_MONTH, 1)
            }
        }
        return EMPTY_INT
    }

    override fun getCurrentMonthLabel() = currentMonthLabel

    override fun getPreviousMonthLabel() = previousMonthLabel

    override fun getNextMonthLabel() = nextMonthLabel

    override fun setCurrentDate(day: Date) {
        calendar.time = day
    }

    override fun getWeekDayName(which: Int): String {
        calendar.add(DAY_OF_WEEK, which)
        val day = calendar.getDisplayName(DAY_OF_WEEK, SHORT, Locale.getDefault())
        calendar.add(DAY_OF_WEEK, -which)
        return day
    }

    override fun goNextMonth(where: Date?) {
        if (where == null) {
            calendar.add(MONTH, 1)
            setDate(calendar.time)
        } else setDate(where)
    }

    override fun goPreviousMonth(where: Date?) {
        if (where == null) {
            calendar.add(MONTH, -1)
            setDate(calendar.time)
        } else setDate(where)
    }

    override fun getDayLabel() = calendar[DAY_OF_MONTH].toString()

    override fun getCurrentDate(): Date = calendar.time
}