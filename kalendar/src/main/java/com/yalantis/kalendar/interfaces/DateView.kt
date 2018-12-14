package com.yalantis.kalendar.interfaces

import java.util.*

interface DateView {

    fun displayDate(emptyBefore: Int, normal: Int, emptyAfter: Int)

    fun selectDay(date: Date)

    fun clearDate()

}