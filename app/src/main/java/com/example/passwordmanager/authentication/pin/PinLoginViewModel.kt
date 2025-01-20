package com.example.passwordmanager.authentication.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passwordmanager.extension.update
import javax.inject.Inject

class PinLoginViewModel @Inject constructor() : ViewModel() {

    val viewState: LiveData<PinLoginViewState> = MutableLiveData(PinLoginViewState(""))

    fun updatePin(key: KeyboardKey) {
        viewState.value?.let { currentViewState ->
            val updatedPin = handlePinKeyInput(currentViewState.pin, key)
            if (updatedPin != null) {
                viewState.update { it.copy(pin = updatedPin) }
            }
        }
    }

    private fun handlePinKeyInput(currentPin: String, key: KeyboardKey): String? {
        val isDeleteKey = key.keyType.isDeleteKeyType()
        return when {
            currentPin.length < MAX_PIN_CIRCLE_COUNT -> if (isDeleteKey) currentPin.dropLastChar() else currentPin + key.keyValue
            isDeleteKey -> currentPin.dropLastChar()
            else -> null
        }
    }

    private fun String.dropLastChar(): String = this.dropLast(1)
}

data class PinLoginViewState(val pin: String) {

    val pinLength
        get() = pin.length
}
