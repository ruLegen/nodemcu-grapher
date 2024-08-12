package com.embedded.grapher

import androidx.navigation.NavController

class GrapherNavigationActions(private val navController: NavController) {

    fun navigateToPlot(fileId: String) {
        navController.navigate(
            NavDestinations.PLOT.replace(
                "{${NavDestinations.PlotScreenArgs.FILE_NAME}}",
                fileId
            )
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToMainPage() {
        navController.popBackStack(NavDestinations.CONNECTION, true)
        navController.navigate(NavDestinations.ROOT)
    }
}