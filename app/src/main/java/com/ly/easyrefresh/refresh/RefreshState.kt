package com.ly.easyrefresh.refresh

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

fun refreshState(
    enableRefresh: Boolean = true,
    triggerRate: Float = 1f,
    maxDragRate: Float = 2.5f,
) = RefreshState(
    enableRefresh = enableRefresh,
    maxDragRate = maxDragRate,
    triggerRate = triggerRate,
)

@Composable
fun rememberRefreshState(
    enableRefresh: Boolean = true,
    triggerRate: Float = 1f,
    maxDragRate: Float = 2.5f,
): RefreshState = remember {
    RefreshState(
        enableRefresh = enableRefresh,
        maxDragRate = maxDragRate,
        triggerRate = triggerRate,
    )
}

class RefreshState(
    var enableRefresh: Boolean,

    val maxDragRate: Float,
    val triggerRate: Float,
) {
    var onRefresh: () -> Unit = {}
    var onLoad: () -> Unit = {}

    lateinit var scope: CoroutineScope

    private var _offsetY = Animatable(0f)

    //    var enableRefresh: Boolean by mutableStateOf(enableRefresh)
    var noMore: Boolean by mutableStateOf(false)

    var headerHeight: Float by mutableStateOf(0f)

    var footerHeight: Float by mutableStateOf(0f)

    var refreshHeight: Float = 0f

    var headerState: HeaderState by mutableStateOf(HeaderState.None)

    var footerState: FooterState by mutableStateOf(FooterState.None)

    internal val offsetY: Float get() = _offsetY.value

    internal val refreshTrigger: Float get() = headerHeight * triggerRate

    internal val maxRefreshDrag: Float get() = headerHeight * maxDragRate

    private val mutatorMutex = MutatorMutex()

    internal suspend fun animateOffsetTo(offset: Float) {
        mutatorMutex.mutate {
            _offsetY.animateTo(offset)
        }
    }

    internal fun isHeaderNeedClip(): Boolean = _offsetY.value > 0 && _offsetY.value < headerHeight

    internal fun isFooterNeedClip(): Boolean =
        _offsetY.value > refreshHeight && _offsetY.value < (refreshHeight + footerHeight)


    internal suspend fun dispatchScrollDelta(delta: Float, dragUp: Boolean = false) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            val nextOffsetY = _offsetY.value + delta
            if (dragUp) {
                if (abs(nextOffsetY) <= footerHeight) {
                    _offsetY.snapTo(nextOffsetY)
                }
            } else {
                if (nextOffsetY <= maxRefreshDrag) {
                    headerState = if (nextOffsetY >= refreshTrigger) {
                        HeaderState.ReleaseToRefresh
                    } else {
                        HeaderState.DragToRefresh
                    }
                    _offsetY.snapTo(nextOffsetY)
                }
            }
        }
    }

    fun refresh() {
        if (headerState == HeaderState.Refreshing || footerState == FooterState.Loading) return
        scope.launch {
            mutatorMutex.mutate {
                headerState = HeaderState.Refreshing
                _offsetY.animateTo(refreshTrigger)
                onRefresh()
            }
        }
    }

    fun loadMore() {
        if (headerState == HeaderState.Refreshing || footerState == FooterState.Loading) return
        scope.launch {
            mutatorMutex.mutate {
                footerState = FooterState.Loading
                _offsetY.animateTo(-footerHeight)
                onLoad()
            }
        }
    }

    internal fun resetHeader() {
        scope.launch {
            animateOffsetTo(0f)
            headerState = HeaderState.None
        }
    }

    internal fun resetFooter() {
        scope.launch {
            animateOffsetTo(0f)
            footerState = FooterState.None
        }
    }

    fun finishRefresh(success: Boolean = true, noMore: Boolean = false, delay: Long = 500L) {
        scope.launch {
            headerState = HeaderState.Refreshed(success)
            this@RefreshState.noMore = noMore
            kotlinx.coroutines.delay(delay)
            animateOffsetTo(0f)
            headerState = HeaderState.None
        }
    }


    fun finishLoadMore(success: Boolean = true, noMore: Boolean = false, delay: Long = 500L) {
        scope.launch {
            footerState = FooterState.Loaded(success)
            this@RefreshState.noMore = noMore
            kotlinx.coroutines.delay(delay)
            animateOffsetTo(0f)
            footerState = FooterState.None
        }
    }
}

