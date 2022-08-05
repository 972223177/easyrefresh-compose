package com.ly.easyrefresh.refresh.headers

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ly.easyrefresh.refresh.FooterState
import com.ly.easyrefresh.refresh.HeaderState
import com.ly.easyrefresh.refresh.drawables.ArrowDrawable
import com.ly.easyrefresh.refresh.drawables.ProgressDrawable

@Composable
fun ClassicHeader(state: HeaderState, footerState: FooterState) {
    val isLoading = footerState == FooterState.Loading
    val text = if (isLoading) "等待加载完成" else when (state) {
        HeaderState.DragToRefresh -> "下拉可以刷新"
        HeaderState.None -> ""
        is HeaderState.Refreshed -> if (state.success) "刷新成功" else "刷新失败"
        HeaderState.Refreshing -> "正在刷新..."
        HeaderState.ReleaseToRefresh -> "释放可以刷新"
    }

    val angle = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = state, block = {
        if (state == HeaderState.ReleaseToRefresh) {
            angle.animateTo(180f)
        } else {
            angle.animateTo(0f)
        }
    })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Text(text = text)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state == HeaderState.Refreshing) {
                    val progressDrawable = rememberDrawablePainter(drawable = ProgressDrawable())
                    Image(
                        painter = progressDrawable,
                        contentDescription = "progress",
                        modifier = Modifier.size(20.dp)
                    )
                } else if (state is HeaderState.Refreshed) {
                    //todo success icon
                } else {
                    val arrowDrawable = rememberDrawablePainter(drawable = ArrowDrawable())
                    Image(
                        painter = arrowDrawable, contentDescription = "arrow", modifier = Modifier
                            .size(20.dp)
                            .rotate(angle.value)
                    )
                }
                Text(
                    text = text,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .wrapContentSize()
                        .clipToBounds()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }

}