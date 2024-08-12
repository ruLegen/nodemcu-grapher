package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.components.NodeMcuFileStatus
import com.embedded.grapher.utils.NodeMcuSample
import javax.inject.Inject


class FakeDeviceManager @Inject constructor() : DeviceManager {


     override suspend fun getFiles():List<NodeMcuFileInfo>{
        return (0..10).map{
            NodeMcuFileInfo(
                "id$it",
                "FileName $it",
                0,
                (if(it % 2 == 0)  NodeMcuFileStatus.RUNNING else NodeMcuFileStatus.CLOSED),
                0
            )
        }.toList()
    }

    override suspend fun connect(host: String, port: Int): Boolean {
        return true
    }


    override suspend fun getFileSamples(fileId: String): List<NodeMcuSample>? {
        val samples = mutableListOf<NodeMcuSample>()
        repeat(150){i->
            val index = i%4
            val channel =index+1
            val angle = Math.sin((index*Math.PI/3)+ Math.PI * 2 * (i/150f)).toFloat()
            val time = i/100f
            samples.add(NodeMcuSample(time,angle,channel))
        }
        return samples
    }

    override suspend fun deleteFileSample(fileId: String): Boolean {
        return true
    }

    override suspend fun stopFileRecording(file: String): Boolean {
        return true
    }

    override suspend fun startRecording(
        channel1: Boolean,
        channel2: Boolean,
        channel3: Boolean,
        channel4: Boolean
    ): Boolean {
        return true
    }

}