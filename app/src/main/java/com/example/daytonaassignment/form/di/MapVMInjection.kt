package com.example.daytonaassignment.form.di

import androidx.lifecycle.ViewModelProviders
import com.example.daytonaassignment.data.repository.DataRepository
import com.example.daytonaassignment.form.FormActivity
import com.example.daytonaassignment.form.MapFragment
import com.example.daytonaassignment.form.MapVM
import com.example.daytonaassignment.mapmyindia.MapinIndiaActivity
import com.example.daytonaassignment.mapmyindia.MapinIndiaVM
import com.mingle.chatapp.movie.vmfactory.MapModelFactory
import dagger.Module
import dagger.Provides

@Module
class MapVMInjection {

    @Provides
    fun providesMapViewModelFactory(movieDataRepository: DataRepository) = MapModelFactory(movieDataRepository)

    @Provides
    fun providesMapViewModel(factory: MapModelFactory, activity: FormActivity) : MapVM = ViewModelProviders
        .of(activity, factory)
        .get(MapVM::class.java)

  /*  @Provides
    fun provideVM(factory: MapModelFactory, activity: MainActivity): MainViewModel = ViewModelProvider(activity, factory).get(MainViewModel::class.java)
*/
}