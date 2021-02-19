package com.example.daytonaassignment.mapmyindia

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daytonaassignment.data.remote.sources.SearchDataModel
import com.example.daytonaassignment.data.repository.DataRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MapinIndiaVM(private val dataRepository: DataRepository) : ViewModel() {

    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    var placesLiveData : MutableLiveData<MapinIndiaModel> = MutableLiveData()

    var errorLiveData : MutableLiveData<Throwable> = MutableLiveData()
    var isLoading : MutableLiveData<Boolean> = MutableLiveData()

    fun getNearbyPlaces(searchData: String) {
        isLoading.value = true
        dataRepository.getNearbyPlaceDataMapinIndia(searchData)
            .subscribeOn(Schedulers.io())
            .subscribe({
                isLoading.postValue(false)
                placesLiveData.postValue(it)

                Log.d("*** RESPONSE = ", ""+it)

            },
                {
                    errorLiveData.postValue(it)
                    isLoading.postValue(false)
                }).let {
                compositeDisposable.add(it)
            }
    }
}