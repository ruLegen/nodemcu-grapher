package com.embedded.grapher.screens.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.embedded.grapher.components.WrapAsync
import com.embedded.grapher.utils.Async
import kotlinx.coroutines.CoroutineScope

@Composable
fun DebugScreen(
    modifier: Modifier,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    vm: DebugScreenViewModel = hiltViewModel()
) {
    val urlServer by vm.urlServer.collectAsState()
    val isConnected by vm.isConnected.collectAsState()
    Column(Modifier.padding(10.dp, 0.dp)) {
        TextField(value = urlServer, onValueChange = { vm.changeServerUrl(it) })
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            vm.startConnection()
        }) {
            Text("Connect")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Result")
        WrapAsync(isConnected,
            loading = { LinearProgressIndicator() },
            error = { Text("Connection Error") }) {

            Text("connection $isConnected")
        }
    }
}

