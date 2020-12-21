package com.example.daytonaassignment

import com.example.daytonaassignment.di.DaggerAppComponent
import com.example.daytonaassignment.di.modules.DatabaseResolver
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent
        .builder()
        .application(this)
        .build()
}