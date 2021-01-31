package com.example.myapplication

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.contract.PagerItem
import com.example.myapplication.util.AlphaBlurImage


class ViewInitializer(private val activity: Activity) {

    fun init() {
        val context: Context = activity

        val wallpaperManager = WallpaperManager.getInstance(context)

        val blurImage = activity.findViewById<AlphaBlurImage>(R.id.wallpaper)

        val readPermission = "android.permission.READ_EXTERNAL_STORAGE"
        val permission = ActivityCompat.checkSelfPermission(activity, readPermission)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            blurImage.setBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.wallpaper))
        } else {
            //ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            blurImage.setBitmap(wallpaperManager.drawable.toBitmap())
        }

        val viewPager = activity.findViewById<ViewPager>(R.id.viewpager)
        val leftView = LeftView(viewPager)
        leftView.initial()
        val mainView = MainView(viewPager)
        mainView.initial()
        val appView = AppView(viewPager) { blurImage.setRadius(it) }
        appView.initial()

        val data: List<PagerItem> = arrayListOf(leftView, mainView, appView)
        viewPager.adapter = MyPagerAdapter(data.map { it.getView() })
        viewPager.currentItem = 1
    }

    private class MyPagerAdapter(private val data: List<View>) : PagerAdapter() {

        override fun isViewFromObject(view: View, it: Any): Boolean = it == view

        override fun getCount(): Int = data.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = data[position]
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(data[position])
        }
    }
}