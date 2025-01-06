package com.example.passwordmanager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.authentication.pin.DialogBuilder
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.extension.autoClearedAlertDialog
import com.example.passwordmanager.extension.autoClearedLateinit
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val refreshViewModel: RefreshViewModel by viewModels()
    private lateinit var viewModel: MainViewModel

    private var searchQueryAdapter by autoClearedLateinit<SearchQueryAdapter>()
    private var binding by autoClearedLateinit<ActivityMainBinding>()
    private var fullAccessDialog: AlertDialog? by autoClearedAlertDialog()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigationController()
        setSupportActionBar(binding.searchBar)
        setUpApplicationContent()
    }

    private fun setUpApplicationContent() {
        setUpViewModel()
        setUpListeners()
        setRecentSearchQueryView()
        observeViewModel()
        observeEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
        // viewModel.clearPersistenceUserAccess()
    }

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.clearPersistenceUserAccess()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.secretAccess -> {
                showAdminAuthorizationDialog()
                true
            }
            R.id.todoFeature -> {
                navController.navigate(R.id.pinLoginFragment)
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
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
        binding.searchView.addTransitionListener { _, _, searchViewState ->
            when (searchViewState) {
                SearchView.TransitionState.SHOWING -> viewModel.fetchSearchQueryList()
                SearchView.TransitionState.HIDING -> viewModel.showAddCredentialFab()
                SearchView.TransitionState.HIDDEN,
                SearchView.TransitionState.SHOWN -> Unit
            }
        }
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            updateCredentialList(query)
            viewModel.updateCredentialList(query)
            return@setOnEditorActionListener true
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshViewModel.refreshCredentialList(binding.searchBar.text.toString())
        }
        binding.addFloatingActionButton.setOnClickListener {
            viewModel.tryToNavigateToAddCredentialItem()
        }
    }

    private fun observeViewModel() {
        refreshViewModel.refreshingStatusEvent.observe(this) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
        viewModel.viewState.observe(this) {
            searchQueryAdapter.submitList(it.queryList)
            binding.addFloatingActionButton.isVisible = it.isFabVisible
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

    private fun setRecentSearchQueryView() {
        searchQueryAdapter = SearchQueryAdapter { updateCredentialList(it.query) }
        binding.searchQueryList.adapter = searchQueryAdapter
        val divider = MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.searchQueryList.addItemDecoration(divider)
    }

    private fun updateCredentialList(query: String) {
        refreshViewModel.getCredentials(query)
        binding.searchBar.setText(query)
        binding.searchView.hide()
    }
}