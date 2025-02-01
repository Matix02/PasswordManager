package com.example.passwordmanager

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.authentication.pin.AdminPinAuthorizationDialog
import com.example.passwordmanager.authentication.pin.PinVerificationViewModel
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.extension.autoClearedAlertDialog
import com.example.passwordmanager.extension.autoClearedLateinit
import com.example.passwordmanager.extension.observeEvent
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private val pinVerificationViewModel: PinVerificationViewModel by viewModels { viewModelFactory }
    private val refreshViewModel: RefreshViewModel by viewModels()

    private var binding by autoClearedLateinit<ActivityMainBinding>()
    private var fullAccessDialog: AlertDialog? by autoClearedAlertDialog()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigationController()
        setUpApplicationContent()
        observeViewState()
    }

    private fun setUpApplicationContent() {
        setUpListeners()
        observeEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    private fun setUpNavigationController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setUpListeners() {
        binding.addFloatingActionButton.setOnClickListener {
            viewModel.tryToNavigateToAddCredentialItem()
        }
    }

    private fun observeViewState() {
        refreshViewModel.fabViewState.observe(this) {
            if (it.isVisible) binding.addFloatingActionButton.show() else binding.addFloatingActionButton.hide()
        }
    }

    private fun observeEvents() {
        viewModel.navigateToAddCredentialItemEvent.observeEvent(this) {
            showAddCredentialDialog()
        }
        viewModel.showNoAccessSnackbarEvent.observeEvent(this) {
            Snackbar
                .make(binding.root, "Nie masz dostępu!", Snackbar.LENGTH_LONG)
                .setAction("Autoryzuj") { showAdminAuthorizationDialog() }
                .show()
        }
    }

    private fun showAddCredentialDialog() {
        WebCredentialItemDialogFragment().show(supportFragmentManager, "WebCredentialDialog")
    }

    private fun showAdminAuthorizationDialog() {
        fullAccessDialog = AdminPinAuthorizationDialog.create(
            context = this,
            onPositiveButtonClick = { pinVerificationViewModel.checkAdminPin(it) }
        )
    }
}