package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.updateValue
import javax.inject.Inject

class RefreshViewModel @Inject constructor() : ViewModel() {

    val refreshingState: LiveData<Boolean> = MutableLiveData(false)
    val filterListEvent: LiveData<Event<String>> = MutableLiveData()
    val refreshListEvent: LiveData<Event<Unit>> = MutableLiveData()

    fun findItems(searchQuery: String) {
        (filterListEvent as MutableLiveData<Event<String>>).value = Event(searchQuery)
    }

    fun refresh() {
        refreshingState.updateValue(true)
        (refreshListEvent as MutableLiveData<Event<Unit>>).value = Event(Unit)
    }

    fun updateRefreshingState(isRefreshing: Boolean) {
        refreshingState.updateValue(isRefreshing)
    }

}