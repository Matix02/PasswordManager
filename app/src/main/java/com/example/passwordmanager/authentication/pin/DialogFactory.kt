package com.example.passwordmanager.authentication.pin

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogFactory {

    fun inputDialog(
        context: Context,
        dialogViewEntity: DialogViewEntity,
        errorDialogViewEntity: DialogErrorViewEntity,
        onPositiveButtonClick: (String) -> Boolean
    ): AlertDialog = InputDialogBuilder(context).build(dialogViewEntity, errorDialogViewEntity, onPositiveButtonClick)

    fun confirmationDialog(
        context: Context,
        dialogViewEntity: DialogViewEntity,
        onPositiveButtonClick: () -> Unit
    ): AlertDialog = ConfirmationDialogBuilder().build(context, dialogViewEntity, onPositiveButtonClick)
}