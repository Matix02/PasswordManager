package com.example.passwordmanager.authentication.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PinVerificationViewModel @Inject constructor(
    private val userStatusDataStoreRepository: UserStatusDataStoreRepository
) : ViewModel() {

    fun checkAdminPin(pinInput: String): Boolean {
        return if (PinValidator.validate(pinInput)) {
            viewModelScope.launch { userStatusDataStoreRepository.setAdminStatus() }
            true
        } else {
            false
        }
    }
}