package com.embedded.grapher.screens.plot

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.embedded.grapher.R
import com.embedded.grapher.components.LineChartMarker
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.Utils

data class PlotRanges(
    val minX: Float = 0.0f,
    val minY: Float = 0.0f,
    val maxX: Float = 0.0f,
    val maxY: Float = 0.0f
) {
    fun isZero(): Boolean {
        return !(minX != 0.0f || minY != 0.0f || maxX != 0.0f || maxY != 0.0f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotScreen(
    nodeMcuFileName: String,
    vm: PlotScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val plotRanges by vm.plotRanges.collectAsState()
    val lineData by vm.lineData.collectAsState()
    LaunchedEffect(true) {
        vm.loadSamples(nodeMcuFileName)
    }
    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Om",
                fontSize = 8.sp,
                modifier = Modifier.padding(0.dp),
                textAlign = TextAlign.Center
            )
            Plot(lineData, plotRanges)
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                "Temperature",
                fontSize = 8.sp,
                modifier = Modifier.padding(0.dp),
                textAlign = TextAlign.Center
            )
        }

        Text("min x ${plotRanges.minX}")
        Slider(value = plotRanges.minX.toFloat(), valueRange = -5f..5f, onValueChange = {
            vm.updateRanges(plotRanges.copy(minX = it))
        })

        Text("min y ${plotRanges.minY}")
        Slider(value = plotRanges.minY.toFloat(), valueRange = -5f..5f, onValueChange = {
            vm.updateRanges(plotRanges.copy(minY = it))
        })
        Text("max x ${plotRanges.maxX}")
        Slider(value = plotRanges.maxX.toFloat(), valueRange = -5f..5f, onValueChange = {
            vm.updateRanges(plotRanges.copy(maxX = it))
        })
        Text("max y ${plotRanges.maxY}")
        Slider(value = plotRanges.maxY.toFloat(), valueRange = -5f..5f, onValueChange = {
            vm.updateRanges(plotRanges.copy(maxY = it))
        })


    }
}

@Composable
private fun Plot(lineData: LineData, plotRanges: PlotRanges) {
    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.5f),
        factory = { context ->
            LineChart(context).apply {
                setBackgroundColor(Color.WHITE)
                setTouchEnabled(true)
                isDragEnabled = true
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                setDrawMarkers(true)
                marker = LineChartMarker(context)
                isScaleXEnabled = true
                isScaleYEnabled = true
                description.isEnabled = false
            }
        }) { chart ->
        if (!plotRanges.isZero()) {
//            chart.xAxis.axisMinimum = plotRanges.minX
//            chart.xAxis.axisMaximum = plotRanges.maxX
//            chart.axisLeft.axisMinimum = plotRanges.minY
//            chart.axisLeft.axisMaximum = plotRanges.maxX
        }
        chart.data = lineData
        chart.invalidate()
    }
}

