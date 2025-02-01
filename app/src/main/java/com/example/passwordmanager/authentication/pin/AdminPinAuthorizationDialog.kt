package com.example.passwordmanager.authentication.pin

import android.content.Context
import androidx.appcompat.app.AlertDialog

object AdminPinAuthorizationDialog {

    fun create(context: Context, onPositiveButtonClick: (String) -> Boolean): AlertDialog {
        val dialogViewEntity = DialogViewEntity(
            title = "Enter Admin Pin",
            message = "To gain access to add, edit or delete credentials, enter Secret Admin Pin.",
            positiveButtonTitle = "OK",
            negativeButtonTitle = "Cancel"
        )
        val errorDialogViewEntity = DialogErrorViewEntity(message = "Wrong Pin!")
        return DialogFactory.inputDialog(context, dialogViewEntity, errorDialogViewEntity, onPositiveButtonClick)
    }
}