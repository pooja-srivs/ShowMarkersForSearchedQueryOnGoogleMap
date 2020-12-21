package com.example.daytonaassignment.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface QueryDao {

    @Query("SELECT * FROM RecentSearch ")
    fun getRecentPlaces(): List<RecentSearch>

    @Insert(onConflict = REPLACE)
    fun insertRecentPlace(searchedSearchData: RecentSearch)

    @Query("SELECT * FROM RecentSearch where name= :name")
    fun getItemById(name: String) : RecentSearch

    @Query("UPDATE RecentSearch SET name= :name where name GLOB '*' || :name || '*'")
    fun updateItem(name: String)


   // ------------ User Place ---------------------


    @Query("SELECT * FROM UserPlaces ORDER BY distance, currentlyOpen")
    fun getUserData(): List<UserPlaces>

    @Query("SELECT * FROM UserPlaces where name= :name")
    fun getUserPlaceItemById(name: String) : UserPlaces

    @Insert(onConflict = REPLACE)
    fun insertUserPlace(userPlaces: UserPlaces)

    @Query("UPDATE UserPlaces SET name= :name where name GLOB '*' || :name || '*'")
    fun updateUserPlaceItem(name: String)

}