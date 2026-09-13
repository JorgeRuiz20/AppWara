package com.example.wara

import android.app.Application
import com.example.wara.di.AppContainer

class WaraApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
