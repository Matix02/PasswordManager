package com.example.passwordmanager.authentication.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.BuildConfig
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.updateValue
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PIN = BuildConfig.ADMIN_KEY

class FullAccessPinViewModel @Inject constructor(
    private val userStatusDataStoreRepository: UserStatusDataStoreRepository
) : ViewModel() {

    val showSuccessfulSnackbarEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showAuthorizationErrorEvent: LiveData<Event<Unit>> = MutableLiveData()

    //TODO sprawdzać i zdefiniować, co może super user a co może zwykły

    fun checkPin(input: String) {
        if (input == PIN) {
            viewModelScope.launch {
                userStatusDataStoreRepository.updateStatus()
                showSuccessfulSnackbarEvent.updateValue(Event(Unit))

            }
        } else {
            showAuthorizationErrorEvent.updateValue(Event(Unit))
        }
    }


}