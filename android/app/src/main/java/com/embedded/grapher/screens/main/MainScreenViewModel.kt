package com.embedded.grapher.screens.main

import androidx.lifecycle.ViewModel
import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.components.NodeMcuFileStatus
import com.embedded.grapher.services.devicemanager.DeviceManager
import com.embedded.grapher.utils.Async
import com.embedded.grapher.utils.isLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext


@HiltViewModel
class MainScreenViewModel @Inject constructor(val dm: DeviceManager) : ViewModel() {
    private var prevFiles: List<NodeMcuFileInfo> = emptyList()
    private val scope = CoroutineScope(EmptyCoroutineContext)
    private val _files: MutableStateFlow<Async<List<NodeMcuFileInfo>>> =
        MutableStateFlow(
            Async.Success(
                emptyList()
            )
        )
    val files = _files.asStateFlow()


    fun updateFiles() {
        if(_files.value.isLoading())
            return
        _files.value = Async.Loading
        scope.launch {
            delay(1000)
            val files = dm.getFiles()
            if(files == null){
                _files.tryEmit(Async.Error(-1))
                prevFiles = emptyList()
            }
            else{
                prevFiles = files
                _files.tryEmit(Async.Success(prevFiles))
            }
        }
    }

    fun removeFile(file:String){
        scope.launch {
            var removed = dm.deleteFileSample(file)
            if(removed){
                _files.tryEmit(Async.Loading)
                prevFiles = prevFiles.filter { it.id != file }.toList()
                _files.tryEmit(Async.Success(prevFiles))
            }
        }
    }

    fun stopFileRecording(file: String) {
        scope.launch {
            val stoped = dm.stopFileRecording(file)
            if(stoped){
                prevFiles = prevFiles.map {
                    if(it.id == file){
                       return@map NodeMcuFileInfo(it.id,it.name,NodeMcuFileStatus.CLOSED)
                    }
                    it
                }.toList()
                _files.tryEmit( Async.Success(prevFiles))
            }
        }

    }

    fun startRecord(channel1: Boolean, channel2: Boolean =false, channel3: Boolean=false, channel4: Boolean=false) {
        scope.launch {
            val started = dm.startRecording(channel1,channel2,channel3,channel4)
            if(started){
                updateFiles()
            }
        }
    }
}