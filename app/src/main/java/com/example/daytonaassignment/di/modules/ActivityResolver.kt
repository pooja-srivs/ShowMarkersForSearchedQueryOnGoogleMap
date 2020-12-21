package com.example.daytonaassignment.di.modules

import com.example.daytonaassignment.maps.MainActivity
import com.example.daytonaassignment.maps.di.DataModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityResolver {

    @ContributesAndroidInjector(modules = arrayOf(DataModule::class))
    abstract fun providesMainActivity() : MainActivity
}