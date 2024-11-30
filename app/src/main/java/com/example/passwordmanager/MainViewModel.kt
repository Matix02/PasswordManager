package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.authentication.pin.UserStatusDataStoreRepository
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.update
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
    val navigateToAddCredentialItemEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showNoAccessSnackbarEvent: LiveData<Event<Unit>> = MutableLiveData()

    init {
        showAddCredentialFab()
    }

    fun fetchSearchQueryList() {
        viewModelScope.launch {
            val queries = queryCacheRepository.getQueryCacheList()
            viewState.updateValue(SearchQueryViewState(queries))
        }
    }

    fun updateCredentialList(input: String) {
        viewModelScope.launch {
            val searchList = queryCacheRepository.fetchCommonQueries(input)
            viewState.updateValue(SearchQueryViewState(searchList))
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

    fun clearPersistenceUserAccess() {
        viewModelScope.launch {
            userStatusDataStoreRepository.clearData()
        }
    }

    fun verifyAccessPin(pinInput: String): Boolean {
        return if (pinInput == BuildConfig.ADMIN_KEY) { //TODO shared this key across the app
            viewModelScope.launch { userStatusDataStoreRepository.setAdminStatus() }
            true
        } else {
            false
        }
    }

    fun showAddCredentialFab() {
        viewState.update {
            it.copy(isFabVisible = true)
        }
    }
}

data class SearchQueryViewState(
    val queryList: List<QueryData> = emptyList(),
    val isFabVisible: Boolean = false
)