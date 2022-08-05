package com.ly.easyrefresh.refresh

import androidx.compose.ui.unit.IntOffset

enum class RefreshStyle {
    Translate, FixedBehind, FixedFront, FixedContent
}

fun RefreshStyle.headerZIndex(): Float = when (this) {
    RefreshStyle.FixedFront, RefreshStyle.FixedContent -> 1f
    else -> 0f
}

fun RefreshStyle.headerOffset(state: RefreshState): IntOffset = when (this) {
    RefreshStyle.Translate -> IntOffset(0, (state.offsetY - state.headerHeight).toInt())
    RefreshStyle.FixedBehind, RefreshStyle.FixedFront -> IntOffset.Zero
    RefreshStyle.FixedContent -> IntOffset(0, (state.offsetY - state.headerHeight).toInt())
}

fun RefreshStyle.contentOffset(state: RefreshState): IntOffset = when (this) {
    RefreshStyle.Translate -> IntOffset(0, state.offsetY.toInt())
    RefreshStyle.FixedBehind -> IntOffset(0, state.headerHeight.toInt())
    else -> IntOffset.Zero
}

fun footerOffset(state:RefreshState):IntOffset = IntOffset(0, (state.refreshHeight + state.offsetY).toInt())
