package com.example.passwordmanager.webDetailsList.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.passwordmanager.webDetailsList.model.QueryData

@Dao
interface QueryCacheDataDao { //TODO <-- start reformating from here

    @Query("SELECT * FROM QueryData ORDER BY id DESC")
    suspend fun getQueries(): List<QueryData>

    @Query("SELECT * FROM QueryData WHERE `query` = :queryText")
    suspend fun fetchCommonQueries(queryText: String): List<QueryData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuery(query: QueryData)

    @Delete
    suspend fun deleteQuery(query: QueryData)

    @Query("SELECT * FROM QueryData WHERE `query` = :queryText")
    suspend fun findQueryBy(queryText: String): QueryData?
}