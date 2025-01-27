package com.example.passwordmanager.authentication.pin

import android.content.Context
import androidx.appcompat.app.AlertDialog

object AdminAuthorizationDialog {
    fun create(context: Context, onPositiveButtonClick: (String) -> Boolean): AlertDialog {
        val dialogViewEntity = DialogViewEntity(
            title = "Enter Admin Pin",
            message = "To gain access to add, edit or delete credentials, enter Secret Admin Pin.",
            positiveButtonTitle = "OK",
            negativeButtonTitle = "Cancel",
            errorMessage = "Wrong Pin!"
        )
        return DialogBuilder.build(context, dialogViewEntity, onPositiveButtonClick)
    }
}