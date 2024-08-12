package com.embedded.grapher.screens.plot

import android.graphics.Paint
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.embedded.grapher.components.NodeMcuFileInfo
import com.embedded.grapher.services.devicemanager.DeviceManager
import com.embedded.grapher.utils.Async
import com.embedded.grapher.utils.NodeMcuSample
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext


@HiltViewModel
class PlotScreenViewModel @Inject constructor(val dm: DeviceManager) : ViewModel() {

    var lineData = MutableStateFlow(LineData())
    var plotRanges = MutableStateFlow(PlotRanges())

    private val scope = CoroutineScope(EmptyCoroutineContext)
    private val _files: MutableStateFlow<Async<List<NodeMcuSample>>> =
        MutableStateFlow(
            Async.Success(
                emptyList()
            )
        )

    fun updateRanges(ranges: PlotRanges) {
        plotRanges.tryEmit(ranges)
    }
    var i =0
    fun loadSamples(nodeMcuFileName: String) {
        scope.launch {
            i++
            val samples = dm.getFileSamples(nodeMcuFileName)
            if (samples == null)
                return@launch

            val groupByChannel = samples.groupBy { it.channel }
            val lineSeries = groupByChannel.map { (k, v) ->
              val entries = v.map { Entry(it.time,it.value) }
              val data = LineDataSet(entries,"Channel $k").apply {
                  color = getColorByChannel(k)
                  setDrawCircleHole(false)
                  lineWidth = 3f
                  fillAlpha = 115
                  isHighlightEnabled = true
//                  enableDashedLine(10f, 5f, 0f);
              }
              return@map data
            }.toList()
            val data = LineData(lineSeries)
            lineData.tryEmit(data)
        }
    }

    private fun getColorByChannel(k: Int): Int {
        val colors = arrayOf(android.graphics.Color.GRAY,android.graphics.Color.RED,android.graphics.Color.GREEN,android.graphics.Color.BLUE)
        var index = k % colors.size
        if (index < 0 || index > colors.size - 1)
            index = 0
        return colors[index]
    }
}