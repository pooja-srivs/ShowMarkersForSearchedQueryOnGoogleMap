package com.example.daytonaassignment.data.remote.sources

import com.example.daytonaassignment.mapmyindia.MapinIndiaModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface DataService {

    @GET("json?")
    fun getSearchData(@Query("location") method: String,
                      @Query("radius") apiKey: String,
                      @Query("types") format: String,
                      @Query("key") keyword: String): Single<SearchDataModel>


    @GET("json?")
    fun getSearchDataMapinIndia(@Query("query") method: String): Single<MapinIndiaModel>

}