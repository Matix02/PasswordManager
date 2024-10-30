package com.example.passwordmanager.webDetailsList

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.update
import com.example.passwordmanager.extension.updateValue
import com.example.passwordmanager.webDetailsList.data.QueryCacheDataRepository
import com.example.passwordmanager.webDetailsList.data.WebDetailsRepository
import com.example.passwordmanager.webDetailsList.model.WebDetails
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebDetailsListViewModel @Inject constructor(
    private val webDetailsRepository: WebDetailsRepository,
    private val queryCacheRepository: QueryCacheDataRepository
) : ViewModel() {

    val viewState: LiveData<WebCredentialsViewState> = MutableLiveData(WebCredentialsViewState())
    val cancelRefreshingEvent: LiveData<Event<Boolean>> = MutableLiveData()
    val navigateToWebItemEditionEvent: LiveData<Event<WebDetails>> = MutableLiveData()
    val showCopiedSnackbar: LiveData<Event<Unit>> = MutableLiveData()

    fun loadData() {
        viewModelScope.launch {
            viewState.updateValue(WebCredentialsViewState(isLoading = true))
            fetchData()
            val cachedQueries = queryCacheRepository.getQueryCacheList()
            Log.d("MGG3", "Queries = $cachedQueries")
        }
    }

    //TODO wyświetlić listę zapisanych queriesów, i poprawić layout tej listy

    fun refreshData() {
        viewModelScope.launch {
            fetchData()
            Log.d("MGG3Ref", "RefreshData")

            cancelRefreshingEvent.updateValue(Event(false))
        }
    }

    private suspend fun fetchData() {
        val webCredentialsDetailsList = webDetailsRepository.getWebDetailsList()
        viewState.updateValue(WebCredentialsViewState(credentials = webCredentialsDetailsList))
    }

    fun filterCredentialsByFirestore(query: String) {
        viewModelScope.launch {
            val filteredCredentials = webDetailsRepository.findCredentialsBy(query)
            val originalQuery = queryCacheRepository.findQuery(query)
            Log.d("MGG3", "What have I found = $originalQuery")
            queryCacheRepository.deleteQuery(originalQuery)
            queryCacheRepository.saveQuery(query)
            if (filteredCredentials.isNotEmpty()) { //TODO
                viewState.update { it.copy(credentials = filteredCredentials) }
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
        showCopiedSnackbar.updateValue(Event(Unit))
    }

    fun navigateToWebItemEdition(webItem: WebDetails) {
        navigateToWebItemEditionEvent.updateValue(Event(webItem))
    }

    private fun searchExpandedCredentials() {
        viewState.update { viewState ->
            val updatedCredentials = viewState.credentials.map {
                it.copy(shouldExpand = false)
            }
            viewState.copy(credentials = updatedCredentials)
        }
    }

}

data class WebCredentialsViewState(
    val isLoading: Boolean = false,
    val credentials: List<WebDetails> = emptyList()
) {
    val isRefreshingButtonVisible: Boolean
        get() = isLoading.not() && credentials.isEmpty()
}
