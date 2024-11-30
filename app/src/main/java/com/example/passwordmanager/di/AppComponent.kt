package com.example.passwordmanager.di

import com.example.passwordmanager.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuilder::class,
        FragmentBuilder::class,
        ViewModelModule::class,
        RepositoryModule::class,
        NetworkModule::class,
        DataStoreModule::class
    ]
)
interface AppComponent : AndroidInjector<MyApplication> {

    override fun inject(application: MyApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: MyApplication): Builder

        fun build(): AppComponent
    }
}