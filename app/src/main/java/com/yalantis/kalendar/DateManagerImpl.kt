package com.yalantis.kalendar

import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH

class DateManagerImpl(private val dateView: DateView) : DateManager {

    private val calendar = Calendar.getInstance()

    override fun addDay() {
        calendar.add(DAY_OF_MONTH, 1)
    }

    override fun setDate(date: Date) {
        dateView.clearDate()

        calendar.time = date

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

//        for (i in 1..31) {
//            if (calendar[DAY_OF_MONTH] != 1) {
//                calendar.add(DAY_OF_MONTH, -1)
//            } else break
//        }

        val daysBefore = calendar.getDaysBefore()

        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)

        dateView.displayDate(daysBefore, daysNormal, daysAfter)

        dateView.selectDay(date)
    }

    override fun getCurrentMonthLabel() = calendar.currentMonthName()

    override fun getPreviousMonthLabel(): String {
        calendar.add(MONTH, -1)
        val name = calendar.currentMonthName()
        calendar.add(MONTH, 1)
        return name
    }

    override fun getNextMonthLabel(): String {
        calendar.add(MONTH, 1)
        val name = calendar.currentMonthName()
        calendar.add(MONTH, -1)
        return name
    }


    override fun goNextMonth() {
        calendar.add(MONTH, 1)
        setDate(calendar.time)
    }

    override fun goPreviousMonth() {
        calendar.add(MONTH, -1)
        setDate(calendar.time)
    }


    override fun getDayLabel() = calendar[DAY_OF_MONTH].toString()

    override fun getCurrentDate(): Date = calendar.time
}