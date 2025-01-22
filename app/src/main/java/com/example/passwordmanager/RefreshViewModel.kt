package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.updateValue
import javax.inject.Inject

class RefreshViewModel @Inject constructor() : ViewModel() {

    val fabViewState: LiveData<FabViewState> = MutableLiveData(FabViewState(true))
    val refreshingStatusEvent: LiveData<Boolean> = MutableLiveData(false)
    val refreshListEvent: LiveData<Event<String>> = MutableLiveData()

    fun getCredentials(searchQuery: String) {
        refreshList(searchQuery)
    }

    fun refreshCredentialList(query: String = "") {
        refreshingStatusEvent.updateValue(true)
        getCredentials(query)
    }

    fun updateRefreshingStatus(isRefreshing: Boolean) {
        refreshingStatusEvent.updateValue(isRefreshing)
    }

    private fun refreshList(query: String) {
        (refreshListEvent as MutableLiveData<Event<String>>).value = Event(query)
    }

}

@JvmInline
value class FabViewState(val isVisible: Boolean)