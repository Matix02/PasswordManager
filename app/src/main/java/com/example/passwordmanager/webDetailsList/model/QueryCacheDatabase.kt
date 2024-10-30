package com.example.passwordmanager.webDetailsList.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.passwordmanager.webDetailsList.data.QueryCacheDataDao

@Database(entities = [QueryData::class], version = 1, exportSchema = false)
abstract class QueryCacheDatabase : RoomDatabase() {

    abstract fun queryCacheDao(): QueryCacheDataDao
}