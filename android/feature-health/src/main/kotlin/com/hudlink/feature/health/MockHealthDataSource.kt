package com.hudlink.feature.health

import com.hudlink.core.data.model.DataQuality
import com.hudlink.core.data.model.DataSource
import com.hudlink.core.data.model.HeartRateData
import com.hudlink.core.data.model.MeasurementType
import com.hudlink.core.data.model.MetricTimestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Mock data source that generates realistic heart rate data for Sprint 2 validation.
 * Simulates different activity states with appropriate BPM ranges and variability.
 */
@Singleton
class MockHealthDataSource @Inject constructor() {

    private var currentState = ActivityState.RESTING
    private var baseBpm = 72

    /**
     * Emits mock heart rate data at the specified interval.
     *
     * @param intervalMs Time between emissions in milliseconds
     */
    fun observeHeartRate(intervalMs: Long = 1000L): Flow<HeartRateData> = flow {
        while (true) {
            emit(generateHeartRate())
            delay(intervalMs)
        }
    }

    /**
     * Generate a single heart rate reading with realistic variability.
     */
    fun generateHeartRate(): HeartRateData {
        // Simulate gradual state transitions
        maybeTransitionState()

        val (minBpm, maxBpm, measurementType) = when (currentState) {
            ActivityState.RESTING -> Triple(55, 75, MeasurementType.RESTING)
            ActivityState.LIGHT_ACTIVITY -> Triple(75, 100, MeasurementType.ACTIVE)
            ActivityState.MODERATE_ACTIVITY -> Triple(100, 140, MeasurementType.ACTIVE)
            ActivityState.INTENSE_ACTIVITY -> Triple(140, 180, MeasurementType.PEAK)
            ActivityState.RECOVERY -> Triple(90, 120, MeasurementType.RECOVERY)
        }

        // Add realistic beat-to-beat variability (HRV simulation)
        val variability = Random.nextInt(-3, 4)
        baseBpm = (baseBpm + variability).coerceIn(minBpm, maxBpm)

        return HeartRateData(
            timestamp = MetricTimestamp.now(),
            source = DataSource.MOCK,
            quality = DataQuality.HIGH,
            beatsPerMinute = baseBpm,
            confidence = Random.nextFloat() * 0.2f + 0.8f, // 0.8-1.0
            measurementType = measurementType
        )
    }

    /**
     * Manually set activity state for testing specific scenarios.
     */
    fun setActivityState(state: ActivityState) {
        currentState = state
        baseBpm = when (state) {
            ActivityState.RESTING -> 65
            ActivityState.LIGHT_ACTIVITY -> 85
            ActivityState.MODERATE_ACTIVITY -> 120
            ActivityState.INTENSE_ACTIVITY -> 160
            ActivityState.RECOVERY -> 105
        }
    }

    private fun maybeTransitionState() {
        // 5% chance to transition to a different state each reading
        if (Random.nextFloat() < 0.05f) {
            currentState = when (currentState) {
                ActivityState.RESTING -> listOf(
                    ActivityState.RESTING,
                    ActivityState.RESTING,
                    ActivityState.LIGHT_ACTIVITY
                ).random()
                ActivityState.LIGHT_ACTIVITY -> listOf(
                    ActivityState.RESTING,
                    ActivityState.LIGHT_ACTIVITY,
                    ActivityState.MODERATE_ACTIVITY
                ).random()
                ActivityState.MODERATE_ACTIVITY -> listOf(
                    ActivityState.LIGHT_ACTIVITY,
                    ActivityState.MODERATE_ACTIVITY,
                    ActivityState.INTENSE_ACTIVITY
                ).random()
                ActivityState.INTENSE_ACTIVITY -> listOf(
                    ActivityState.MODERATE_ACTIVITY,
                    ActivityState.INTENSE_ACTIVITY,
                    ActivityState.RECOVERY
                ).random()
                ActivityState.RECOVERY -> listOf(
                    ActivityState.RECOVERY,
                    ActivityState.LIGHT_ACTIVITY,
                    ActivityState.RESTING
                ).random()
            }
        }
    }

    enum class ActivityState {
        RESTING,
        LIGHT_ACTIVITY,
        MODERATE_ACTIVITY,
        INTENSE_ACTIVITY,
        RECOVERY
    }
}
