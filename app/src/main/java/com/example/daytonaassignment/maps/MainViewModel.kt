package com.example.daytonaassignment.maps

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daytonaassignment.data.database.RecentSearch
import com.example.daytonaassignment.data.database.UserPlaces
import com.example.daytonaassignment.data.remote.sources.SearchDataModel
import com.example.daytonaassignment.data.repository.DataRepository
import com.example.daytonaassignment.maps.di.RecentSearchListItem
import com.google.android.gms.maps.model.LatLng
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val dataRepository: DataRepository) : ViewModel() {

    var placesLiveData : MutableLiveData<SearchDataModel> = MutableLiveData()

    var errorLiveData : MutableLiveData<Throwable> = MutableLiveData()
    var isLoading : MutableLiveData<Boolean> = MutableLiveData()
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    private var latLng : String = ""
    private var currentPosition : LatLng? = null

    fun getNearbyPlaces(searchData: String) {
        isLoading.value = true
        Log.d("Http latlng = ", ""+latLng)
        dataRepository.getNearbyPlaceData(latLng, "5000", searchData)
             .subscribeOn(Schedulers.io())
             .subscribe({
                 isLoading.postValue(false)
                 placesLiveData.postValue(it)

                 insertItem(searchData)
                 },
                 {
                     errorLiveData.postValue(it)
                     isLoading.postValue(false)
                 }).let {
                compositeDisposable.add(it)
            }
     }

    fun insertItem(searchData: String) {
        dataRepository.insertOrUpdate(RecentSearch(name = searchData))
    }

    fun getAllRecentSearchedData() : List<RecentSearch>{
        return dataRepository.getAllRecentSearchPlacesData()
    }

    fun setGetLatLon(latLong: LatLng){
        currentPosition = latLong
        latLng = latLong.latitude.toString()+","+latLong.longitude.toString()
    }

    fun getLanlng() : LatLng?{
        return currentPosition
    }

    override fun onCleared() {
        super.onCleared()
        if (compositeDisposable != null){
            compositeDisposable.clear()
        }
    }

    fun insertUserPlaceItem(
        recentSearchListItem: RecentSearchListItem,
        distance: Int
    ){

        dataRepository.insertOrUpdateUserPlaces(
            UserPlaces(
                name = recentSearchListItem.textname,
                address = recentSearchListItem.textAddress,
                isFav = recentSearchListItem.isFav,
                rating = recentSearchListItem.rating,
                currentlyOpen = recentSearchListItem.currentlyOpen,
                lattitude = recentSearchListItem.latitude,
                longitude = recentSearchListItem.longitude,
                distance = distance
        ))
    }

    fun getAllUserPlaceData() : List<UserPlaces>{
        return dataRepository.getAllUserPlacesData()
    }

    fun getDistance(latitude: Double, longitude: Double): Int{
        val startPoint = Location("locationA")
        startPoint.setLatitude(currentPosition?.latitude?: 0.0)
        startPoint.setLongitude(currentPosition?.longitude?: 0.0)

        val endPoint = Location("locationB")
        endPoint.setLatitude(latitude)
        endPoint.setLongitude(longitude)

        return startPoint.distanceTo(endPoint).toInt()
    }
}