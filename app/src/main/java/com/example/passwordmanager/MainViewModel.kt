package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.authentication.pin.UserStatusDataStoreRepository
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.updateValue
import com.example.passwordmanager.webDetailsList.data.QueryCacheDataRepository
import com.example.passwordmanager.webDetailsList.model.QueryData
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val queryCacheRepository: QueryCacheDataRepository,
    private val userStatusDataStoreRepository: UserStatusDataStoreRepository
) : ViewModel() {

    val viewState: LiveData<SearchQueryViewState> = MutableLiveData(SearchQueryViewState())
    val selectQuery: LiveData<Event<String>> = MutableLiveData()
    val navigateToAddCredentialItemEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showNoAccessSnackbarEvent: LiveData<Event<Unit>> = MutableLiveData()

    fun loadData() {
        viewModelScope.launch {
            val queries = queryCacheRepository.getQueryCacheList()
            queries?.let { viewState.updateValue(SearchQueryViewState(queries)) }
        }
    }

    fun tryToNavigateToAddCredentialItem() {
        viewModelScope.launch {
            if (userStatusDataStoreRepository.isAdmin()) {
                navigateToAddCredentialItemEvent.updateValue(Event(Unit))
            } else {
                showNoAccessSnackbarEvent.updateValue(Event(Unit))
            }
        }
    }

    fun clearUserStatusData() {
        viewModelScope.launch {
            userStatusDataStoreRepository.clearData()
        }
    }

    fun checkPinForDialog(input: String): Boolean {
        return if (input == BuildConfig.ADMIN_KEY) { //TODO shared this key across the app
            viewModelScope.launch { userStatusDataStoreRepository.updateStatus() }
            true
        } else {
            false
        }
    }

}

data class SearchQueryViewState(
    val queryList: List<QueryData> = emptyList()
)