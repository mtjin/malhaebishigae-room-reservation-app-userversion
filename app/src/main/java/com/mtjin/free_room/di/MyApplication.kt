package com.mtjin.free_room.di

import android.app.Application
import com.mtjin.free_room.di.AppComponent

class MyApplication : Application() {
    val appComponent: AppComponent by lazy {
        //initializeComponent()
        DaggerAppComponent.factory().create(this)
    }
}