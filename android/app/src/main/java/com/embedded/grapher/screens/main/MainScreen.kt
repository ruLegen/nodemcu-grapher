package com.embedded.grapher.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.embedded.grapher.NavDestinations
import com.embedded.grapher.components.NodeMcuFIleInfoItem

@Composable
fun MainScreen(
    modifier: Modifier,
    vm: MainScreenViewModel = hiltViewModel(),
    onNavigateToPlot:(String) ->Unit = {},
) {
    val files by vm.files.collectAsState()
    Surface(modifier = modifier) {
        Column {
            Text("MainScreen", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.updateFiles() }) {
                    Text(text = "Update list")
                }
                Button(onClick = { vm.updateFiles() }) {
                    Text(text = "Start record")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (files.isEmpty()) {
                Text(text = "No Files", textAlign = TextAlign.Center)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    for (nodeMcuFileInfo in files) {
                        item(nodeMcuFileInfo.id) {
                            NodeMcuFIleInfoItem(
                                id = nodeMcuFileInfo.id,
                                name = nodeMcuFileInfo.name,
                                status = nodeMcuFileInfo.status,
                                onClick = { onNavigateToPlot(it) },
                                onStop = { println("Stopped $it") },
                                onDelete = { println("Deleted $it") },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}