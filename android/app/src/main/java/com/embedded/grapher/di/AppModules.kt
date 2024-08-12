package com.embedded.grapher.di

import com.embedded.grapher.services.devicemanager.DeviceManager
import com.embedded.grapher.services.devicemanager.FakeDeviceManager
import com.embedded.grapher.services.devicemanager.NodeMcuDeviceManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModules {

    @Binds
    @Singleton
    abstract fun bindsDeviceManager(dm: NodeMcuDeviceManager) : DeviceManager
}