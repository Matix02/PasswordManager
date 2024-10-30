package com.example.passwordmanager.webDetailsList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.RefreshViewModel
import com.example.passwordmanager.WebCredentialItemDialogFragment
import com.example.passwordmanager.databinding.FragmentWebDetailsListBinding
import com.example.passwordmanager.extension.autoClearedLateinit
import com.example.passwordmanager.extension.observeEvent
import com.example.passwordmanager.webDetailsList.view.WebDetailsAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class WebDetailsListFragment : Fragment() {

    private var binding by autoClearedLateinit<FragmentWebDetailsListBinding>()

    private var credentialsAdapter by autoClearedLateinit<WebDetailsAdapter>()

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
        viewModel.loadData()
        setUpRecyclerView()
        observeData()
    }

    private fun filterData(searchQuery: String) {
        if (searchQuery.isNotEmpty()) { //TODO czy tu powinno być sprawdzane czy empty, czy poprzez eventy
            viewModel.filterCredentialsByFirestore(searchQuery)
        } else {
            viewModel.loadData()
        }
    }

    private fun observeData() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            credentialsAdapter.submitList(viewState.credentials)
            binding.progressIndicator.isVisible = viewState.isLoading
            binding.asdasdasdasd.root.isVisible = viewState.isRefreshingButtonVisible
        }
        viewModel.showCopiedSnackbar.observeEvent(viewLifecycleOwner) {
            Snackbar.make(binding.root, "Skopiowano!", Snackbar.LENGTH_SHORT).show()
        }
        refreshViewModel.filterListEvent.observeEvent(viewLifecycleOwner) {
            filterData(it)
        }
        refreshViewModel.refreshListEvent.observeEvent(viewLifecycleOwner) {
            viewModel.refreshData()
        }
        viewModel.cancelRefreshingEvent.observeEvent(viewLifecycleOwner) {
            refreshViewModel.updateRefreshingState(it)
        }
        viewModel.navigateToWebItemEditionEvent.observeEvent(viewLifecycleOwner) {
            Log.d("MGG3", "clicked")
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
    }

    private fun setUpRecyclerView() {
        credentialsAdapter = WebDetailsAdapter(
            onItemClick = { viewModel.expandCredentials(it) },
            onLongItemClick = { viewModel.navigateToWebItemEdition(it) },
            onCredentialClick = { viewModel.copyToClipboard(requireContext(), it) }
        )
        binding.webCredentials.adapter = credentialsAdapter
    }
}