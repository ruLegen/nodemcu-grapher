package com.embedded.grapher

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.embedded.grapher.NavDestinations
import com.embedded.grapher.screens.debug.DebugScreen
import com.embedded.grapher.screens.main.MainScreen
import com.embedded.grapher.screens.plot.PlotScreen
import kotlinx.coroutines.CoroutineScope
@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = fadeIn(tween(1000)),
        exit = fadeOut(tween(1000))
    ) {
        content()
    }
}
@Composable
fun GrapherNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = NavDestinations.ROOT,
    navActions: GrapherNavigationActions = remember(navController) {
        GrapherNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {EnterTransition.None},
        exitTransition = {ExitTransition.None},
        popEnterTransition = {EnterTransition.None},
        popExitTransition = {ExitTransition.None},
    ) {
        composable(NavDestinations.ROOT){
            EnterAnimation{
                MainScreen(modifier, onNavigateToPlot = {
                    navActions.navigateToPlot(it)
                })
            }
        }
        composable(NavDestinations.DEBUG_SCREEN) {
            DebugScreen(modifier, navController)
        }
        composable(
            NavDestinations.PLOT,
            arguments = listOf(navArgument(NavDestinations.PlotScreenArgs.FILE_NAME) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { params ->
            val nodemcuSampleFile = params.arguments?.getString(NavDestinations.PlotScreenArgs.FILE_NAME,"") ?: ""
            EnterAnimation{
                PlotScreen(nodemcuSampleFile,modifier = modifier)
            }
        }
    }
}
