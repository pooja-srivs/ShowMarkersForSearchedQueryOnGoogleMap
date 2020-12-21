package com.example.daytonaassignment.data.remote.sources

import com.google.gson.annotations.SerializedName

data class SearchDataModel(
    @SerializedName("results")
    val results : List<Results>,

    @SerializedName("status")
    val status : String
)

data class Results(
    @SerializedName("name")
    val name : String,
    @SerializedName("icon")
    val icon : String,
    @SerializedName("geometry")
    val geometry : Geometry,
    @SerializedName("opening_hours")
    val opening_hours : OpeningHours? = null,
    @SerializedName("rating")
    val rating : Double,
    @SerializedName("place_id")
    val place_id : String,
    @SerializedName("vicinity")
    val vicinity : String
)

data class Geometry(
    @SerializedName("location")
    val location : PlaceLocation
)
data class PlaceLocation(
    @SerializedName("lat")
    val lat : Double,
    @SerializedName("lng")
    val lng : Double
)
data class OpeningHours(
    @SerializedName("open_now")
    val open_now : Boolean
)
