package com.example.passwordmanager.authentication.pin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.databinding.FragmentPinLoginBinding
import com.example.passwordmanager.extension.autoClearedLateinit
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PinLoginFragment : Fragment() {

    private var binding by autoClearedLateinit<FragmentPinLoginBinding>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: PinLoginViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPinLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[PinLoginViewModel::class.java]
        setUpListeners()
        observeData()
    }

    private fun observeData() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.pinView.setPinCircle(viewState.pin.count())
        }
    }

    private fun setUpListeners() {
        binding.keyboardView.onKeyPress = {
            viewModel.updatePin(it)
        }
    }

}
