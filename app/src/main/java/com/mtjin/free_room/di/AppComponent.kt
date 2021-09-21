package com.mtjin.free_room.di

import android.content.Context
import com.mtjin.free_room.di.data.LocalDataModule
import com.mtjin.free_room.di.data.RemoteDataModule
import com.mtjin.free_room.di.data.RepositoryModule
import com.mtjin.free_room.di.views.ViewModelFactoryModule
import com.mtjin.free_room.di.views.ViewModelModule
import com.mtjin.free_room.views.main.MainComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelModule::class, ViewModelFactoryModule::class, AppSubComponentsModule::class, RepositoryModule::class, LocalDataModule::class, RemoteDataModule::class]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory

}