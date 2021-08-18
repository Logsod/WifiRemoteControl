package com.losman.wifiremotecontrol.di

import com.losman.wifiremotecontrol.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainActivityAppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}