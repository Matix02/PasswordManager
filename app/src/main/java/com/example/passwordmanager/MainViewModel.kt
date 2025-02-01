package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.authentication.pin.UserStatusDataStoreRepository
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.updateValue
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userStatusDataStoreRepository: UserStatusDataStoreRepository
) : ViewModel() {

    val navigateToAddCredentialItemEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showNoAccessSnackbarEvent: LiveData<Event<Unit>> = MutableLiveData()

    fun tryToNavigateToAddCredentialItem() {
        viewModelScope.launch {
            if (userStatusDataStoreRepository.isAdmin()) {
                navigateToAddCredentialItemEvent.updateValue(Event(Unit))
            } else {
                showNoAccessSnackbarEvent.updateValue(Event(Unit))
            }
        }
    }
}