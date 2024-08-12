package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.components.NodeMcuFileStatus
import com.embedded.grapher.utils.NodeMcuSample
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.Utils
import javax.inject.Inject


class FakeDeviceManager @Inject constructor() : DeviceManager {

    private var client : CoapClient? = null

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
        TODO("Not yet implemented")
    }

    override suspend fun deleteFileSample(fileId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun stopFileRecording(file: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun startRecording(
        channel1: Boolean,
        channel2: Boolean,
        channel3: Boolean,
        channel4: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    fun printResponse(response: CoapResponse?) {
        if (response != null) {
            println("${response.code} - ${response.code.name}")
            println("${response.options}")
            println(response.responseText)
            println("Advanced: ")
            val context = response.advanced().sourceContext
            val identity = context.peerIdentity
            if (identity != null)
                println(context.peerIdentity)
            else
                println("Anonymous")
            println(Utils.prettyPrint(response))
        } else
            println("No response received.")

        println("\n")
    }
}