package com.example.passwordmanager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.databinding.FragmentWebCredentialItemDialogBinding
import com.example.passwordmanager.extension.autoClearedAlertDialog
import com.example.passwordmanager.extension.autoClearedLateinit
import com.example.passwordmanager.extension.observeEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class WebCredentialItemDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WebCredentialItemDialogViewModel

    private var binding by autoClearedLateinit<FragmentWebCredentialItemDialogBinding>()
    private val refreshViewModel: RefreshViewModel by activityViewModels()

    private var alertPinDialog: AlertDialog? by autoClearedAlertDialog()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWebCredentialItemDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[WebCredentialItemDialogViewModel::class.java]
        val webDetailsItem = arguments?.let {
            NewWebCredentialItem(
                id = it.getString(CREDENTIAL_ITEM_ID_KEY).orEmpty(),
                urlIcon = it.getString(CREDENTIAL_ITEM_URL_ICON_KEY).orEmpty(),
                name = it.getString(CREDENTIAL_ITEM_NAME_KEY).orEmpty(),
                username = it.getString(CREDENTIAL_ITEM_LOGIN_KEY).orEmpty(),
                password = it.getString(CREDENTIAL_ITEM_PASSWORD_KEY).orEmpty()
            )
        }
        viewModel.init(webDetailsItem)
        observeData()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.saveChanges()
        }
        binding.btnClose.setOnClickListener {
            viewModel.checkChanges(
                NewWebCredentialItem(
                    urlIcon = binding.txtUri.text.toString(),
                    name = binding.txtName.text.toString(),
                    password = binding.txtPassword.text.toString(),
                    username = binding.txtLogin.text.toString()
                )
            )
        }
        binding.btnDelete.setOnClickListener {
            viewModel.tryToDeleteItem()
        }
        binding.txtPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveChanges()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun showPinVerificationDialog() {
        alertPinDialog?.dismiss()
//        alertPinDialog = DialogBuilder.create(  //TODO refactor Item Fragment
//            context = requireContext(),
//            dialogViewEntity = ,
//            onPositiveButtonClick = { viewModel.checkPinForDialog(it) }
//        )
    }

    private fun showPinVerificationDialogForDeleting() {
        alertPinDialog?.dismiss()
//        alertPinDialog = DialogBuilder.create( //TODO refactor Item Fragment
//            context = requireContext(),
//            dialogViewEntity = ,
//            onPositiveButtonClick = { viewModel.checkPinForDeleteDialog(it) },
//        )
    }

    private fun showPinVerificationDialogWithoutAuthentication() {
        alertPinDialog?.dismiss()
//        alertPinDialog = DialogBuilder.create2( //TODO refactor Item Fragment
//            context = requireContext(),
//            onPositiveButtonClick = { viewModel.showDeleteConfirmationDialog() }
//        )
    }

    private fun showPinVerificationDialogToEditItem(oldName: String, item: NewWebCredentialItem) {
        alertPinDialog?.dismiss()
//        alertPinDialog = DialogBuilder.create( //TODO refactor Item Fragment
//            context = requireContext(),
//            dialogViewEntity = ,
//            onPositiveButtonClick = { viewModel.checkPinToEditData(input = it, oldName, item) }
//        )
    }

    private fun observeData() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            viewState?.newItem?.let {
                binding.txtUri.setText(it.urlIcon)
                binding.txtName.setText(it.name)
                binding.txtPassword.setText(it.password)
                binding.txtLogin.setText(it.username)
            }
            binding.btnDelete.isVisible = viewModel.oldWebDetails != null
        }
        viewModel.saveCredentialItemEvent.observeEvent(viewLifecycleOwner) {
            viewModel.tryToAddOrUpdate(
                NewWebCredentialItem(
                    urlIcon = binding.txtUri.text.toString(),
                    name = binding.txtName.text.toString(),
                    password = binding.txtPassword.text.toString(),
                    username = binding.txtLogin.text.toString()
                )
            )
        }
        viewModel.showUsernameEmptyErrorInputEvent.observeEvent(viewLifecycleOwner) {
            binding.txtLogin.error = "Empty field"
        }
        viewModel.showNameEmptyErrorInputEvent.observeEvent(viewLifecycleOwner) {
            binding.txtName.error = "Empty field"
        }
        viewModel.showPasswordEmptyErrorInputEvent.observeEvent(viewLifecycleOwner) {
            binding.txtPassword.error = "Empty field"
        }
        viewModel.navigateUpEvent.observeEvent(viewLifecycleOwner) {
            dismiss()
        }
        viewModel.refreshEvent.observeEvent(viewLifecycleOwner) {
            refreshViewModel.refreshCredentialList()
        }
        viewModel.hideKeyboardEvent.observeEvent(viewLifecycleOwner) {
            binding.root.clearFocus()
        }
        viewModel.showDeleteDialogWithAuthenticationEvent.observeEvent(viewLifecycleOwner) {
            showPinVerificationDialogForDeleting()
        }
        viewModel.showDeleteConfirmationDialogEvent.observeEvent(viewLifecycleOwner) {
            showPinVerificationDialogWithoutAuthentication()
        }
        viewModel.showDiscardChangesDialogEvent.observeEvent(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext()) //TODO refactor Item Fragment
                .setTitle("Discard Changes?")
                .setMessage("") //TODO Message
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ok") { _, _ ->
                    viewModel.navigateUp()
                }.show()
        }
        viewModel.showPinVerificationDialogEvent.observeEvent(viewLifecycleOwner) {
            showPinVerificationDialog()
        }
        viewModel.showEditDialogWithAuthenticationEvent.observeEvent(viewLifecycleOwner) {
            showPinVerificationDialogToEditItem(it.first, it.second)
        }
    }

    companion object {
        const val CREDENTIAL_ITEM_URL_ICON_KEY = "web-credential-item-url-icon-key"
        const val CREDENTIAL_ITEM_ID_KEY = "web-credential-item-id-key"
        const val CREDENTIAL_ITEM_NAME_KEY = "web-credential-item-name-key"
        const val CREDENTIAL_ITEM_LOGIN_KEY = "web-credential-item-login-key"
        const val CREDENTIAL_ITEM_PASSWORD_KEY = "web-credential-item-password-key"

        const val REQUEST_KEY = "isAuthorizationSuccessful"
        const val RESULT_KEY = "resultKey"

        fun newInstance(data: Bundle?): WebCredentialItemDialogFragment {
            return WebCredentialItemDialogFragment().apply {
                arguments = data
            }
        }
    }

}
