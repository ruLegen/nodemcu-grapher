package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.components.NodeMcuFileStatus
import com.embedded.grapher.utils.NodeMcuSample
import com.embedded.grapher.utils.NodeMcuSampleHelper
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.net.Socket
import javax.inject.Inject

class NodeMcuDeviceManager @Inject constructor() : DeviceManager {

    private var serverHost: String = ""
    private var serverUri: String? = null
    private var client: HttpClient = HttpClient(Android)

    override suspend fun connect(host: String, port: Int): Boolean {
        serverHost = host
        serverUri = "http://$host:$port/"

        return checkConnection()
    }

    private suspend fun checkConnection(): Boolean {
        try {
            val resp = client.get(buildRequest("heartbeat"))
            return resp.status == HttpStatusCode.OK
        } catch (ex: Exception) {
            return false
        }
    }

    private fun buildRequest(destination: String): String {
        return serverUri!! + destination
    }

    override suspend fun getFiles(): List<NodeMcuFileInfo>? {
        try {
            val resp = client.get(buildRequest("files_list"))
            val text = resp.body<String>()
            if (text.isEmpty())
                return emptyList()
            val fileInfos = text.split("@").map {
                println(it)
                val fields = it.split(";")

                val name = fields.tryGet(0, "NO_NAME")
                val size = fields.tryGet(1, "0").toLong()
                val status = fields.tryGet(3, "0").toInt()

                NodeMcuFileInfo(name, name, size, NodeMcuFileStatus.fromInt(status), 0)
            }.toList()
            return fileInfos
        } catch (ex: Exception) {
            println(ex)
            return null
        }
    }

    override suspend fun getFileSamples(fileId: String): List<NodeMcuSample>? {
        try {
            val resp = client.get(buildRequest("files_read_${fileId}"))
            val text = resp.body<String>()
            if (text.isEmpty())
                return null
            val port = text.toIntOrNull() ?: 0
            if (port == 0)
                return null
            print(port)
            val client = Socket(serverHost, port)
            val bytes = client.getInputStream().readBytes()
            client.close()
            if (bytes == null)
                return null
            val samples = NodeMcuSampleHelper.decodeSamples(bytes)
            return samples
        } catch (ex: Exception) {
            println(ex)
            return null
        }
    }

    override suspend fun deleteFileSample(fileId: String): Boolean {
        try {
            val resp = client.get(buildRequest("files_remove_${fileId}"))
            return true
        } catch (ex: Exception) {
            println(ex)
            return false
        }
    }

    override suspend fun stopFileRecording(fileId: String): Boolean {
        val stringId = fileId.split(".").firstOrNull()
        if (stringId == null)
            return false
        try {
            val resp = client.get(buildRequest("stream_stop_${stringId}"))
            val textResponse = resp.body<String>()
            return textResponse == stringId
        } catch (ex: Exception) {
            println(ex)
            return false
        }
    }

    data class ChannelStatus(val num: Int, val enabled: Boolean)

    override suspend fun startRecording(
        channel1: Boolean,
        channel2: Boolean,
        channel3: Boolean,
        channel4: Boolean
    ): Boolean {
        if (!(channel1 || channel2 || channel3 || channel4))
            return false
        val channels = arrayOf(
            ChannelStatus(1, channel1),
            ChannelStatus(2, channel2),
            ChannelStatus(3, channel3),
            ChannelStatus(4, channel4)
        )

        val channelsToStart = channels.filter { it.enabled }
                                      .joinToString(separator = "_") { it.num.toString() }
        try {
            val cmd = "stream_start_${channelsToStart}"
            val resp = client.get(buildRequest(cmd))
            val textResponse = resp.body<String>()
            val id = textResponse.toLongOrNull() ?: 0
            return id != 0L
        } catch (ex: Exception) {
            println(ex)
            return false
        }
    }
}

fun <T> List<T>.tryGet(index: Int, default: T): T {
    if (index < size)
        return get(index)
    else
        return default
}