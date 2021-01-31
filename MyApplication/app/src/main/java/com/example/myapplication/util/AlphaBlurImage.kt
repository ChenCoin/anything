package com.example.myapplication.util

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator

class AlphaBlurImage(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val tag: String = "FakeBlurImage"

    private var src: Bitmap? = null

    private var bitmap: Bitmap? = null

    private var srcRect: Rect? = null

    private var baseRect: Rect? = null

    private var value: Float = 0F

    private val paint: Paint = Paint()

    private val interpolator = AccelerateInterpolator()

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            if (bitmap != null) {
                drawBitmap(canvas, bitmap!!)
            } else it.drawColor(Color.WHITE)
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        src = bitmap

        val tempBitmap = if (this.bitmap == null) {
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        } else this.bitmap
        this.bitmap = null
        val rs = RenderScript.create(context)
        val gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        gaussianBlue.setRadius(25F)
        gaussianBlue.setInput(Allocation.createFromBitmap(rs, bitmap))
        val output = Allocation.createFromBitmap(rs, tempBitmap)
        gaussianBlue.forEach(output)
        output.copyTo(tempBitmap)
        rs.destroy()
        srcRect = Rect(0, 0, bitmap.width, bitmap.height)
        this.bitmap = tempBitmap
    }

    fun setRadius(value: Float) {
        if (value < 0 || value > 100) {
            return
        }
        this.value = value
        invalidate()
    }

    private fun drawBitmap(canvas: Canvas, bitmap: Bitmap) {
        val alpha = (interpolator.getInterpolation(1 - value) * 255).toInt()
        if (alpha != 255) {
            canvas.drawBitmap(bitmap, srcRect, canvasRect(), null)
        }

        paint.alpha = alpha
        canvas.drawBitmap(src!!, srcRect, canvasRect(), paint)
    }

    private fun canvasRect(): Rect {
        if (baseRect == null) {
            baseRect = Rect(0, 0, width, height)
        }
        return baseRect!!
    }
}