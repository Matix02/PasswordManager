package com.example.passwordmanager.webDetailsList.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.passwordmanager.webDetailsList.model.QueryData

@Dao
interface QueryCacheDataDao {

    @Query("SELECT * FROM QueryData ORDER BY id DESC")
    suspend fun getQueries(): List<QueryData>

    @Insert
    suspend fun saveQuery(query: QueryData)

    @Delete
    suspend fun deleteQuery(query: QueryData)

    @Query("SELECT * FROM QueryData WHERE `query` = :queryText")
    suspend fun findQueryBy(queryText: String): QueryData
}