package com.example.passwordmanager.extension

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AutoClearedNullableValue<T : Any>(
    lifecycleOwner: LifecycleOwner,
    private val onDestroy: (T?) -> Unit = {}
) : ReadWriteProperty<LifecycleOwner, T?>, DefaultLifecycleObserver {
    private var _value: T? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T? = _value

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T?) {
        _value = value
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy.invoke(_value)
        _value = null
    }
}

fun Fragment.autoClearedDialog() = AutoClearedNullableValue<Dialog>(this) {
    it?.dismiss()
}

fun Fragment.autoClearedAlertDialog() = AutoClearedNullableValue<AlertDialog>(this) {
    it?.dismiss()
}

fun AppCompatActivity.autoClearedAlertDialog() = AutoClearedNullableValue<AlertDialog>(this) {
    it?.dismiss()
}

fun AppCompatActivity.autoClearedDialog() = AutoClearedNullableValue<Dialog>(this) {
    it?.dismiss()
}