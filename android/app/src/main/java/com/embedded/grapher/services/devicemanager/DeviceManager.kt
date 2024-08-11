package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo

interface DeviceManager {
    fun getFiles():List<NodeMcuFileInfo>
}