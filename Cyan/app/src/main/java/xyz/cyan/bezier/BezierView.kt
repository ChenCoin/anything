package xyz.cyan.bezier

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import xyz.cyan.R
import java.util.*

class BezierView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val tag = "BezierView"

    private val path: Path = Path()

    private val lineMargin: Float = context.resources.getDimension(R.dimen.lineMargin)

    private val effectSize: Float = context.resources.getDimension(R.dimen.effectSize)

    private val maxEffect: Float = context.resources.getDimension(R.dimen.maxEffect)

    private val minBack: Float = context.resources.getDimension(R.dimen.minBack)

    private val paint: Paint = Paint().also {
        it.color = Color.parseColor("#80000000")
    }

    private var touchX = 0F

    private var touchY = 0F

    private var enable = false

    private var backEvent: () -> Unit = {}

    private var menuEvent: () -> Unit = {}

    private var hadTryMenuEvent = false

    private var menuEventHappened = false

    private var timer: Timer? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!enable) {
            return
        }
        Log.v(tag, "$touchX $touchY $effectSize")
        path.reset()
        path.moveTo(0F, touchY - effectSize / 2)
        path.cubicTo(
            0F, touchY - effectSize / 4,
            touchX, touchY - effectSize / 4,
            touchX, touchY
        )
        path.cubicTo(
            touchX, touchY + effectSize / 4,
            0F, touchY + effectSize / 4,
            0F, touchY + effectSize / 2
        )
        canvas.drawPath(path, paint)
    }

    fun onTouchListener(): OnTouchListener {
        return MyOnTouchListener(::onMove)
    }

    fun setOnBackEvent(backEvent: () -> Unit) {
        this.backEvent = backEvent
    }

    fun setOnMenuEvent(menuEvent: () -> Unit) {
        this.menuEvent = menuEvent
    }

    private fun onMove(event: MotionEvent, x: Float): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            enable = true
            hadTryMenuEvent = false
            menuEventHappened = false
            touchY = event.y + lineMargin
            return true
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            touchX = x.coerceAtMost(maxEffect)
            runMenuEvent(x)
        }
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            touchX = x.coerceAtMost(maxEffect)
            if (!menuEventHappened) {
                if (hadTryMenuEvent) {
                    timer?.cancel()
                }
                runBackEvent(x)
            }
            onEndAnim()
        }
        invalidate()

        Log.v(tag, "${event.action} ${event.x}")
        return false
    }

    private fun onEndAnim() {
        val anim = ValueAnimator.ofFloat(touchX, 0F)
        anim.duration = 200
        anim.addUpdateListener { _ ->
            touchX = anim.animatedValue as Float
            invalidate()
        }
        anim.addListener(onEnd = { enable = false })
        anim.start()
    }

    private fun runBackEvent(x: Float) {
        if (x > minBack) {
            backEvent()
        }
    }

    private fun runMenuEvent(x: Float) {
        if (hadTryMenuEvent) {
            return
        }
        if (x >= maxEffect) {
            hadTryMenuEvent = true
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    menuEventHappened = true
                    ContextCompat.getMainExecutor(context).execute(menuEvent)
                }
            }, 500)
        }
    }

    private class MyOnTouchListener(val onMove: (MotionEvent, Float) -> Boolean) : OnTouchListener {
        private var firstX = 0F

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            Log.v("TagTest", "event")
            return event?.let {
                var x = 0F
                if (it.action == MotionEvent.ACTION_DOWN) {
                    firstX = it.x
                } else {
                    x = it.x - firstX
                }
                onMove(it, x)
            } ?: false
        }
    }
}