package com.ly.easyrefresh.refresh.footers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ly.easyrefresh.refresh.FooterState
import com.ly.easyrefresh.refresh.HeaderState
import com.ly.easyrefresh.refresh.drawables.ArrowDrawable
import com.ly.easyrefresh.refresh.drawables.ProgressDrawable

@Composable
fun ClassicFooter(headerState: HeaderState, state: FooterState, noMore: Boolean) {
    val text = when {
        noMore -> "没有更多数据了"
        headerState == HeaderState.Refreshing -> "等待刷新完成"
        else -> when (state) {
            is FooterState.Loaded -> if (state.success) "加载完成" else "加载失败"
            FooterState.Loading -> "正在加载..."
            FooterState.None -> ""
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state is FooterState.Loading) {
                Image(
                    painter = rememberDrawablePainter(drawable = ProgressDrawable()),
                    contentDescription = "progress",
                    modifier = Modifier.size(20.dp)
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