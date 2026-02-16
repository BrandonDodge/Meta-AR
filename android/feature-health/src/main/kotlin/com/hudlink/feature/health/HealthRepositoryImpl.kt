package com.hudlink.feature.health

import com.hudlink.core.data.model.HeartRateData
import com.hudlink.core.data.repository.HealthRepository
import com.hudlink.core.storage.dao.HeartRateDao
import com.hudlink.core.storage.entity.HeartRateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepositoryImpl @Inject constructor(
    private val mockDataSource: MockHealthDataSource,
    private val heartRateDao: HeartRateDao
) : HealthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun observeHeartRate(): Flow<HeartRateData> =
        mockDataSource.observeHeartRate()
            .onEach { data ->
                // Persist each reading to local storage
                scope.launch {
                    heartRateDao.insert(HeartRateEntity.fromDomain(data))
                }
            }

    override suspend fun getLatestHeartRate(): HeartRateData? =
        heartRateDao.getLatest()?.toDomain()

    override suspend fun getHeartRateHistory(
        startEpochMillis: Long,
        endEpochMillis: Long
    ): List<HeartRateData> =
        heartRateDao.getByTimeRange(startEpochMillis, endEpochMillis)
            .map { it.toDomain() }
}
