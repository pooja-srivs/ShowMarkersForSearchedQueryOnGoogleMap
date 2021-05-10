package com.example.daytonaassignment.mapBuilder.di

import androidx.lifecycle.ViewModelProviders
import com.example.daytonaassignment.data.repository.DataRepository
import com.example.daytonaassignment.mapBuilder.MapinIndiaActivity
import com.example.daytonaassignment.mapBuilder.MapinIndiaVM
import com.mingle.chatapp.movie.vmfactory.MapinIndiaModelFactory
import dagger.Module
import dagger.Provides

@Module
class VMInjection {

    @Provides
    fun providesMapmyIndiaViewModelFactory(movieDataRepository: DataRepository) = MapinIndiaModelFactory(movieDataRepository)

    @Provides
    fun providesMapmyIndiaViewModel(factory: MapinIndiaModelFactory, activity: MapinIndiaActivity) : MapinIndiaVM = ViewModelProviders
        .of(activity, factory)
        .get(MapinIndiaVM::class.java)

  /*  @Provides
    fun provideVM(factory: MapModelFactory, activity: MainActivity): MainViewModel = ViewModelProvider(activity, factory).get(MainViewModel::class.java)
*/
}