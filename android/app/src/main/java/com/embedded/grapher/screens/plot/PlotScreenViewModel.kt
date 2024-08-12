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
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.getDefaultAreaFill
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.VerticalPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
class PlotScreenViewModel @Inject constructor(val dm: DeviceManager) : ViewModel() {
    val lineProvider
        get() = LineCartesianLayer.LineProvider.series(lineSeries)
    val chartSeriesProduced = CartesianChartModelProducer()
    private var lineSeries: List<LineCartesianLayer.Line> = listOf(getDefaultLineStyle(LineCartesianLayer.LineFill.single(fill(getColorByChannel(0)))))

    private val scope = CoroutineScope(EmptyCoroutineContext)
    private val _files: MutableStateFlow<Async<List<NodeMcuSample>>> =
        MutableStateFlow(
            Async.Success(
                emptyList()
            )
        )

    fun loadSamples(nodeMcuFileName: String) {
        scope.launch {
            val samples = dm.getFileSamples(nodeMcuFileName)
            if (samples == null)
                return@launch

            val groupByChannel = samples.groupBy { it.channel }

            lineSeries = groupByChannel.map { (k, v) ->
                val fill = LineCartesianLayer.LineFill.single(fill(getColorByChannel(k)))
                return@map getDefaultLineStyle(fill)
            }.toList()

            chartSeriesProduced.runTransaction {
                lineSeries {
                    for ((key, value) in groupByChannel) {
                        val x = value.map { sample -> sample.time }
                        val y = value.map { sample -> sample.value }
                        series(x, y)
                    }
                }
            }
        }
    }

    private fun getDefaultLineStyle(fill: LineCartesianLayer.LineFill): LineCartesianLayer.Line {
        return LineCartesianLayer.Line(
            fill,
            2f.dp.value,
            null,
            Paint.Cap.ROUND,
            null,
            LineCartesianLayer.PointConnector.cubic(),
            null,
            VerticalPosition.Top,
            CartesianValueFormatter.decimal(),
            0f,
        )
    }

    private fun getColorByChannel(k: Int): Color{
        var index = k;        // channels starts from 1
        val colors = arrayOf(Color.Gray, Color.Red, Color.Gray, Color.Blue, Color.Yellow)
        if(index < 0 || index > colors.size-1)
            index =0
        return  colors[index]
    }
}