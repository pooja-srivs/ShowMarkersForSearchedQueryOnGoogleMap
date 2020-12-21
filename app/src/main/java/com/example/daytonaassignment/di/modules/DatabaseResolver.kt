package com.example.daytonaassignment.di.modules

import androidx.room.Room
import com.example.daytonaassignment.MyApp
import com.example.daytonaassignment.data.database.AppDatabase
import com.example.daytonaassignment.data.database.QueryDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseResolver() {

    @Provides
    @Singleton
    fun providesAppDatabse(application: MyApp) : AppDatabase = Room.
                                                             databaseBuilder(application, AppDatabase::class.java, "my-searched-places")
                                                            .allowMainThreadQueries()
                                                            .build()


    @Provides
    @Singleton
    fun providesRoomModule(appDatabase: AppDatabase) : QueryDao = appDatabase.queryDao()

}