package com.example.daytonaassignment.form

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daytonaassignment.data.remote.sources.SearchDataModel
import com.example.daytonaassignment.data.repository.DataRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MapVM(private val dataRepository: DataRepository) : ViewModel() {

    var placesLiveData : MutableLiveData<String> = MutableLiveData()


}