package com.yalantis.kalendar

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MonthPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val months = mutableListOf<MonthPage>()

    override fun getItem(position: Int) = months[position]

    override fun getCount() = months.size

    fun setMonths(month: List<MonthPage>) {
        months.addAll(month)
    }


    fun addMonthToEnd(month: MonthPage) {
        months.removeAt(0)
        months.add(month)
        notifyDataSetChanged()
    }

    fun addMonthToStart(month: MonthPage) {
        months.removeAt(months.size - 1)
        months.add(0, month)
        notifyDataSetChanged()
    }

}