package com.ly.easyrefresh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ly.easyrefresh.refresh.EasyRefresh
import com.ly.easyrefresh.refresh.footers.BallRefreshHeader
import com.ly.easyrefresh.refresh.rememberRefreshState
import com.ly.easyrefresh.ui.theme.EasyRefreshTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyRefreshTheme {
                EasyRefresh(
                    state = viewModel.refreshState,
                    modifier = Modifier.fillMaxSize(),
                    header = { headerState, _ ->
                        BallRefreshHeader(state = headerState)
                    },
                    onRefresh = {
                        viewModel.refresh()
                    },
                    onLoad = {
                        viewModel.loadMore()
                    }) {
                    LazyColumn {
                        items(count = 20) {
                            TestItem(index = it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TestItem(index: Int) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.White)
                .border(width = 1.dp, color = Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = index.toString())
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EasyRefreshTheme {
        Greeting("Android")
    }
}