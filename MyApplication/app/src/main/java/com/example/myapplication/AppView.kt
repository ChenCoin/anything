package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.contract.PagerItem

class AppView(private val parent: ViewPager, private val offsetChanged: (Float) -> Unit) : PagerItem {
    private val tag: String = "AppView"

    private val root: View

    init {
        val context = parent.context
        root = LayoutInflater.from(context).inflate(R.layout.layout_app, parent, false)
    }

    override fun getView(): View = root

    override fun initial() {
        parent.addOnPageChangeListener(createPageListener())
    }

    private fun createPageListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 1) offsetChanged(positionOffset)
                if (position == 2) offsetChanged(1 - positionOffset)
            }

            override fun onPageSelected(position: Int) {

            }
        }
    }
}