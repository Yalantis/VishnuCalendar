package com.yalantis.kalendar

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MonthPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val months = mutableListOf<Obertka>()

    override fun getItem(position: Int) = months[position]

    override fun getCount() = months.size

    fun setMonths(month: List<Obertka>) {
        months.addAll(month)
    }

}