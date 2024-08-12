package com.embedded.grapher.screens.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.embedded.grapher.components.WrapAsync
import com.embedded.grapher.services.devicemanager.FakeDeviceManager
import com.embedded.grapher.utils.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ConnectionScreen(
    vm: ConnectionScreenViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    modifier: Modifier = Modifier,
    onConnected: () -> Unit = {}
) {
    val urlServer by vm.urlServer.collectAsState()
    val portServer by vm.portServer.collectAsState()
    val isConnected by vm.isConnected.collectAsState()

    Surface(modifier = modifier) {
        Column(Modifier.padding(10.dp, 0.dp), verticalArrangement = Arrangement.Center) {
            Row {
                Column(modifier = Modifier.weight(4f)) {
                    Text("Server")
                    TextField(
                        value = urlServer,
                        onValueChange = { vm.changeServerUrl(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column(modifier = Modifier.weight(2f)) {
                    Text("Port")
                    TextField(
                        value = portServer,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { vm.changeServerPort(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { vm.startConnection() },
                    enabled = isConnected !is Async.Loading
                ) {
                    Text("Connect")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Result")
            WrapAsync(isConnected,
                loading = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) },
                error = { Text("Connection Error") }) {

                val connected = it
                Text("connection $connected")
                if (connected) {
                    LaunchedEffect(isConnected) {
                        onConnected()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    ConnectionScreen(vm = ConnectionScreenViewModel(FakeDeviceManager()))
}