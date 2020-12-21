package com.example.daytonaassignment.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class UserPlaces(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val placeId: String = "",
    val isFav : Boolean = false,
    val timeInteracted : Long = Calendar.getInstance().timeInMillis,
    val rating : Double = 0.0,
    val currentlyOpen : Boolean = false,
    val longitude: Double = 0.0,
    val lattitude: Double = 0.0,
    val distance: Int = 0
)

@Entity
data class RecentSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = ""
)
