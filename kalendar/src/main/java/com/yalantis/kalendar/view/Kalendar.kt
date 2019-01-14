package com.yalantis.kalendar.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.yalantis.kalendar.EMPTY_INT
import com.yalantis.kalendar.MonthPagerAdapter
import com.yalantis.kalendar.R
import com.yalantis.kalendar.model.KalendarStylable
import java.util.*

class Kalendar(context: Context, val attributeSet: AttributeSet) : FrameLayout(context, attributeSet),
    MonthPage.KalendarListener {

    private lateinit var viewPager: ViewPager

    private var isFirstMonthInit = false

    private lateinit var stylable: KalendarStylable

    private var pagerColor = EMPTY_INT

    var changeListener: MonthPage.KalendarListener? = null

    private val scrollListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
                1 -> refreshAdapterFront()
                viewPager.adapter?.count?.minus(2) -> refreshAdapterBack()
            }
            makeWrapContent(position)
        }
    }

    init {
        parseStyledAttributes()
        createView()
    }

    private fun makeWrapContent(position: Int) {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val page = adapter.getPageAt(position)
        viewPager.layoutParams = viewPager.layoutParams.apply {
            this.height = page?.getCurrentHeight() ?: WRAP_CONTENT
        }
    }

    private fun refreshAdapterFront() {
        val adapter = viewPager.adapter as MonthPagerAdapter
        adapter.addToStart(monthsToStart())
        viewPager.currentItem = 7
    }

    private fun refreshAdapterBack() {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val currentItem = viewPager.currentItem
        adapter.addToEnd(monthsToEnd())
        viewPager.currentItem = currentItem
    }

    private fun monthsToStart(): List<Date> {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val firstDate = adapter.getFirstDate()
        val calendar = Calendar.getInstance()
        calendar.time = firstDate
        return (0 until 6).map {
            calendar.add(Calendar.MONTH, -1)
            calendar.time
        }.toList()
    }

    private fun monthsToEnd(): List<Date> {
        val adapter = viewPager.adapter as MonthPagerAdapter
        val lastDate = adapter.getLastDate()
        val calendar = Calendar.getInstance()
        calendar.time = lastDate
        return (0 until 6).map {
            calendar.add(Calendar.MONTH, 1)
            calendar.time
        }.toList()
    }

    private fun createViewPager(): View? {
        viewPager = ViewPager(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setBackgroundColor(pagerColor)
            addOnPageChangeListener(scrollListener)
            adapter = MonthPagerAdapter(this@Kalendar).apply {
                stylable = this@Kalendar.stylable
                setMonths(createYear(Calendar.getInstance().time))
            }
        }
        viewPager.offscreenPageLimit = 2
        viewPager.currentItem = 6
        return viewPager
    }


    /**
     * Method allow you to force collapse view
     */

    fun collapse() {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        (currentPage as MonthPage).collapse()
    }

    /**
     * Method allow you to force expand view
     */

    fun expand() {
        val currentPage = (viewPager.adapter as MonthPagerAdapter).getPageAt(viewPager.currentItem)
        (currentPage as MonthPage).expand()
    }

    private fun createYear(from: Date): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.time = from
        calendar.add(Calendar.MONTH, -7)
        return (0 until 12).map {
            calendar.add(Calendar.MONTH, 1)
            calendar.time
        }.toList()
    }


    private fun parseStyledAttributes() {
        stylable = KalendarStylable(context.obtainStyledAttributes(attributeSet, R.styleable.Kalendar))
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = layoutParams.apply { this.height = WRAP_CONTENT }
    }

    private fun createView() {
        addView(createViewPager())
    }

    override fun onSizeMeasured(monthPage: MonthPage, collapsedHeight: Int, totalHeight: Int) {
        if (isFirstMonthInit.not()) {
            isFirstMonthInit = true
            makeWrapContent(6)
        }
        changeListener?.onSizeMeasured(monthPage, collapsedHeight, totalHeight)
    }

    override fun onDayClick(date: Date) {
        changeListener?.onDayClick(date)
    }

    override fun onStateChanged(isCollapsed: Boolean) {
        changeListener?.onStateChanged(isCollapsed)
    }

    override fun onHeightChanged(newHeight: Int) {
        viewPager.layoutParams = viewPager.layoutParams.apply { height = newHeight }
        changeListener?.onHeightChanged(newHeight)
    }

    override fun onMonthChanged(forward: Boolean) {
        if (forward) {
            viewPager.arrowScroll(ViewPager.FOCUS_RIGHT)
        } else {
            viewPager.arrowScroll(ViewPager.FOCUS_LEFT)
        }
        changeListener?.onMonthChanged(forward)
    }

}