package com.embedded.grapher.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

object EmptyPopupPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return IntOffset.Zero
    }
}

data class RecordStartPopupState(
    val channel1: Boolean = false,
    val channel2: Boolean = false,
    val channel3: Boolean = false,
    val channel4: Boolean = false
) {
    val allDisabled :Boolean
        get(){
           return !channel1 && !channel2 && !channel3 && channel4
        }
}

@Composable
fun RecordStartPopup(
    isOpened: Boolean,
    onDismiss: () -> Unit = {},
    onStartRecord: (channels: RecordStartPopupState) -> Unit = {}
) {
    if (!isOpened)
        return
    var channels by remember { mutableStateOf(RecordStartPopupState(false, false, false, false)) }

    Popup(popupPositionProvider = EmptyPopupPositionProvider) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SolidColor(Color.DarkGray), RectangleShape, .8f)
                .clickable(interactionSource = null, indication = null, onClick = { onDismiss() }),
            contentAlignment = Alignment.Center
        )
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .fillMaxWidth(0.8f)
                        .clickable(interactionSource = null, indication = null, onClick = {})
                        .padding(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                            .background(Color.White)
                    ) {
                        Text(text = "Выберите каналы", textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.SpaceAround) {
                            CheckBoxItem(
                                "1",
                                checked = channels.channel1,
                                onCheckedChange = {
                                    channels = channels.copy(channel1 = !channels.channel1)
                                })
                            CheckBoxItem(
                                "2",
                                checked = channels.channel2,
                                onCheckedChange = {
                                    channels = channels.copy(channel2 = !channels.channel2)
                                })
                            CheckBoxItem(
                                "3",
                                checked = channels.channel3,
                                onCheckedChange = {
                                    channels = channels.copy(channel3 = !channels.channel3)
                                })
                            CheckBoxItem(
                                "4",
                                checked = channels.channel4,
                                onCheckedChange = {
                                    channels = channels.copy(channel4 = !channels.channel4)
                                })
                        }
                        Spacer(Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                onStartRecord(channels)
                            }) {
                                Text("Start")
                            }
                            Button(onClick = { onDismiss() }) {
                                Text("Cancell")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckBoxItem(caption: String, checked: Boolean, onCheckedChange: (b: Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = caption, textAlign = TextAlign.Center)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}