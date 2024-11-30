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
    fun provideSearchQueryRepository(queryDataRepository: QueryDataRepository): QueryCacheDataRepository = queryDataRepository

    @Singleton
    @Provides
    fun provideCredentialListRepository(webDetailsRepository: WebDetailsRepository): WebCredentialsDetailsRepository = webDetailsRepository

    @Singleton
    @Provides
    fun provideUserStatusDataStoreRepository(userDataStoreRepository: UserDataStoreRepository): UserStatusDataStoreRepository = userDataStoreRepository

}