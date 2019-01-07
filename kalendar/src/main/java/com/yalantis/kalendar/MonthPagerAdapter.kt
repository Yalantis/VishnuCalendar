package com.yalantis.kalendar

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.viewpager.widget.PagerAdapter
import com.yalantis.kalendar.view.MonthPage
import java.util.*

class MonthPagerAdapter(val listener: MonthPage.KalendarListener, val attributeSet: AttributeSet) : PagerAdapter() {

    private val months = mutableListOf<Date>()

    private lateinit var container: ViewGroup

    override fun isViewFromObject(p0: View, p1: Any) = p0 === p1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val kalendar = MonthPage(container.context, attributeSet).apply {
            pageDate = months[position]
            listener = this@MonthPagerAdapter.listener
        }
        container.addView(kalendar, MATCH_PARENT, WRAP_CONTENT)
        this.container = container
        return kalendar
    }

    override fun getCount() = months.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun getPageAt(pos: Int) = container.getChildAt(pos)

    fun getLastDate() = months.last()

    fun getFirstDate() = months.first()

    fun addMonths(month: List<Date>) {
        months.clear()
        months.addAll(month)
        notifyDataSetChanged()
    }

}