package com.pavit.vanilla.di

import com.pavit.vanilla.data.AppRepository
import com.pavit.vanilla.data.AppRepositoryImpl
import com.pavit.vanilla.domain.ObserveFilteredGroupedApps
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// it's a provider module => @Module
// installs in SingletonComponent => what does this do?
// why is this class Abstract?
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    // when someone asks for app repo, give them app repo impl
    @Binds
    abstract fun bindAppRepository(appRepositoryImpl: AppRepositoryImpl): AppRepository
}