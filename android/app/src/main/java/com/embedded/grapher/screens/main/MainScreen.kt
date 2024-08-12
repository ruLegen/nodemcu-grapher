package com.embedded.grapher.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.embedded.grapher.components.NodeMcuFIleInfoItem
import com.embedded.grapher.components.RecordStartPopup
import com.embedded.grapher.components.WrapAsync
import com.embedded.grapher.utils.isLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier,
    vm: MainScreenViewModel = hiltViewModel(),
    onNavigateToPlot: (String) -> Unit = {},
) {
    val files by vm.files.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isPopupOpened by remember { mutableStateOf(false) }

    val pullToRefreshState = rememberPullToRefreshState()
    isRefreshing = files.isLoading()

    Surface(modifier = modifier) {
        RecordStartPopup(
            isOpened = isPopupOpened,
            onDismiss = { isPopupOpened = false },
            onStartRecord = { channels ->
                if (channels.allDisabled)
                    return@RecordStartPopup
                isPopupOpened = false
                vm.startRecord(channels.channel1,channels.channel2,channels.channel3,channels.channel4)
            })
        Column {
            Text("MainScreen", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { vm.updateFiles() }) {
                    Text(text = "Update list")
                }
                Button(onClick = { isPopupOpened = true }) {
                    Text(text = "Start record")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            PullToRefreshBox(isRefreshing = isRefreshing,
                state = pullToRefreshState,
                onRefresh = {
                    vm.updateFiles()
                })
            {
                Box(modifier = Modifier.fillMaxSize()) {
                    WrapAsync(data = files, error = {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item { Text("Error when retrieving files") }
                        }
                    }) {
                        val newFiles = it
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            val isEmpty = newFiles.isEmpty()
                            if (isEmpty) {
                                item(isEmpty) {
                                    Text(
                                        text = "No Files",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            for (nodeMcuFileInfo in newFiles) {
                                item(nodeMcuFileInfo.key()) {
                                    NodeMcuFIleInfoItem(
                                        id = nodeMcuFileInfo.id,
                                        name = nodeMcuFileInfo.name,
                                        size = nodeMcuFileInfo.size,
                                        status = nodeMcuFileInfo.status,
                                        onClick = { onNavigateToPlot(it) },
                                        onStop = { vm.stopFileRecording(it) },
                                        onDelete = { vm.removeFile(it) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
