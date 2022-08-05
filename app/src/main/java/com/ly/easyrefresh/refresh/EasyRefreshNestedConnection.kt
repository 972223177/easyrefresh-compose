package com.ly.easyrefresh.refresh

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue

private const val DragMultiplier = 0.5f

class EasyRefreshNestedConnection(
    private val state: RefreshState,
    private val coroutineScope: CoroutineScope,

    ) : NestedScrollConnection {
    var enabled: Boolean = false
    var refreshTrigger: Float = 0f
    var isDragUp = false

    @Suppress("KotlinConstantConditions")
    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        !enabled -> Offset.Zero
        state.headerState == HeaderState.Refreshing -> Offset.Zero
        source == NestedScrollSource.Drag && available.y < 0 && !isDragUp -> onScrollDown(available)
        else -> Offset.Zero
    }

    @Suppress("KotlinConstantConditions")
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        isDragUp = source == NestedScrollSource.Drag && available.y < 0
        return when {
            !enabled -> Offset.Zero
            state.headerState == HeaderState.Refreshing -> Offset.Zero
            source == NestedScrollSource.Drag && available.y > 0 -> onScrollDown(available)
            isDragUp && consumed.y == 0.0f -> onScrollUp(available)
            else -> Offset.Zero
        }
    }

    private fun onScrollUp(available: Offset): Offset = onScroll(available, true)

    private fun onScrollDown(available: Offset): Offset = onScroll(available, false)

    private fun onScroll(available: Offset, dragUp: Boolean): Offset {
        if (isFetchState() && !state.noMore) return Offset.Zero
        val newOffset = (available.y * DragMultiplier + state.offsetY).let {
            if (dragUp) it.coerceAtMost(0f) else it.coerceAtLeast(0f)
        }
        val dragConsumed = newOffset - state.offsetY
        val consumed = if (dragConsumed.absoluteValue >= 0.5f) {
            coroutineScope.launch {
                state.dispatchScrollDelta(dragConsumed, dragUp)
            }
            Offset(0f, dragConsumed / DragMultiplier)
        } else {
            Offset.Zero
        }
        return consumed
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (!isDragUp) {
            if (!isRefreshState()) {
                when {
                    state.offsetY >= refreshTrigger -> state.refresh()
                    isLoadState() -> {}
                    else -> state.resetHeader()
                }
            }
        } else {
            if (state.noMore) {
                state.resetFooter()
                return Velocity.Zero
            }
            val offsetYAbs = abs(state.offsetY)
            if (!isLoadState()) {
                when {
                    offsetYAbs > 0 && offsetYAbs < state.footerHeight -> state.loadMore()
                    isRefreshState() -> {}
                    else -> state.resetFooter()
                }
            }
        }
        return Velocity.Zero
    }

    private fun isRefreshState(): Boolean =
        state.headerState == HeaderState.Refreshing || state.headerState is HeaderState.Refreshed

    private fun isLoadState(): Boolean =
        state.footerState == FooterState.Loading || state.footerState is FooterState.Loaded

    private fun isFetchState(): Boolean = state.headerState == HeaderState.Refreshing ||
            state.headerState is HeaderState.Refreshed ||
            state.footerState == FooterState.Loading ||
            state.footerState is FooterState.Loaded
}