package com.embedded.grapher.screens.main

import androidx.lifecycle.ViewModel
import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.services.devicemanager.DeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(val dm: DeviceManager) : ViewModel() {
    private val _files = MutableStateFlow<List<NodeMcuFileInfo>>(emptyList())
    val files = _files.asStateFlow()


    fun updateFiles(){
        _files.tryEmit(dm.getFiles())
    }
}