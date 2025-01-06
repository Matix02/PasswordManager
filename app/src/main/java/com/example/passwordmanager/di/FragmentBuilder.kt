package com.example.passwordmanager.di

import com.example.passwordmanager.WebCredentialItemDialogFragment
import com.example.passwordmanager.authentication.pin.PinLoginFragment
import com.example.passwordmanager.webDetailsList.WebDetailsListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector
    abstract fun bindWebDetailsListFragment(): WebDetailsListFragment

    @ContributesAndroidInjector
    abstract fun bindWebCredentialItemDialogFragment(): WebCredentialItemDialogFragment

    @ContributesAndroidInjector
    abstract fun bindPinLoginFragment(): PinLoginFragment

}