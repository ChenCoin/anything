package xyz.cyan

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import xyz.cyan.bezier.BezierView
import xyz.cyan.bezier.MenuBezierView
import xyz.cyan.bezier.RightBezierView
import xyz.cyan.bezier.RightMenuBezierView
import xyz.cyan.touchbar.TouchBarManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TouchBarManager(findViewById(R.id.touchBar)).init()

        val launcherMode = findViewById<TextView>(R.id.launcherMode)
        val appMode = findViewById<TextView>(R.id.appMode)

        val leftLauncherView = findViewById<View>(R.id.leftLauncherMode)
        val leftAppView = findViewById<View>(R.id.leftAppMode)
        val rightLauncherMode = findViewById<View>(R.id.rightLauncherMode)
        val rightAppView = findViewById<View>(R.id.rightAppMode)

        launcherMode.setOnClickListener {
            launcherMode.setBackgroundResource(R.drawable.btn_bgr_select)
            launcherMode.setTextColor(Color.WHITE)
            appMode.setBackgroundResource(R.drawable.btn_bgr)
            appMode.setTextColor(Color.BLACK)

            leftLauncherView.visibility = View.VISIBLE
            rightLauncherMode.visibility = View.VISIBLE
            leftAppView.visibility = View.INVISIBLE
            rightAppView.visibility = View.INVISIBLE
        }
        appMode.setOnClickListener {
            launcherMode.setBackgroundResource(R.drawable.btn_bgr)
            launcherMode.setTextColor(Color.BLACK)
            appMode.setBackgroundResource(R.drawable.btn_bgr_select)
            appMode.setTextColor(Color.WHITE)

            leftLauncherView.visibility = View.INVISIBLE
            rightLauncherMode.visibility = View.INVISIBLE
            leftAppView.visibility = View.VISIBLE
            rightAppView.visibility = View.VISIBLE
        }

        val line = findViewById<View>(R.id.line)
        val bezierView = findViewById<BezierView>(R.id.bezierView)
        line.setOnTouchListener(bezierView.onTouchListener())
        bezierView.setOnBackEvent { Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show() }
        bezierView.setOnMenuEvent { Toast.makeText(this, "菜单", Toast.LENGTH_SHORT).show() }

        val rightLine = findViewById<View>(R.id.rightLine)
        val rightBezierView = findViewById<RightBezierView>(R.id.rightBezierView)
        rightLine.setOnTouchListener(rightBezierView.onTouchListener())
        rightBezierView.setOnBackEvent { Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show() }
        rightBezierView.setOnMenuEvent { Toast.makeText(this, "菜单", Toast.LENGTH_SHORT).show() }

        val line2 = findViewById<View>(R.id.line2)
        val padBezierView = findViewById<MenuBezierView>(R.id.padBezierView)
        line2.setOnTouchListener(padBezierView.onTouchListener())
        padBezierView.setOnMenuEvent { Toast.makeText(this, "菜单", Toast.LENGTH_SHORT).show() }

        val rightLine2 = findViewById<View>(R.id.rightLine2)
        val rightPadBezierView = findViewById<RightMenuBezierView>(R.id.rightPadBezierView)
        rightLine2.setOnTouchListener(rightPadBezierView.onTouchListener())
        rightPadBezierView.setOnMenuEvent { Toast.makeText(this, "菜单", Toast.LENGTH_SHORT).show() }
    }

}