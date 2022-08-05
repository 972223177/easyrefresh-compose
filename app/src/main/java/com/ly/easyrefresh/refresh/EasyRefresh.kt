package com.ly.easyrefresh.refresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import com.ly.easyrefresh.refresh.footers.ClassicFooter
import com.ly.easyrefresh.refresh.headers.ClassicHeader

@Composable
fun EasyRefresh(
    state: RefreshState,
    modifier: Modifier = Modifier,
    refreshStyle: RefreshStyle = RefreshStyle.Translate,
    header: @Composable (HeaderState, FooterState) -> Unit = { headerState, footerState ->
        ClassicHeader(state = headerState, footerState = footerState)
    },
    footer: @Composable (HeaderState, FooterState, Boolean) -> Unit = { headerState, footerState, noMore ->
        ClassicFooter(headerState = headerState, state = footerState, noMore = noMore)
    },
    onRefresh: () -> Unit = {},
    onLoad: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val refreshCallback by rememberUpdatedState(newValue = onRefresh)
    val loadCallback by rememberUpdatedState(newValue = onLoad)
    LaunchedEffect(key1 = refreshCallback, key2 = loadCallback, key3 = scope) {
        state.onRefresh = refreshCallback
        state.onLoad = loadCallback
        state.scope = scope
    }
    val nestedConnection = remember {
        EasyRefreshNestedConnection(state, scope)
    }.also {
        it.refreshTrigger = state.refreshTrigger
        it.enabled = state.enableRefresh
    }
    Box(modifier = modifier
        .nestedScroll(nestedConnection)
        .onGloballyPositioned {
            state.refreshHeight = it.size.height.toFloat()
        }) {
        Box(modifier = Modifier
            .onGloballyPositioned {
                state.headerHeight = it.size.height.toFloat()
            }
            .let {
                if (state.isHeaderNeedClip()) it.clipToBounds() else it
            }
            .offset { refreshStyle.headerOffset(state) }
            .zIndex(refreshStyle.headerZIndex())) {
            header(state.headerState, state.footerState)
        }

        Box(modifier = Modifier
            .onGloballyPositioned {
                state.footerHeight = it.size.height.toFloat()
            }
            .let {
                val needClip = state.isFooterNeedClip()
                if (needClip) it.clipToBounds() else it
            }
            .offset { footerOffset(state) }
            .zIndex(refreshStyle.headerZIndex())
        ) {
            footer(state.headerState, state.footerState, state.noMore)
        }

        Box(modifier = Modifier.offset {
            refreshStyle.contentOffset(state)
        }) {
            content()
        }


    }


}