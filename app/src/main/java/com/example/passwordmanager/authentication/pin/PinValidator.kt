package com.example.passwordmanager.authentication.pin

import com.example.passwordmanager.BuildConfig


object PinValidator {  // TODO reconsider checking the admin key like this
    fun validate(pin: String): Boolean = pin == BuildConfig.ADMIN_KEY

}