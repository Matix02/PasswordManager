package com.example.passwordmanager.authentication.pin

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmationDialogBuilder {

    fun build(context: Context, dialogViewEntity: DialogViewEntity, onPositiveButtonClick: () -> Unit): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context).apply {
            setTitle(dialogViewEntity.title)
            setMessage(dialogViewEntity.message)
            setNegativeButton(dialogViewEntity.negativeButtonTitle, null)
            setPositiveButton(dialogViewEntity.positiveButtonTitle, null)
        }.create().also { it.show() }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            onPositiveButtonClick.invoke()
            dialog.dismiss()
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }
}