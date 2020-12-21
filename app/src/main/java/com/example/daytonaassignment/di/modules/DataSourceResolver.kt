package com.example.daytonaassignment.di.modules

import com.example.daytonaassignment.data.database.QueryDao
import com.mingle.chatapp.data.remote.config.ApiManager
import com.example.daytonaassignment.data.remote.sources.DataSource
import com.example.daytonaassignment.data.repository.DataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataSourceResolver {

    @Singleton
    @Provides
    fun providesMovieSource(apiManager: ApiManager) : DataSource = DataSource(apiManager.dataService)

    @Singleton
    @Provides
    fun providesMovieDataRepository(movieSource: DataSource, apiKey : String, dao: QueryDao) : DataRepository = DataRepository(movieSource, apiKey, dao)

}