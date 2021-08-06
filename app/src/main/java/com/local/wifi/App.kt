package com.local.wifi

import android.app.Application
import com.local.wifi.di.AppComponent
import com.local.wifi.di.DaggerAppComponent
import com.local.wifi.di.MainActivityAppModule

class App : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        appComponent =
            DaggerAppComponent.builder().mainActivityAppModule(MainActivityAppModule(this)).build()
        super.onCreate()
    }
}