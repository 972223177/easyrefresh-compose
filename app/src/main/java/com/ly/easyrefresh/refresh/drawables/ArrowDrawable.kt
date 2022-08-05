package com.ly.easyrefresh.refresh.drawables

import android.graphics.Canvas
import android.graphics.Path

class ArrowDrawable : PaintDrawable() {

    private var mWidth = 0f
    private var mHeight = 0f
    private val mPath = Path()

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()
        if (mWidth != width || mHeight != height) {
            val lineWidth = width * 30 / 225
            mPath.reset()
            val vector1 = lineWidth * 0.70710677f //Math.sin(Math.PI/4));
            val vector2 = lineWidth / 0.70710677f //Math.sin(Math.PI/4));
            mPath.moveTo(width / 2f, height)
            mPath.lineTo(0f, height / 2f)
            mPath.lineTo(vector1, height / 2f - vector1)
            mPath.lineTo(width / 2f - lineWidth / 2f, height - vector2 - lineWidth / 2f)
            mPath.lineTo(width / 2f - lineWidth / 2f, 0f)
            mPath.lineTo(width / 2f + lineWidth / 2f, 0f)
            mPath.lineTo(width / 2f + lineWidth / 2f, height - vector2 - lineWidth / 2f)
            mPath.lineTo(width - vector1, height / 2f - vector1)
            mPath.lineTo(width, height / 2f)
            mPath.close()
            mWidth = width
            mHeight = height
        }
        canvas.drawPath(mPath, paint)
    }
}