package com.example.daytonaassignment.data.repository

import com.example.daytonaassignment.data.database.RecentSearch
import com.example.daytonaassignment.data.database.QueryDao
import com.example.daytonaassignment.data.database.UserPlaces
import com.example.daytonaassignment.data.remote.sources.DataSource
import com.example.daytonaassignment.data.remote.sources.SearchDataModel
import com.example.daytonaassignment.mapmyindia.MapinIndiaModel
import io.reactivex.Single

class DataRepository(
    private val dataSource: DataSource,
    private val apiKey: String,
    private val dao: QueryDao
) {
    fun getNearbyPlaceData(location : String, radius : String, type : String) : Single<SearchDataModel> {
        return dataSource.getSearchData(location, radius, type, apiKey)
    }

    fun getNearbyPlaceDataMapinIndia(location : String) : Single<MapinIndiaModel> {
        return dataSource.getSearchDataMapinIndia(location)
    }

    fun getAllRecentSearchPlacesData() : List<RecentSearch> {
        return dao.getRecentPlaces()
    }

    fun getAllUserPlacesData() : List<UserPlaces> {
        return dao.getUserData()
    }

    fun insertOrUpdateUserPlaces(userPlaces: UserPlaces) {
        val itemFromDB: UserPlaces
        itemFromDB = dao.getUserPlaceItemById(userPlaces.name)

        if (itemFromDB == null){
            dao.insertUserPlace(userPlaces)
        }else{
            dao.updateUserPlaceItem(userPlaces.name)
        }
    }

    fun insertOrUpdate(recentSearch: RecentSearch) {
        val itemFromDB: RecentSearch
        itemFromDB = dao.getItemById(recentSearch.name)

        if (itemFromDB == null){
            dao.insertRecentPlace(recentSearch)
        }else{
            dao.updateItem(recentSearch.name)
        }
    }


}