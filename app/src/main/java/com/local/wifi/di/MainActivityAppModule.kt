package com.local.wifi.di

import android.app.Application
import android.content.Context
import com.local.wifi.presenter.MainPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainActivityAppModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideApplicationContext() : Context {
      return application
    }

    @Provides
    @Singleton
    fun provideWordsPresenter(): MainPresenter {
        return MainPresenter()
    }
}
