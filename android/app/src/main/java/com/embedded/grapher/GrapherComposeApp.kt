package com.embedded.grapher

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.embedded.grapher.ui.theme.GrapherTheme

@Composable
fun GrapherApp(modifier: Modifier = Modifier) {
    GrapherTheme() {
      GrapherNavGraph(modifier = modifier)
    }
}

