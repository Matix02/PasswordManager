package com.example.passwordmanager.di

import com.example.passwordmanager.WebCredentialItemDialogFragment
import com.example.passwordmanager.authentication.pin.FullAccessPinFragment
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
    abstract fun bindFullAccessPinFragment(): FullAccessPinFragment
}
//https://medium.com/tompee/dagger-2-scopes-and-subcomponents-d54d58511781