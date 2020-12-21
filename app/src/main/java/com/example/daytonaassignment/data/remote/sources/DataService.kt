package com.example.daytonaassignment.data.remote.sources

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface DataService {

    @GET("json?")
    fun getSearchData(@Query("location") method: String,
                      @Query("radius") apiKey: String,
                      @Query("types") format: String,
                      @Query("key") keyword: String): Single<SearchDataModel>
}