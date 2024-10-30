package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.example.passwordmanager.databinding.DialogTextInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogBuilder {

    fun create(
        context: Context,
        onPositiveButtonClick: (String) -> Boolean,
        onCancelListener: (() -> Unit)? = null
    ): AlertDialog {
        val binding = DialogTextInputBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context).apply {
            setView(binding.root)
            setTitle("Test")
            setCancelable(false)
            setNegativeButton("Cancel", null)
            setPositiveButton("OK", null)
        }.create().also { it.show() }

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val isValid = onPositiveButtonClick.invoke(binding.dialogTextInputLayout.editText?.text.toString())
            if (isValid) {
                dialog.dismiss()
            } else {
                binding.dialogTextInputLayout.error = "Wrong Pin!"
            }
        }

        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            onCancelListener?.invoke()
            dialog.dismiss()
        }

        binding.dialogTextInputLayout.editText?.doOnTextChanged { _, _, _, _ ->
            binding.dialogTextInputLayout.error = null
        }

        return dialog
    }

    fun create2(
        context: Context,
        onPositiveButtonClick: () -> Unit,
    ): AlertDialog {
        val binding = DialogTextInputBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context).apply {
            setTitle("Test")
            setCancelable(false)
            setNegativeButton("Cancel", null)
            setPositiveButton("OK", null)
        }.create().also { it.show() }

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            onPositiveButtonClick.invoke()
        }

        binding.dialogTextInputLayout.editText?.doOnTextChanged { _, _, _, _ ->
            binding.dialogTextInputLayout.error = null
        }

        return dialog
    }
}