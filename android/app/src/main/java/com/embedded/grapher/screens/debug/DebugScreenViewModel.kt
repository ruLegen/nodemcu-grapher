package com.embedded.grapher.screens.debug

import androidx.lifecycle.ViewModel
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
class DebugScreenViewModel @Inject constructor(val dm: DeviceManager) : ViewModel() {
    private val scope = CoroutineScope(EmptyCoroutineContext)

    private val _urlServer = MutableStateFlow("coap://192.168.4.1:5683/v1/f/cmd_handler")
    private val _isConnected = MutableStateFlow<Async<Boolean>>(Async.Success(false))

    val urlServer = _urlServer.asStateFlow()
    val isConnected = _isConnected.asStateFlow()


    fun changeServerUrl(it: String) {
        TODO("Not yet implemented")
    }

    fun startConnection() {
        scope.launch {
            _isConnected.value = Async.Loading
            delay(1000)
            val connected = dm.connect("",0)
            _isConnected.value = if (connected) Async.Success(true) else Async.Error(-1)
        }
    }

}