package com.ly.easyrefresh.refresh

sealed class HeaderState {
    object None : HeaderState()
    object DragToRefresh : HeaderState()
    object ReleaseToRefresh : HeaderState()
    object Refreshing : HeaderState()
    data class Refreshed(val success: Boolean = true) : HeaderState()

    override fun toString(): String = this::class.simpleName ?: ""
}

sealed class FooterState {
    object None : FooterState()
    object Loading : FooterState()
    data class Loaded(val success: Boolean = true) : FooterState()

    override fun toString(): String = this::class.simpleName ?: ""
}