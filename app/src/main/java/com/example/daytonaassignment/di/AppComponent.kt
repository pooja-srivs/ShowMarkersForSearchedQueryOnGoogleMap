package com.example.daytonaassignment.di

import android.app.Application
import com.example.daytonaassignment.MyApp
import com.example.daytonaassignment.di.modules.ActivityResolver
import com.example.daytonaassignment.di.modules.DataSourceResolver
import com.example.daytonaassignment.di.modules.DatabaseResolver
import com.example.daytonaassignment.di.modules.NetworkResolver
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AndroidSupportInjectionModule::class,
    ActivityResolver::class,
    DataSourceResolver::class,
    NetworkResolver::class,
    DatabaseResolver::class))
interface AppComponent : AndroidInjector<MyApp>{

    override fun inject(myApp: MyApp)

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(myApp: MyApp): Builder

        fun build(): AppComponent
    }

}