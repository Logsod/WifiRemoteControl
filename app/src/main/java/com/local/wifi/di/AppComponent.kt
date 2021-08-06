package com.local.wifi.di

import com.local.wifi.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainActivityAppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}