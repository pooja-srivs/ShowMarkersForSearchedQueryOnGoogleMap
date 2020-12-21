package com.example.daytonaassignment.data.remote.sources

import io.reactivex.Single

//Movie Source will consume Movie Service
class DataSource(val dataService: DataService) {

    fun getSearchData(location : String, radius : String, type : String, key : String): Single<SearchDataModel> {
        return dataService.getSearchData(location, radius, type, key)
    }
}