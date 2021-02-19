package com.example.daytonaassignment.mapmyindia.di

import androidx.lifecycle.ViewModelProviders
import com.example.daytonaassignment.maps.MainActivity
import com.example.daytonaassignment.maps.MainViewModel
import com.example.daytonaassignment.data.repository.DataRepository
import com.example.daytonaassignment.mapmyindia.MapinIndiaActivity
import com.example.daytonaassignment.mapmyindia.MapinIndiaVM
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
    fun provideVM(factory: MapinIndiaModelFactory, activity: MainActivity): MainViewModel = ViewModelProvider(activity, factory).get(MainViewModel::class.java)
*/
}