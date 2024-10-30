package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.passwordmanager.WebCredentialItemDialogFragment
import com.example.passwordmanager.databinding.FragmentFullAccessPinBinding
import com.example.passwordmanager.extension.autoClearedLateinit
import com.example.passwordmanager.extension.observeEvent
import com.example.passwordmanager.extension.showSoftKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FullAccessPinFragment : Fragment() {

    private var binding by autoClearedLateinit<FragmentFullAccessPinBinding>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: FullAccessPinViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    //TODO dorobic shareprefy po tym jak wpisze się pin itd.
    //TODO schowac aktualny toolbar, dorobic cos na wzor tego, z back button czy cos
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFullAccessPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[FullAccessPinViewModel::class.java]

        binding.txtPin.showSoftKeyboard()
        setUpListener()
        observeEvents()
    }

    private fun setUpListener() {
        binding.btnSave.setOnClickListener { //TODO przenieść logikę zapisu i odczytu do VM
            viewModel.checkPin(binding.txtPin.text.toString())
        }
    }

    private fun observeEvents() {
        viewModel.showSuccessfulSnackbarEvent.observeEvent(viewLifecycleOwner) {
            Snackbar.make(binding.root, "Przyznano God-Mode!", Snackbar.LENGTH_SHORT).show()
            Log.d("MGG3", "showSuccessfullEvent")
            setFragmentResult(WebCredentialItemDialogFragment.REQUEST_KEY, bundleOf(WebCredentialItemDialogFragment.RESULT_KEY to true))
            findNavController().navigateUp()
        }
        viewModel.showAuthorizationErrorEvent.observeEvent(viewLifecycleOwner) {
            binding.txtPin.error = "Nie ze mną te numery, Hackerinio!"
        }
    }

}