package com.ly.easyrefresh.refresh.drawables

import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt


abstract class PaintDrawable : Drawable() {
    protected val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = 0xffaaaaaa.toInt()
    }

    fun setColor(@ColorInt color: Int) {
        paint.color = color
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT


}