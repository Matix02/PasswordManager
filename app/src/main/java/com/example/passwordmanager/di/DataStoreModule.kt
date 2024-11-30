package com.example.passwordmanager.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.example.passwordmanager.MyApplication
import com.example.passwordmanager.UserStatus
import com.example.passwordmanager.authentication.pin.UserStatusProtoFileSerializer
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val DATA_STORE_FILE_NAME = "AdminPermission.pb"

@Module
class DataStoreModule {

    @Singleton
    @Provides
    fun provideUserStatusDataStore(application: MyApplication): DataStore<UserStatus> {
        return DataStoreFactory.create(
            serializer = UserStatusProtoFileSerializer,
            produceFile = { application.applicationContext.dataStoreFile(DATA_STORE_FILE_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler {
                UserStatus.getDefaultInstance()
                //TODO Crashlytics etc.
            },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

}