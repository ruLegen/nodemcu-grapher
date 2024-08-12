package com.embedded.grapher.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

enum class NodeMcuFileStatus(val status: Int = -1) {
    UNKNOWN(-1),
    CLOSED(0),
    RUNNING(1);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull() { it.status == value } ?: UNKNOWN
    }
}

data class NodeMcuFileInfo(
    val id: String,
    val name: String,
    val size: Int,
    var status: NodeMcuFileStatus
){
    fun key():String{
        return "$id$name$size$status"
    }
}

@Composable
fun NodeMcuFIleInfoItem(
    id: String,
    name: String,
    status: NodeMcuFileStatus,
    modifier: Modifier = Modifier,
    onStop: (id: String) -> Unit = {},
    onDelete: (id: String) -> Unit = {},
    onClick: (id: String) -> Unit = {}
) {
    Row(modifier = modifier.clickable { onClick(id)}) {
        Text(name, textAlign = TextAlign.Start)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            val isDelete = status == NodeMcuFileStatus.UNKNOWN || status == NodeMcuFileStatus.CLOSED
            val callback = if (isDelete) onDelete else onStop
            val text = if (isDelete) "Delete" else "Stop"

            Button(onClick = { callback(id) } ) {
                Text(text = text)
            }
        }
    }
}