package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.utils.NodeMcuSample

interface DeviceManager {
    suspend fun getFiles(): List<NodeMcuFileInfo>?

    suspend fun connect(host:String, port:Int): Boolean
    suspend fun getFileSamples(fileId: String): List<NodeMcuSample>?
    suspend fun deleteFileSample(fileId: String):Boolean
    suspend fun stopFileRecording(file: String): Boolean
    suspend fun startRecording(channel1: Boolean, channel2: Boolean, channel3: Boolean, channel4: Boolean): Boolean

}