package com.example.passwordmanager.webDetailsList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.FabViewState
import com.example.passwordmanager.R
import com.example.passwordmanager.RefreshViewModel
import com.example.passwordmanager.SearchQueryAdapter
import com.example.passwordmanager.WebCredentialItemDialogFragment
import com.example.passwordmanager.authentication.pin.AdminAuthorizationDialog
import com.example.passwordmanager.databinding.FragmentWebDetailsListBinding
import com.example.passwordmanager.extension.autoClearedAlertDialog
import com.example.passwordmanager.extension.autoClearedLateinit
import com.example.passwordmanager.extension.observeEvent
import com.example.passwordmanager.extension.updateValue
import com.example.passwordmanager.webDetailsList.view.WebDetailsAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class WebDetailsListFragment : Fragment() {

    private var binding by autoClearedLateinit<FragmentWebDetailsListBinding>()

    private var credentialsAdapter by autoClearedLateinit<WebDetailsAdapter>()
    private var searchQueryAdapter by autoClearedLateinit<SearchQueryAdapter>()
    private var fullAccessDialog: AlertDialog? by autoClearedAlertDialog()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WebDetailsListViewModel

    private val refreshViewModel: RefreshViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWebDetailsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[WebDetailsListViewModel::class.java]
        setUpSearchBar()
        setUpListeners()
        setUpRecycleViews()
        observeData()
        observeEvents()
    }

    private fun setUpSearchBar() {
        binding.searchBar.inflateMenu(R.menu.top_app_bar)
        binding.searchBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.secretAccess -> {
                    showAdminAuthorizationDialog()
                    true
                }
                R.id.todoFeature -> { //TODO delete
                    findNavController().navigate(WebDetailsListFragmentDirections.actionWebDetailsListToPinLoginFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpListeners() {
        binding.searchBar.setOnClickListener {
            binding.searchView.show()
        }
        binding.searchView.addTransitionListener { _, _, searchViewState ->
            when (searchViewState) {
                SearchView.TransitionState.SHOWING -> viewModel.openSearchView()
                SearchView.TransitionState.HIDING -> viewModel.showFab()
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
    }

    private fun observeData() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            credentialsAdapter.submitList(viewState.credentials)
            binding.progressIndicator.isVisible = viewState.isLoading
            binding.emptyListView.root.isVisible = viewState.isRefreshingButtonVisible
        }
        viewModel.queryViewState.observe(viewLifecycleOwner) {
            searchQueryAdapter.submitList(it.queryList)
        }
    }

    private fun observeEvents() {
        viewModel.showCopiedSnackbarEvent.observeEvent(viewLifecycleOwner) {
            Snackbar.make(binding.root, "Skopiowano!", Snackbar.LENGTH_SHORT).show()
        }
        viewModel.showRefreshEvent.observeEvent(viewLifecycleOwner) {
            refreshViewModel.updateRefreshingStatus(it)
        }
        viewModel.navigateToWebItemEditionEvent.observeEvent(viewLifecycleOwner) {
            val bundle = Bundle().apply {
                with(WebCredentialItemDialogFragment) {
                    putString(CREDENTIAL_ITEM_ID_KEY, it.id)
                    putString(CREDENTIAL_ITEM_URL_ICON_KEY, it.icon)
                    putString(CREDENTIAL_ITEM_NAME_KEY, it.name)
                    putString(CREDENTIAL_ITEM_LOGIN_KEY, it.username)
                    putString(CREDENTIAL_ITEM_PASSWORD_KEY, it.password)
                }
            }
            val dialogFragment = WebCredentialItemDialogFragment.newInstance(bundle)
            dialogFragment.show(childFragmentManager, "WebCredentialDialog")
        }
        refreshViewModel.refreshListEvent.observeEvent(viewLifecycleOwner) {
            viewModel.refreshData(it)
        }
        refreshViewModel.refreshingStatusEvent.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
        viewModel.showFabEvent.observeEvent(viewLifecycleOwner) { showFab ->
            refreshViewModel.fabViewState.updateValue(FabViewState(showFab))
        }
        viewModel.hideFabEvent.observeEvent(viewLifecycleOwner) { hideFab ->
            refreshViewModel.fabViewState.updateValue(FabViewState(hideFab))
        }
    }

    private fun showAdminAuthorizationDialog() {
        fullAccessDialog = AdminAuthorizationDialog.create(
            context = requireContext(),
            onPositiveButtonClick = { viewModel.verifyAccessPin(it) }
        )
    }

    private fun setUpRecycleViews() {
        setUpCredentialsAdapter()
        setUpRecentSearchQueryView()
    }

    private fun setUpCredentialsAdapter() {
        credentialsAdapter = WebDetailsAdapter(
            onItemClick = { viewModel.expandCredentials(it) },
            onLongItemClick = { viewModel.tryToNavigateToWebItemEdition(it) },
            onCredentialClick = { viewModel.copyToClipboard(requireContext(), it) }
        )
        binding.webCredentials.adapter = credentialsAdapter
    }

    private fun setUpRecentSearchQueryView() {
        searchQueryAdapter = SearchQueryAdapter { updateCredentialList(it.query) }
        binding.searchQueryList.adapter = searchQueryAdapter
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.searchQueryList.addItemDecoration(divider)
    }

    private fun updateCredentialList(query: String) {
        refreshViewModel.getCredentials(query)
        binding.searchBar.setText(query)
        binding.searchView.hide()
    }
}