package com.it.ya_hackathon.presentation.main

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.it.ya_hackathon.data.room.roomDbModule
import com.it.ya_hackathon.di.receiptSplitterModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(context = this)

        startKoin {
            androidContext(this@App)
            modules( receiptSplitterModule, roomDbModule)
        }
    }

}