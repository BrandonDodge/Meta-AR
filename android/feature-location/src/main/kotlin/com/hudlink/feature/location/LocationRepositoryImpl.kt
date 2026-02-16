package com.hudlink.feature.location

import com.hudlink.core.data.model.GpsData
import com.hudlink.core.data.model.HeadingData
import com.hudlink.core.data.repository.LocationRepository
import com.hudlink.core.storage.dao.GpsDao
import com.hudlink.core.storage.entity.GpsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val mockDataSource: MockLocationDataSource,
    private val gpsDao: GpsDao
) : LocationRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Cache latest heading in memory (not persisted - too high frequency)
    private val latestHeading = MutableStateFlow<HeadingData?>(null)

    override fun observeGps(): Flow<GpsData> =
        mockDataSource.observeGps()
            .onEach { data ->
                // Persist GPS readings to local storage
                scope.launch {
                    gpsDao.insert(GpsEntity.fromDomain(data))
                }
            }

    override fun observeHeading(): Flow<HeadingData> =
        mockDataSource.observeHeading()
            .onEach { data ->
                // Only cache in memory - heading updates too frequent for DB
                latestHeading.value = data
            }

    override suspend fun getLatestGps(): GpsData? =
        gpsDao.getLatest()?.toDomain()

    override suspend fun getLatestHeading(): HeadingData? =
        latestHeading.value

    override suspend fun getGpsHistory(
        startEpochMillis: Long,
        endEpochMillis: Long
    ): List<GpsData> =
        gpsDao.getByTimeRange(startEpochMillis, endEpochMillis)
            .map { it.toDomain() }
}
