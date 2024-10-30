package com.example.passwordmanager.authentication.pin

import androidx.datastore.core.DataStore
import com.example.passwordmanager.UserStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface UserStatusDataStoreRepository {
    suspend fun updateStatus()
    suspend fun isAdmin(): Boolean
    suspend fun clearData()
}

class UserDataStoreRepository @Inject constructor(
    private val dataStore: DataStore<UserStatus>
) : UserStatusDataStoreRepository {

    override suspend fun updateStatus() {
        dataStore.updateData {
            it.toBuilder().setIsPermissionGranted(true).build()
        }
    }

    override suspend fun isAdmin(): Boolean {
        return dataStore.data.first().isPermissionGranted
    }

    override suspend fun clearData() {
        dataStore.updateData { it.toBuilder().clear().build() }
    }

}