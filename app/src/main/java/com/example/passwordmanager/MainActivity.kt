package com.example.passwordmanager

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.authentication.pin.DialogBuilder
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.extension.autoClearedAlertDialog
import com.example.passwordmanager.extension.autoClearedLateinit
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

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
    }

    private fun setUpApplicationContent() {
        setUpViewModel()
        setUpListeners()
        observeEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun setUpNavigationController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun showAdminAuthorizationDialog() {
        fullAccessDialog = DialogBuilder.create(
            context = this,
            onPositiveButtonClick = { viewModel.verifyAccessPin(it) }
        )
    }

    private fun setUpListeners() {
        binding.addFloatingActionButton.setOnClickListener {
            viewModel.tryToNavigateToAddCredentialItem()
        }
    }

    private fun observeEvents() {
        viewModel.navigateToAddCredentialItemEvent.observe(this) {
            showAddCredentialDialog()
        }
        viewModel.showNoAccessSnackbarEvent.observe(this) {
            Snackbar
                .make(binding.root, "Nie masz dostępu!", Snackbar.LENGTH_LONG)
                .setAction("Autoryzuj") { showAdminAuthorizationDialog() }
                .show()
        }
    }

    private fun showAddCredentialDialog() {
        WebCredentialItemDialogFragment().show(supportFragmentManager, "WebCredentialDialog")
    }

}