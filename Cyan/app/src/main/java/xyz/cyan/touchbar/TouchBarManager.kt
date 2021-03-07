package xyz.cyan.touchbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import xyz.cyan.R

class TouchBarManager(private val root: View) {
    private val tag = "TouchBarManager"

    private val touchArea = root.findViewById<View>(R.id.touchArea)

    private val viewArea = root.findViewById<View>(R.id.viewArea)

    private val realLine = root.findViewById<View>(R.id.realLine)

    private val fakeLine = root.findViewById<View>(R.id.fakeLine)

    private val touchBarHeight: Float

    private val fakeHeight: Float

    private val fakePosition: Float

    private var downY = 0F

    private var touchActive = false

    init {
        val resources = root.context.resources
        fakeHeight = resources.getDimension(R.dimen.fakeHeight)
        touchBarHeight = resources.getDimension(R.dimen.touchBarHeight)
        fakePosition = touchBarHeight - fakeHeight
    }

    fun init() {
        @SuppressLint("ClickableViewAccessibility")
        val onTouchAreaTouch = View.OnTouchListener { _, event ->
            onTouchAreaTouch(event) {
                viewArea.y = fakePosition
                viewArea.alpha = 0F
                downY = event.rawY - viewArea.y
                viewArea.visibility = View.VISIBLE
                realLine.visibility = View.INVISIBLE
                fakeLine.alpha = 1F
            }
        }
        touchArea.setOnTouchListener(onTouchAreaTouch)

        @SuppressLint("ClickableViewAccessibility")
        val onViewAreaTouch = View.OnTouchListener { _, event ->
            onTouchAreaTouch(event) { downY = event.rawY }
        }
        viewArea.setOnTouchListener(onViewAreaTouch)
    }

    private fun onTouchAreaTouch(event: MotionEvent, onDown: () -> Unit): Boolean {
        if (touchActive && event.action == MotionEvent.ACTION_DOWN) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchActive = true
                onDown()
            }
            MotionEvent.ACTION_MOVE -> {
                viewArea.y = (event.rawY - downY).coerceAtMost(fakePosition).coerceAtLeast(0F)
                val alpha: Float = viewArea.y / fakePosition
                fakeLine.alpha = alpha
                viewArea.alpha = 1 - alpha
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                when {
                    viewArea.y == 0F -> {
                        touchActive = false
                    }
                    viewArea.y < touchBarHeight * 2 / 5 -> {
                        animationShowViewArea { touchActive = false }
                    }
                    else -> {
                        animationHideViewArea {
                            touchActive = false
                            viewArea.visibility = View.INVISIBLE
                            realLine.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        return true
    }

    private fun animationShowViewArea(onAnimEnd: (Animator) -> Unit) {
        val animMove = ObjectAnimator.ofFloat(viewArea, "translationY", viewArea.y, 0F)
        val animAlpha = ObjectAnimator.ofFloat(viewArea, "alpha", viewArea.alpha, 1F)
        val animAlphaLine = ObjectAnimator.ofFloat(fakeLine, "alpha", fakeLine.alpha, 0F)
        val animSet = AnimatorSet()
        animSet.playTogether(animMove, animAlpha, animAlphaLine)
        animSet.interpolator = DecelerateInterpolator()
        animSet.duration = (1000 * fakeLine.alpha).toLong()
        Log.v(tag, "fakeLine.alpha: " + fakeLine.alpha + ", duration: " + animSet.duration)
        animSet.addListener(onEnd = onAnimEnd)
        animSet.start()
    }

    private fun animationHideViewArea(onAnimEnd: (Animator) -> Unit) {
        val animMove = ObjectAnimator.ofFloat(viewArea, "translationY", viewArea.y, fakePosition)
        val animAlpha = ObjectAnimator.ofFloat(viewArea, "alpha", viewArea.alpha, 0F)
        val animAlphaLine = ObjectAnimator.ofFloat(fakeLine, "alpha", fakeLine.alpha, 1F)
        val animSet = AnimatorSet()
        animSet.playTogether(animMove, animAlpha, animAlphaLine)
        animSet.interpolator = DecelerateInterpolator()
        animSet.duration = (1000 * (1 - fakeLine.alpha)).toLong()
        Log.v(tag, "fakeLine.alpha: " + fakeLine.alpha + ", duration: " + animSet.duration)
        animSet.addListener(onEnd = onAnimEnd)
        animSet.start()
    }

}