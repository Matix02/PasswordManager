package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.updateValue
import com.example.passwordmanager.webDetailsList.data.WebDetailsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebCredentialItemDialogViewModel @Inject constructor(
    private val webDetailsRepository: WebDetailsRepository
) : ViewModel() {

    val viewEntity: LiveData<NewWebCredentialItemViewEntity?> = MutableLiveData(NewWebCredentialItemViewEntity())

    val showDiscardChangesDialogEvent: LiveData<Event<Unit>> = MutableLiveData()
    val navigateUpEvent: LiveData<Event<Unit>> = MutableLiveData()
    val refreshEvent: LiveData<Event<Unit>> = MutableLiveData()
    val hideKeyboardEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showNameEmptyErrorInputEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showPasswordEmptyErrorInputEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showUsernameEmptyErrorInputEvent: LiveData<Event<Unit>> = MutableLiveData()
    val saveCredentialItemEvent: LiveData<Event<Unit>> = MutableLiveData()
    var oldWebDetails: NewWebCredentialItem? = null

    fun init(webDetails: NewWebCredentialItem?) {
        viewModelScope.launch {
            webDetails?.let { webCredentialItem ->
                oldWebDetails = webDetails
                viewEntity.updateValue(
                    NewWebCredentialItemViewEntity(
                        newItem = NewWebCredentialItem(
                            id = webCredentialItem.id,
                            urlIcon = webCredentialItem.urlIcon,
                            username = webCredentialItem.username,
                            name = webCredentialItem.name,
                            password = webCredentialItem.password
                        ),
                        oldIndexName = "" //TODO ??? needless?
                    )
                )
            }
        }
    }

    fun saveChanges() {
        saveCredentialItemEvent.updateValue(Event(Unit))
    }

    fun tryToAddOrUpdate(item: NewWebCredentialItem) {
        oldWebDetails?.let {
            saveWebCredential(item, it.name)
        } ?: addWebCredential()
    }

    private fun saveWebCredential(item: NewWebCredentialItem, oldIdName: String) {
        viewModelScope.launch {
            if (item.name.isNotEmpty() && item.password.isNotEmpty() && item.username.isNotEmpty()) {
                viewEntity.value?.let {
                    updateCredential(oldIdName, item)
                }
                //TODO? trzeba zrobić pull to refresh,by zaciągneło aktualizację, zmienic czy nie zmienić?
            } else {
                validateData()
            }
        }
    }

    private suspend fun updateCredential(oldIdName: String, item: NewWebCredentialItem) {
        webDetailsRepository.saveWebCredential(oldIdName, item)
        refreshEvent.updateValue(Event(Unit))
        navigateUpEvent.updateValue(Event(Unit))
    }

    private fun addWebCredential() {
        viewEntity.value?.newItem?.let {
            viewModelScope.launch {
                if (it.name.isNotEmpty() && it.password.isNotEmpty() && it.username.isNotEmpty()) {
                    webDetailsRepository.addWebCredential(it)
                    refreshEvent.updateValue(Event(Unit))
                    navigateUpEvent.updateValue(Event(Unit))
                } else {
                    validateData()
                }
            }
        }
    }

    private fun validateData() {
        viewEntity.value?.newItem?.let {
            if (it.name.isEmpty()) {
                showNameEmptyErrorInputEvent.updateValue(Event(Unit))
            }
            if (it.username.isEmpty()) {
                showUsernameEmptyErrorInputEvent.updateValue(Event(Unit))
            }
            if (it.password.isEmpty()) {
                showPasswordEmptyErrorInputEvent.updateValue(Event(Unit))
            }
        }
    }

    fun checkChanges(newWebCredentialItem: NewWebCredentialItem) {
        if (oldWebDetails != newWebCredentialItem) {
            showDiscardChangesDialogEvent.updateValue(Event(Unit))
        } else {
            navigateUp()
            hideKeyboardEvent.updateValue(Event(Unit))
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            // webDetailsRepository.deleteWebCredential(itemName) //TODO deactivated for testing
            refreshEvent.updateValue(Event(Unit))
            navigateUp()
        }
    }

    fun navigateUp() {
        navigateUpEvent.updateValue(Event(Unit))
    }

}

data class NewWebCredentialItemViewEntity( //TODO Modify
    val oldIndexName: String = "",
    val newItem: NewWebCredentialItem = NewWebCredentialItem()
)

data class NewWebCredentialItem( //TODO Export
    val id: String = "",
    val urlIcon: String = "",
    val name: String = "",
    val username: String = "",
    val password: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return with(other as NewWebCredentialItem) {
            when {
                name.isNotEmpty() || name != other.name -> false
                password.isNotEmpty() || password != other.password -> false
                urlIcon.isNotEmpty() || urlIcon != other.urlIcon -> false
                username.isNotEmpty() || username != other.username -> false
                else -> true
            }
        }
    }

    override fun hashCode(): Int {
        var result = if (name.isNotEmpty()) name.hashCode() else 0
        result = 31 * result + if (password.isNotEmpty()) password.hashCode() else 0
        result = 31 * result + if (urlIcon.isNotEmpty()) urlIcon.hashCode() else 0
        result = 31 * result + if (username.isNotEmpty()) username.hashCode() else 0
        return result
    }
}