package com.yalantis.kalendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.yalantis.kalendar.MonthPagerAdapter
import com.yalantis.kalendar.Obertka

class KalendarConteiner : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createViewPager()
    }

    private fun createViewPager(): View? {
        var linearLayout: LinearLayout? = null
        context?.let {
            linearLayout = LinearLayout(it).apply {
                addView(ViewPager(it).apply {
                    id = View.generateViewId()
                    adapter = MonthPagerAdapter(childFragmentManager).apply {
                        setMonths(listOf(
                            Obertka.newInstance(Obertka.PREVIOUS),
                            Obertka.newInstance(Obertka.CURRENT),
                            Obertka.newInstance(Obertka.NEXT)))
                    }
                })
            }
        }
        return linearLayout
    }
}