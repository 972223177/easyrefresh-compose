package com.ly.easyrefresh.refresh.footers

import android.preference.PreferenceActivity.Header
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ly.easyrefresh.refresh.HeaderState
import kotlinx.coroutines.delay

@Composable
fun BallRefreshHeader(state: HeaderState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        val radius = with(LocalDensity.current) {
            8.dp.toPx()
        }
        val circleSpacing = with(LocalDensity.current) {
            8.dp.toPx()
        }

        val interpolator = remember {
            AccelerateDecelerateInterpolator()
        }
        val isRefreshing = state == HeaderState.Refreshing
        val ballColor =
            if (isRefreshing) Color(0xff33aaff) else Color(0xffeeeeee)

        val startTime = if (isRefreshing) System.currentTimeMillis() else 0L
        var targetScale by remember {
            mutableStateOf(0f)
        }

        var now by remember {
            mutableStateOf(0L)
        }
        LaunchedEffect(key1 = isRefreshing, block = {
            if (isRefreshing) {
                while (true) {
                    now = System.currentTimeMillis()
                    delay(60L)
                }
            }
        })

        Canvas(modifier = Modifier.wrapContentSize(), onDraw = {
            for (i in 0 until 3) {
                val time = now - startTime - 120 * (i + 1)
                var percent = if (time > 0) time % 750 / 750f else 0f
                percent = interpolator.getInterpolation(percent)
                targetScale = if (isRefreshing) {
                    if (percent < 0.5f) {
                        1 - percent * 2 * 0.7f
                    } else {
                        percent * 2 * 0.7f - 0.4f
                    }
                } else {
                    1f
                }
                scale(
                    scale = targetScale,
                    Offset((i - 1) * (circleSpacing + radius * 2) * 1.0f, 0f)
                ) {
                    drawCircle(
                        ballColor,
                        radius,
                        center = Offset((i - 1) * (circleSpacing + radius * 2), 0f)
                    )
                }
            }
        })
    }
}