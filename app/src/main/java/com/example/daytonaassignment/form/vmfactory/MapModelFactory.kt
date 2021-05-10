package com.mingle.chatapp.movie.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.daytonaassignment.data.repository.DataRepository
import com.example.daytonaassignment.form.MapVM
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapModelFactory @Inject constructor(private val movieDataRepository : DataRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapVM(movieDataRepository) as T
    }
}