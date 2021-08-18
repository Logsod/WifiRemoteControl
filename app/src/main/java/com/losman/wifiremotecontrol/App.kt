package com.losman.wifiremotecontrol

import android.app.Application
import com.losman.wifiremotecontrol.di.AppComponent
import com.losman.wifiremotecontrol.di.DaggerAppComponent
import com.losman.wifiremotecontrol.di.MainActivityAppModule

class App : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        appComponent =
            DaggerAppComponent.builder().mainActivityAppModule(MainActivityAppModule(this)).build()
        super.onCreate()
    }
}