package com.hudlink.feature.health.di

import com.hudlink.core.data.repository.HealthRepository
import com.hudlink.feature.health.HealthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HealthModule {

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        impl: HealthRepositoryImpl
    ): HealthRepository
}
