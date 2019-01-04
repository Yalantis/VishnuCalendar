package com.yalantis.kalendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.yalantis.kalendar.MonthPage
import com.yalantis.kalendar.MonthPagerAdapter

class KalendarConteiner : Fragment() {

    private val scrollListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            val addToEnd = viewPager.adapter?.count == position
            val addToStart = 0 == position

            when {
                addToEnd -> addMonthPageToEnd()
                addToStart -> addMonthPageToStart()
            }
        }
    }

    private fun addMonthPageToStart() {
        (viewPager.adapter as MonthPagerAdapter).addMonthToStart(MonthPage.newInstance(MonthPage.PREVIOUS))
    }

    private fun addMonthPageToEnd() {
        (viewPager.adapter as MonthPagerAdapter).addMonthToEnd(MonthPage.newInstance(MonthPage.NEXT))
    }

    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createView()
    }

    private fun createView(): View? {
        return LinearLayout(requireContext()).apply {
            addView(createViewPager())
        }
    }

    private fun createViewPager(): ViewPager {
        viewPager = ViewPager(requireContext()).apply {
            id = View.generateViewId()
            addOnPageChangeListener(scrollListener)
            adapter = MonthPagerAdapter(childFragmentManager).apply {
                setMonths(
                    listOf(
                        MonthPage.newInstance(MonthPage.PREVIOUS),
                        MonthPage.newInstance(MonthPage.CURRENT),
                        MonthPage.newInstance(MonthPage.NEXT)
                    )
                )
            }
        }
        return viewPager
    }

}