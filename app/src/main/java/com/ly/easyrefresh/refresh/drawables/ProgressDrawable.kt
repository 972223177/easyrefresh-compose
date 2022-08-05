package com.ly.easyrefresh.refresh.drawables

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable


class ProgressDrawable : PaintDrawable(), Animatable, ValueAnimator.AnimatorUpdateListener {
    private var mWidth = 0f
    private var mHeight = 0f
    private var mProgressDegree = 0f
    private val mValueAnimator = ValueAnimator.ofInt(30, 3600).apply {
        duration = 10 * 1000
        interpolator = null
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
    }
    private val mPath = Path()

    override fun draw(canvas: Canvas) {
        val drawable: Drawable = this@ProgressDrawable
        val bounds: Rect = drawable.bounds
        val width: Int = bounds.width()
        val height: Int = bounds.height()
        val r = 1f.coerceAtLeast(width / 22f)

        if (mWidth != width.toFloat() || mHeight != height.toFloat()) {
            mPath.reset()
            mPath.addCircle(width - r, height / 2f, r, Path.Direction.CW)
            mPath.addRect(
                width - 5 * r,
                height / 2f - r,
                width - r,
                height / 2f + r,
                Path.Direction.CW
            )
            mPath.addCircle(width - 5 * r, height / 2f, r, Path.Direction.CW)
            mWidth = width.toFloat()
            mHeight = height.toFloat()
        }

        canvas.save()
        canvas.rotate(mProgressDegree, width / 2f, height / 2f)
        for (i in 0..11) {
            paint.alpha = (i + 5) * 0x11
            canvas.rotate(30f, width / 2f, height / 2f)
            canvas.drawPath(mPath, paint)
        }
        canvas.restore()
    }

    override fun start() {
        with(mValueAnimator) {
            if (!isRunning) {
                addUpdateListener(this@ProgressDrawable)
                start()
            }
        }
    }

    override fun stop() {
        with(mValueAnimator) {
            if (isRunning) {
                removeAllListeners()
                removeAllUpdateListeners()
                cancel()
            }
        }
    }

    override fun isRunning(): Boolean = mValueAnimator.isRunning

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        val value = (animation?.animatedValue as? Int) ?: 0
        mProgressDegree = 30 * (value / 30f)
        invalidateSelf()
    }
}