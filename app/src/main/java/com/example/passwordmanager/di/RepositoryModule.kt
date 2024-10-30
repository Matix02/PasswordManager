package com.example.passwordmanager.di

import com.example.passwordmanager.authentication.pin.UserDataStoreRepository
import com.example.passwordmanager.authentication.pin.UserStatusDataStoreRepository
import com.example.passwordmanager.webDetailsList.data.QueryCacheDataRepository
import com.example.passwordmanager.webDetailsList.data.QueryDataRepository
import com.example.passwordmanager.webDetailsList.data.WebCredentialsDetailsRepository
import com.example.passwordmanager.webDetailsList.data.WebDetailsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideQueryCacheRepository(queryDataRepository: QueryDataRepository): QueryCacheDataRepository {
        return queryDataRepository
    }

    @Singleton
    @Provides
    fun provideWebDetailsListRepository(webDetailsRepository: WebDetailsRepository): WebCredentialsDetailsRepository {
        return webDetailsRepository
    }

    @Singleton
    @Provides
    fun provideUserDataStoreRepository(userDataStoreRepository: UserDataStoreRepository): UserStatusDataStoreRepository {
        return userDataStoreRepository
    }

}