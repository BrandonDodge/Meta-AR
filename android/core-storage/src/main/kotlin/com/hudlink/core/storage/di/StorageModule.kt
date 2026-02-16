package com.hudlink.core.storage.di

import android.content.Context
import androidx.room.Room
import com.hudlink.core.storage.HudLinkDatabase
import com.hudlink.core.storage.dao.GpsDao
import com.hudlink.core.storage.dao.HeartRateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HudLinkDatabase = Room.databaseBuilder(
        context,
        HudLinkDatabase::class.java,
        HudLinkDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration() // Safe for development; use proper migrations in production
        .build()

    @Provides
    fun provideHeartRateDao(database: HudLinkDatabase): HeartRateDao =
        database.heartRateDao()

    @Provides
    fun provideGpsDao(database: HudLinkDatabase): GpsDao =
        database.gpsDao()
}
