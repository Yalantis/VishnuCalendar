package com.yalantis.kalendar

import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH

class DateManagerImpl(private val dateView: DateView) : DateManager {

    private val calendar = Calendar.getInstance()

    private var currentMonthLabel = ""
    private var nextMonthLabel = ""
    private var previousMonthLabel = ""

    override fun addDay() {
        calendar.add(DAY_OF_MONTH, 1)
    }

    override fun setDate(date: Date) {
        calendar.time = date

        currentMonthLabel = calendar.currentMonthName()
        nextMonthLabel = calendar.nextMonthName()
        previousMonthLabel = calendar.previousMonthName()

        // find count of normal days
        var prevDay = calendar[DAY_OF_MONTH]
        var currDay: Int
        var daysNormal = 0
        for (i in 0..31) {
            currDay = calendar[DAY_OF_MONTH]
            if (currDay < prevDay) {
                daysNormal = prevDay
                calendar.add(DAY_OF_MONTH, -1)
                break
            } else {
                prevDay = currDay
                calendar.add(DAY_OF_MONTH, 1)
            }
        }

        val daysAfter = calendar.getDaysAfter()

        //scroll back to first day of month
        calendar.set(DAY_OF_MONTH, 1)

        val daysBefore = calendar.getDaysBefore()

        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)

        dateView.displayDate(daysBefore, daysNormal, daysAfter)

        dateView.selectDay(date)
    }

    override fun getCurrentMonthLabel() = currentMonthLabel

    override fun getPreviousMonthLabel() = previousMonthLabel

    override fun getNextMonthLabel() = nextMonthLabel

    override fun goNextMonth() {
        dateView.clearDate()
        calendar.add(MONTH, 1)
        setDate(calendar.time)
    }

    override fun goPreviousMonth() {
        dateView.clearDate()
        calendar.add(MONTH, -1)
        setDate(calendar.time)
    }

    override fun getDayLabel() = calendar[DAY_OF_MONTH].toString()

    override fun getCurrentDate(): Date = calendar.time
}