package com.embedded.grapher.screens.plot

import androidx.lifecycle.ViewModel
import com.embedded.grapher.services.devicemanager.DeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlotScreenViewModel @Inject constructor(val dm: DeviceManager) : ViewModel() {

}