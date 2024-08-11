package com.embedded.grapher.services.devicemanager

import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.components.NodeMcuFileStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class FakeDeviceManager @Inject constructor(): DeviceManager {

    override fun getFiles():List<NodeMcuFileInfo>{
        return (0..10).map{
            NodeMcuFileInfo("id$it","FileName $it", (if(it % 2 == 0)  NodeMcuFileStatus.RUNNING else NodeMcuFileStatus.CLOSED))
        }.toList()
    }
}