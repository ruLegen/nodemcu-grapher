package com.embedded.grapher.screens.debug

import android.util.Log
import androidx.lifecycle.ViewModel
import com.embedded.grapher.services.devicemanager.DeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebugScreenViewModel @Inject constructor(val dm: DeviceManager): ViewModel() {

}