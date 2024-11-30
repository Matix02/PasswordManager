package com.example.passwordmanager.di

import androidx.room.Room
import com.example.passwordmanager.MyApplication
import com.example.passwordmanager.webDetailsList.data.QueryCacheDataDao
import com.example.passwordmanager.webDetailsList.model.QueryCacheDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

private const val QUERY_CACHE_DATABASE_NAME = "QueryCacheDatabase"

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideFirebase() = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideSearchQueryDataAccessObject(database: QueryCacheDatabase): QueryCacheDataDao = database.queryCacheDao()

    @Singleton
    @Provides
    fun provideSearchQueryDatabase(application: MyApplication): QueryCacheDatabase {
        return Room.databaseBuilder(application, QueryCacheDatabase::class.java, QUERY_CACHE_DATABASE_NAME).build()
    }

}