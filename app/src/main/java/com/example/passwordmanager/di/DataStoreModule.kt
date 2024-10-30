package com.example.passwordmanager.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.example.passwordmanager.MyApplication
import com.example.passwordmanager.UserStatus
import com.example.passwordmanager.authentication.pin.UserStatusSerializer
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
class DataStoreModule {

    @Singleton
    @Provides
    fun provideUserDataStore(application: MyApplication): DataStore<UserStatus> {
        return DataStoreFactory.create(
            serializer = UserStatusSerializer,
            produceFile = { application.applicationContext.dataStoreFile("AdminPermission.pb") },
            corruptionHandler = ReplaceFileCorruptionHandler {
                UserStatus.getDefaultInstance()
                //TODO Crashlytics etc.
            },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

}