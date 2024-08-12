package com.embedded.grapher.screens.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embedded.grapher.services.devicemanager.DeviceManager
import com.embedded.grapher.utils.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
class ConnectionScreenViewModel
@Inject constructor(private val dm: DeviceManager) : ViewModel() {

    private val scope = CoroutineScope(EmptyCoroutineContext)

//    private val _urlServer = MutableStateFlow("coap://192.168.4.1:5683/v1/f/cmd_handler")
    private val _urlServer = MutableStateFlow("192.168.4.1")
    private val _portServer = MutableStateFlow("5683")
    private val _isConnected = MutableStateFlow<Async<Boolean>>(Async.Success(false))

    val urlServer = _urlServer.asStateFlow()
    val portServer = _portServer.asStateFlow()
    val isConnected = _isConnected.asStateFlow()


    fun changeServerUrl(it: String) {
        _urlServer.value = it
    }
    fun changeServerPort(it: String) {
        _portServer.value = it
    }

    fun startConnection() {
        scope.launch {
            _isConnected.value = Async.Loading
            delay(1000)

            val connected = dm.connect(urlServer.value,_portServer.value.toIntOrNull() ?: 1024)
            _isConnected.value = if (connected) Async.Success(true) else Async.Error(-1)
        }
    }

}