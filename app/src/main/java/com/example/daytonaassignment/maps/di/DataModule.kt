package com.example.daytonaassignment.maps.di

import androidx.lifecycle.ViewModelProviders
import com.example.daytonaassignment.maps.MainActivity
import com.example.daytonaassignment.maps.MainViewModel
import com.example.daytonaassignment.data.repository.DataRepository
import com.mingle.chatapp.movie.vmfactory.MainViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun providesViewModelFactory(movieDataRepository: DataRepository) = MainViewModelFactory(movieDataRepository)

    @Provides
    fun providesMainViewModel(factory: MainViewModelFactory, activity: MainActivity) : MainViewModel = ViewModelProviders
        .of(activity, factory)
        .get(MainViewModel::class.java)

  /*  @Provides
    fun provideVM(factory: MainViewModelFactory, activity: MainActivity): MainViewModel = ViewModelProvider(activity, factory).get(MainViewModel::class.java)
*/
}