package com.example.passwordmanager.authentication.pin

data class DialogViewEntity(
    val title: String,
    val message: String,
    val positiveButtonTitle: String,
    val negativeButtonTitle: String
)

@JvmInline
value class DialogErrorViewEntity(val message: String)