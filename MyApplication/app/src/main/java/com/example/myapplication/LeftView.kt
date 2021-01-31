package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.contract.PagerItem

class LeftView(parent: ViewPager) : PagerItem {

    private val root: View

    init {
        val context = parent.context
        root = LayoutInflater.from(context).inflate(R.layout.layout_left, parent, false)
    }

    override fun getView(): View = root

    override fun initial() {

    }
}