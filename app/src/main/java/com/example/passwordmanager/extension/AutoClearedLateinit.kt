package com.example.passwordmanager.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AutoClearedLateinit<T : Any>(
    private val onClear: T.() -> Unit,
    lifecycleOwner: LifecycleOwner
) : ReadWriteProperty<LifecycleOwner, T>, DefaultLifecycleObserver {

    private var _value: T? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        return _value ?: throw java.lang.IllegalStateException("Auto Cleared Lateinit Value Get is not available")
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        _value = value
    }

    override fun onDestroy(owner: LifecycleOwner) {
        _value?.let(onClear)
        _value = null
    }
}

fun <T : Any> Fragment.autoClearedLateinit(onClear: T.() -> Unit = {}) = AutoClearedLateinit(onClear = onClear, lifecycleOwner = this)

fun <T : Any> AppCompatActivity.autoClearedLateinit(onClear: T.() -> Unit = {}) = AutoClearedLateinit(onClear = onClear, lifecycleOwner = this)
