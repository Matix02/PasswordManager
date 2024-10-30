package com.example.passwordmanager.extension

import androidx.lifecycle.*

class NonNullMediatorLiveData<T> : MediatorLiveData<T>() {
    fun observe(owner: LifecycleOwner, observer: (t: T) -> Unit) {
        this.observe(owner, Observer {
            it?.let(observer)
        })
    }
}

fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator: NonNullMediatorLiveData<T> = NonNullMediatorLiveData()
    mediator.addSource(this) { it?.let { mediator.value = it } }
    return mediator
}

fun <T> LiveData<Event<T>>.observeEvent(viewLifecycleOwner: LifecycleOwner, onEmit: (T) -> Unit) {
    nonNull().observe(viewLifecycleOwner) { event ->
        event.getContentIfNotHandled()?.also {
            onEmit(it)
        }
    }
}

inline fun <T> LiveData<T>.update(crossinline change: (T) -> T) {
    value?.let { updateValue(change.invoke(it)) }
}

fun <T> LiveData<T>.updateValue(state: T?) {
    (this as? MutableLiveData<T>)?.apply {
        value = state
    }
}