package com.embedded.grapher.screens.debug

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.embedded.grapher.NavDestinations

@Composable
fun DebugScreen(modifier: Modifier, navController: NavHostController, vm: DebugScreenViewModel = hiltViewModel()) {
    Row (Modifier.padding(10.dp,0.dp)) {
        Button(    onClick = {
            navController.navigate(NavDestinations.ROOT){
                this.launchSingleTop= true
            }
        }) {
            Text("Back")
        }
    }
}