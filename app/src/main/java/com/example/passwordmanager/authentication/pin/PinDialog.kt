package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.example.passwordmanager.databinding.DialogTextInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogBuilder {

    fun build(context: Context, dialogViewEntity: DialogViewEntity, onPositiveButtonClick: (String) -> Boolean): AlertDialog {
        val binding = DialogTextInputBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context).apply {
            setView(binding.root)
            setTitle(dialogViewEntity.title)
            setMessage(dialogViewEntity.message)
            setNegativeButton(dialogViewEntity.negativeButtonTitle, null)
            setPositiveButton(dialogViewEntity.positiveButtonTitle, null)
        }.create().also { it.show() }

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val isValid = onPositiveButtonClick.invoke(binding.dialogTextInputLayout.editText?.text.toString())
            if (isValid) {
                dialog.dismiss()
            } else {
                binding.dialogTextInputLayout.error = dialogViewEntity.errorMessage
            }
        }

        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
        }

        binding.dialogTextInputLayout.editText?.doOnTextChanged { _, _, _, _ ->
            binding.dialogTextInputLayout.error = null
        }

        return dialog
    }
}