package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.example.passwordmanager.databinding.DialogTextInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InputDialogBuilder(private val context: Context) {

    private val binding = DialogTextInputBinding.inflate(LayoutInflater.from(context))
    private val dialogTextInputLayout = binding.dialogTextInputLayout
    fun build(
        dialogViewEntity: DialogViewEntity,
        errorDialogViewEntity: DialogErrorViewEntity,
        onPositiveButtonClick: (String) -> Boolean
    ): AlertDialog {
        val dialogTextInputLayoutEditText = dialogTextInputLayout.editText
        val dialog = MaterialAlertDialogBuilder(context).apply {
            setView(binding.root)
            setTitle(dialogViewEntity.title)
            setMessage(dialogViewEntity.message)
            setNegativeButton(dialogViewEntity.negativeButtonTitle, null)
            setPositiveButton(dialogViewEntity.positiveButtonTitle, null)
        }.create().also { it.show() }

        dialogTextInputLayout.isErrorEnabled = true

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val isValid = onPositiveButtonClick.invoke(dialogTextInputLayoutEditText?.text.toString())
            if (isValid) {
                dialog.dismiss()
            } else {
                setError(errorDialogViewEntity.message)
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
        }
        dialogTextInputLayoutEditText?.doOnTextChanged { _, _, _, _ ->
            setError(null)
        }
        return dialog
    }

    private fun setError(message: String?) {
        dialogTextInputLayout.error = message
    }
}