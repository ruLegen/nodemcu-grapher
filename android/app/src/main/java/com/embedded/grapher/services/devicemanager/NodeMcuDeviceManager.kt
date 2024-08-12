package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.components.NodeMcuFileStatus
import com.embedded.grapher.utils.NodeMcuSample
import com.embedded.grapher.utils.NodeMcuSampleHelper
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.Utils
import org.eclipse.californium.core.coap.MediaTypeRegistry
import java.net.Socket
import java.net.URI
import javax.inject.Inject

class NodeMcuDeviceManager @Inject constructor() : DeviceManager {

    private var serverHost: String = ""
    private var serverUri: URI? = null
    private val client: CoapClient?
        get() {
            if (serverUri == null)
                return null
            return CoapClient(serverUri).apply {
                timeout = 8000
                useEarlyNegotiation(32)
                useExecutor()
            }
        }

    override suspend fun connect(url: String, port: Int): Boolean {
        val urltring = "coap://$url:$port/v1/f/cmd_handler"
        serverHost = url
        serverUri = URI(urltring)
        val coapClient = client!!
        return checkConnection(coapClient)
    }

    private fun checkConnection(coapClient: CoapClient): Boolean {
        try {
            val resp = client!!.post("heartbeat".toByteArray(), MediaTypeRegistry.TEXT_PLAIN)
            if (resp == null)
                return false
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    override suspend fun getFiles(): List<NodeMcuFileInfo>? {
        try {
            val coapClient = client!!
            val isConnected = checkConnection(coapClient)
            if (!isConnected)
                return null

            val resp = coapClient.post("files_list".toByteArray(), MediaTypeRegistry.TEXT_PLAIN)
            if (resp == null)
                return null
            val text = resp.responseText
            if (text.isEmpty())
                return emptyList()
            val fileInfos = text.split("@").map {
                println(it)
                val fields = it.split(";")

                val name = fields.tryGet(0, "NO_NAME")
                val size = fields.tryGet(1, "0").toInt()
                val status = fields.tryGet(2, "0").toInt()
                NodeMcuFileInfo(name, name, NodeMcuFileStatus.fromInt(status))
            }.toList()
            return fileInfos
        } catch (ex: Exception) {
            println(ex)
            return null
        }
    }

    override suspend fun getFileSamples(fileId: String): List<NodeMcuSample>? {
        try {
            val coapClient = client!!
            val isConnected = checkConnection(coapClient)
            if (!isConnected)
                return null
            val resp =
                coapClient.post("files_read_${fileId}".toByteArray(), MediaTypeRegistry.TEXT_PLAIN)
            if (resp == null)
                return null
            val text = resp.responseText
            if (text.isEmpty())
                return null
            val port = text.toIntOrNull() ?: 0
            if(port == 0)
                return null
            print(port)
            val client = Socket(serverHost, port)
            val bytes = client.getInputStream().readBytes()
            client.close()
            if(bytes == null)
                return null
            val samples = NodeMcuSampleHelper.decodeSamples(bytes)
            return samples
        } catch (ex: Exception) {
            println(ex)
            return null
        }
    }

    override suspend fun deleteFileSample(fileId: String):Boolean{
        try {
            val coapClient = client!!
            val isConnected = checkConnection(coapClient)
            if (!isConnected)
                return false
            val resp =
                coapClient.post("files_remove_${fileId}".toByteArray(), MediaTypeRegistry.TEXT_PLAIN)
            if (resp == null)
                return false
            return true
        } catch (ex: Exception) {
            println(ex)
            return false
        }
    }

    override suspend fun stopFileRecording(fileId: String): Boolean {
        val stringId = fileId.split(".").firstOrNull()
        if(stringId == null)
            return false
        try {
            val coapClient = client!!
            val isConnected = checkConnection(coapClient)
            if (!isConnected)
                return false
            val resp =
                coapClient.post("stream_stop_${stringId}".toByteArray(), MediaTypeRegistry.TEXT_PLAIN)
            if (resp == null)
                return false
            val textResponse =  resp.responseText
            return textResponse == stringId
        } catch (ex: Exception) {
            println(ex)
            return false
        }
    }
    data class ChannelStatus(val num:Int,val enabled:Boolean)

    override suspend fun startRecording(
        channel1: Boolean,
        channel2: Boolean,
        channel3: Boolean,
        channel4: Boolean
    ): Boolean {
        if(!(channel1 || channel2 || channel3 || channel4))
            return false
        val channels = arrayOf(ChannelStatus(1,channel1),ChannelStatus(2,channel2),ChannelStatus(3,channel3),ChannelStatus(4,channel4))
        val channelsToStart = channels.joinToString(separator = "_") { it.num.toString() }
        try {
            val coapClient = client!!
            val isConnected = checkConnection(coapClient)
            if (!isConnected)
                return false
            val cmd = "stream_start_${channelsToStart}"
            val resp = coapClient.post(cmd.toByteArray(), MediaTypeRegistry.TEXT_PLAIN)
            if (resp == null)
                return false
            val textResponse =  resp.responseText
            val id = textResponse.toLongOrNull() ?: 0
            return id != 0L
        } catch (ex: Exception) {
            println(ex)
            return false
        }
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

fun <T> List<T>.tryGet(index: Int, default: T): T {
    if (index < size)
        return get(index)
    else
        return default
}