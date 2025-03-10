package com.example.passwordmanager.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.MainViewModel
import com.example.passwordmanager.WebCredentialItemDialogViewModel
import com.example.passwordmanager.authentication.pin.PinLoginViewModel
import com.example.passwordmanager.authentication.pin.PinVerificationViewModel
import com.example.passwordmanager.webDetailsList.WebDetailsListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WebDetailsListViewModel::class)
    abstract fun credentialListViewModel(viewModel: WebDetailsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainActivityViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WebCredentialItemDialogViewModel::class)
    abstract fun webCredentialItemDialogViewModel(viewModel: WebCredentialItemDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinLoginViewModel::class)
    abstract fun pinLoginViewModel(viewModel: PinLoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinVerificationViewModel::class)
    abstract fun pinVerificationViewModel(viewModel: PinVerificationViewModel): ViewModel

}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)