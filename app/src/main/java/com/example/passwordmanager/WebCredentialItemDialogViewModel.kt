package com.example.passwordmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.authentication.pin.UserStatusDataStoreRepository
import com.example.passwordmanager.extension.Event
import com.example.passwordmanager.extension.update
import com.example.passwordmanager.extension.updateValue
import com.example.passwordmanager.webDetailsList.data.WebDetailsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebCredentialItemDialogViewModel @Inject constructor(
    private val webDetailsRepository: WebDetailsRepository,
    private val userDataStoreRepository: UserStatusDataStoreRepository
) : ViewModel() {

    val viewState: LiveData<NewWebCredentialItemViewEntity?> =
        MutableLiveData(NewWebCredentialItemViewEntity()) //TODO viewstate a w nim view entity

    val showDiscardChangesDialogEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showDeleteConfirmationDialogEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showDeleteDialogWithAuthenticationEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showEditDialogWithAuthenticationEvent: LiveData<Event<Pair<String, NewWebCredentialItem>>> = MutableLiveData()
    val navigateUpEvent: LiveData<Event<Unit>> = MutableLiveData()
    val refreshEvent: LiveData<Event<Unit>> = MutableLiveData()
    val showPinVerificationDialog: LiveData<Event<Unit>> = MutableLiveData()
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
                viewState.updateValue(
                    NewWebCredentialItemViewEntity(
                        newItem = NewWebCredentialItem(
                            id = webCredentialItem.id,
                            urlIcon = webCredentialItem.urlIcon,
                            username = webCredentialItem.username,
                            name = webCredentialItem.name,
                            password = webCredentialItem.password,
                            belongToAdmin = webCredentialItem.belongToAdmin
                        ),
                        oldIndexName = "" //TODO ??? needless?
                    )
                )
            }
            val isAdmin = userDataStoreRepository.isAdmin()
            viewState.update { it?.copy(isAdmin = isAdmin) }
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
                viewState.value?.let {
                    if (it.newItem.belongToAdmin && it.isAdmin.not()) {
                        showEditDialogWithAuthenticationEvent.updateValue(Event(Pair(oldIdName, item)))
                    } else {
                        updateCredential(oldIdName, item)
                    }
                }
                //TODO? trzeba zrobić pull to refresh,by zaciągneło aktualizację, zmienic czy nie zmienić?
            } else {
                validateData()
            }
        }
    }

    fun checkPinToEditData(input: String, oldIdName: String, item: NewWebCredentialItem): Boolean {
        return checkPin(input) {
            viewModelScope.launch { if (it) updateCredential(oldIdName, item) }
        }
    }

    private suspend fun updateCredential(oldIdName: String, item: NewWebCredentialItem) {
        webDetailsRepository.saveWebCredential(oldIdName, item)
        refreshEvent.updateValue(Event(Unit))
        navigateUpEvent.updateValue(Event(Unit))
    }

    private fun addWebCredential() {
        viewState.value?.newItem?.let {
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
        viewState.value?.newItem?.let {
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
        newWebCredentialItem.let {
            if ((it.name.isNotEmpty() && oldWebDetails?.name != it.name)
                || (it.password.isNotEmpty() && oldWebDetails?.password != it.password)
                || (it.urlIcon.isNotEmpty() && oldWebDetails?.urlIcon != it.urlIcon)
                || (it.username.isNotEmpty() && oldWebDetails?.username != it.username)
            ) {
                showDiscardChangesDialogEvent.updateValue(Event(Unit))
            } else {
                navigateUp()
                hideKeyboardEvent.updateValue(Event(Unit))
            }
        }
    }

    fun deleteItem(itemName: String) {
        viewModelScope.launch {
            webDetailsRepository.deleteWebCredential(itemName)
            refreshEvent.updateValue(Event(Unit))
            navigateUp()
        }
    }

    fun tryToDeleteItem() {
        viewState.value?.newItem?.belongToAdmin?.let { belongToAdmin ->
            viewModelScope.launch {
                val isAdmin = userDataStoreRepository.isAdmin()
                if (belongToAdmin && !isAdmin) {
                    showDeleteDialogWithAuthenticationEvent.updateValue(Event(Unit))
                } else {
                    showDeleteConfirmationDialogEvent.updateValue(Event(Unit))
                }
            }
        }
    }

    fun showDeleteConfirmationDialog() {
        viewState.value?.newItem?.name?.let {
            deleteItem(it)
        }
    }

    fun navigateUp() {
        navigateUpEvent.updateValue(Event(Unit))
    }

    fun authorize(isChecked: Boolean) {
        viewModelScope.launch {
            if (isChecked && userDataStoreRepository.isAdmin().not()) {
                showPinVerificationDialog.updateValue(Event(Unit))
            } else {
                updateBelongToAdminAccess(isChecked)
            }
        }
    }

    fun checkPinForDialog(input: String): Boolean {
        return checkPin(input) { updateBelongToAdminAccess(it) }
    }

    fun checkPinForDeleteDialog(input: String): Boolean {
        return checkPin(input) { if (it) showDeleteConfirmationDialog() }
    }

    fun checkPin(input: String, additionalAction: (Boolean) -> Unit): Boolean {
        return (if (input == BuildConfig.ADMIN_KEY) { //TODO shared this key across the app
            viewModelScope.launch { userDataStoreRepository.updateStatus() }
            true
        } else {
            false
        }).also { additionalAction.invoke(it) }
    }

    fun updateBelongToAdminAccess(isChecked: Boolean) {
        viewModelScope.launch {
            viewState.update {
                it?.copy(
                    newItem = it.newItem.copy(
                        belongToAdmin = isChecked
                    ),
                    isAdmin = if (it.isAdmin.not() && isChecked) true else it.isAdmin
                )
            }
        }
    }

}

data class NewWebCredentialItemViewEntity(
    val oldIndexName: String = "",
    val newItem: NewWebCredentialItem = NewWebCredentialItem(),
    val isAdmin: Boolean = false
)

data class NewWebCredentialItem(
    val id: String = "",
    val urlIcon: String = "",
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val belongToAdmin: Boolean = false
)