package com.embedded.grapher

object NavDestinations {
    object PlotScreenArgs{
        const val FILE_NAME = "fileName"
    }


    const val PLOT = "plot/{${PlotScreenArgs.FILE_NAME}}"
    const val ROOT = "/"
    const val DEBUG_SCREEN = "debug"
}