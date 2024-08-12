package com.embedded.grapher.screens.plot

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer

@Composable
fun PlotScreen(
    nodeMcuFileName: String,
    vm: PlotScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(true) {
        vm.loadSamples(nodeMcuFileName)
    }
    Plot(vm.chartSeriesProduced,vm.lineProvider)
}

@Composable
private fun Plot(modelProducer: CartesianChartModelProducer,lineProvider: LineCartesianLayer.LineProvider) {

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(lineProvider),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        ),
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )
}