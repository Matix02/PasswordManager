package com.example.passwordmanager.webDetailsList

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.BuildConfig
import com.example.passwordmanager.authentication.pin.UserStatusDataStoreRepository
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.update
import com.example.passwordmanager.extension.updateValue
import com.example.passwordmanager.webDetailsList.data.QueryCacheDataRepository
import com.example.passwordmanager.webDetailsList.data.WebDetailsRepository
import com.example.passwordmanager.webDetailsList.model.QueryData
import com.example.passwordmanager.webDetailsList.model.WebDetails
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebDetailsListViewModel @Inject constructor(
    private val webDetailsRepository: WebDetailsRepository,
    private val queryCacheRepository: QueryCacheDataRepository,
    private val userStatusDataStoreRepository: UserStatusDataStoreRepository
) : ViewModel() {

    val queryViewState: LiveData<SearchQueryViewState> = MutableLiveData(SearchQueryViewState())
    val showFabEvent: LiveData<Event<Boolean>> = MutableLiveData()
    val hideFabEvent: LiveData<Event<Boolean>> = MutableLiveData()
    val viewState: LiveData<WebCredentialsViewState> = MutableLiveData(WebCredentialsViewState())
    val showRefreshEvent: LiveData<Event<Boolean>> = MutableLiveData()
    val navigateToWebItemEditionEvent: LiveData<Event<WebDetails>> = MutableLiveData()
    val showCopiedSnackbarEvent: LiveData<Event<Unit>> = MutableLiveData()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            viewState.updateValue(WebCredentialsViewState(isLoading = true))
            fetchData()
        }
    }

    fun refreshData(searchQuery: String) {
        viewModelScope.launch {
            filterData(searchQuery)
            showRefreshEvent.updateValue(Event(false))
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

    private fun filterData(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            filterCredentialsByFirestore(searchQuery)
        } else {
            loadData()
        }
    }

    private suspend fun fetchData() {
        val webCredentialsDetailsList = webDetailsRepository.getWebDetailsList()
        viewState.updateValue(WebCredentialsViewState(credentials = webCredentialsDetailsList))
    }

    private fun filterCredentialsByFirestore(query: String) {
        viewModelScope.launch {
            setLoading(true)
            val filteredCredentials = webDetailsRepository.findCredentialsBy(query)
            queryCacheRepository.saveQuery(query)
            if (filteredCredentials.isNotEmpty()) {
                viewState.updateValue(WebCredentialsViewState(credentials = filteredCredentials))
            } else {
                viewState.updateValue(WebCredentialsViewState())
            }
        }
    }

    fun expandCredentials(webDetails: WebDetails) {
        searchExpandedCredentials()
        viewState.update { viewState ->
            val list = viewState.credentials.toMutableList()
            val elementToUpdate = viewState.credentials.find { it == webDetails }
            elementToUpdate?.let {
                val indexOfUpdatedElement = viewState.credentials.indexOf(elementToUpdate)
                val updateCredential = it.copy(shouldExpand = true)
                list[indexOfUpdatedElement] = updateCredential
            }
            viewState.copy(credentials = list)
        }
    }

    fun copyToClipboard(context: Context, credentialInput: String) {
        val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        clipboardManager?.setPrimaryClip(ClipData.newPlainText("Copied Credential", credentialInput))
        showCopiedSnackbarEvent.updateValue(Event(Unit))
    }

    fun tryToNavigateToWebItemEdition(webItem: WebDetails) {
        viewModelScope.launch {
            if (userStatusDataStoreRepository.isAdmin()) {
                navigateToWebItemEditionEvent.updateValue(Event(webItem))
            }
        }
    }

    private fun searchExpandedCredentials() {
        viewState.update { viewState ->
            val updatedCredentials = viewState.credentials.map {
                it.copy(shouldExpand = false)
            }
            viewState.copy(credentials = updatedCredentials)
        }
    }

    fun openSearchView() {
        viewModelScope.launch {
            hideFab()
            val queries = queryCacheRepository.getQueryCacheList()
            queryViewState.updateValue(SearchQueryViewState(queries))
        }
    }

    fun updateCredentialList(input: String) {
        viewModelScope.launch {
            val searchList = queryCacheRepository.fetchCommonQueries(input)
            queryViewState.updateValue(SearchQueryViewState(searchList))
        }
    }

    fun showFab() {
        showFabEvent.updateValue(Event(true))
    }

    private fun hideFab() {
        hideFabEvent.updateValue(Event(false))
    }

    private fun setLoading(isLoading: Boolean) {
        viewState.update { it.copy(isLoading = isLoading) }
    }
}

data class WebCredentialsViewState(
    val isLoading: Boolean = false,
    val credentials: List<WebDetails> = emptyList()
) {
    val isRefreshingButtonVisible: Boolean
        get() = isLoading.not() && credentials.isEmpty()
}

@JvmInline
value class SearchQueryViewState(
    val queryList: List<QueryData> = emptyList()
)