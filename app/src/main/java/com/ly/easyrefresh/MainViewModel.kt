package com.ly.easyrefresh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.easyrefresh.refresh.refreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val refreshState = refreshState()

    fun refresh(){
        viewModelScope.launch {
            delay(2000L)
            refreshState.finishRefresh(noMore = true)
        }
    }

    fun loadMore(){
        viewModelScope.launch {
            delay(2000L)
            refreshState.finishLoadMore()
        }
    }
}