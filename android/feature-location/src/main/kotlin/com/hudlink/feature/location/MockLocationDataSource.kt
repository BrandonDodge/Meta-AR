package com.hudlink.feature.location

import com.hudlink.core.data.model.DataQuality
import com.hudlink.core.data.model.DataSource
import com.hudlink.core.data.model.GpsData
import com.hudlink.core.data.model.HeadingData
import com.hudlink.core.data.model.MetricTimestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Mock data source that generates realistic GPS and heading data for Sprint 2 validation.
 * Simulates movement along a path with realistic speed and heading changes.
 */
@Singleton
class MockLocationDataSource @Inject constructor() {

    // Start at University of Kansas campus (Lawrence, KS)
    private var currentLat = 38.9543
    private var currentLon = -95.2558
    private var currentBearing = 0f
    private var currentSpeed = 0f
    private var currentAzimuth = 0f

    /**
     * Emits mock GPS data at the specified interval.
     * Simulates movement with realistic position changes.
     */
    fun observeGps(intervalMs: Long = 1000L): Flow<GpsData> = flow {
        while (true) {
            emit(generateGps())
            delay(intervalMs)
        }
    }

    /**
     * Emits mock heading data at the specified interval.
     */
    fun observeHeading(intervalMs: Long = 100L): Flow<HeadingData> = flow {
        while (true) {
            emit(generateHeading())
            delay(intervalMs)
        }
    }

    /**
     * Generate a single GPS reading with simulated movement.
     */
    fun generateGps(): GpsData {
        // Simulate gradual speed changes (walking/running/stopped)
        currentSpeed = (currentSpeed + Random.nextFloat() * 2f - 1f)
            .coerceIn(0f, 5f) // 0-5 m/s (0-18 km/h, walking to jogging)

        // Simulate gradual bearing changes
        currentBearing = (currentBearing + Random.nextFloat() * 10f - 5f).mod(360f)

        // Move position based on speed and bearing
        if (currentSpeed > 0.1f) {
            val distanceMeters = currentSpeed // Distance traveled in 1 second
            val bearingRadians = Math.toRadians(currentBearing.toDouble())

            // Approximate meters to degrees (varies by latitude)
            val metersPerDegreeLat = 111320.0
            val metersPerDegreeLon = 111320.0 * cos(Math.toRadians(currentLat))

            currentLat += (distanceMeters * cos(bearingRadians)) / metersPerDegreeLat
            currentLon += (distanceMeters * sin(bearingRadians)) / metersPerDegreeLon
        }

        return GpsData(
            timestamp = MetricTimestamp.now(),
            source = DataSource.MOCK,
            quality = DataQuality.HIGH,
            latitude = currentLat,
            longitude = currentLon,
            altitude = 280.0 + Random.nextDouble() * 2.0, // ~280m elevation in Lawrence
            speedMps = currentSpeed,
            bearingDegrees = currentBearing,
            horizontalAccuracyMeters = Random.nextFloat() * 5f + 3f // 3-8m accuracy
        )
    }

    /**
     * Generate a single heading reading with device orientation.
     */
    fun generateHeading(): HeadingData {
        // Simulate gradual heading changes with some noise
        currentAzimuth = (currentAzimuth + Random.nextFloat() * 2f - 1f).mod(360f)

        return HeadingData(
            timestamp = MetricTimestamp.now(),
            source = DataSource.MOCK,
            quality = DataQuality.HIGH,
            azimuthDegrees = currentAzimuth,
            pitchDegrees = Random.nextFloat() * 10f - 5f, // -5 to 5 degrees
            rollDegrees = Random.nextFloat() * 6f - 3f,   // -3 to 3 degrees
            magneticDeclinationDegrees = 3.5f // Approximate for Kansas
        )
    }

    /**
     * Set a specific starting location for testing.
     */
    fun setLocation(latitude: Double, longitude: Double) {
        currentLat = latitude
        currentLon = longitude
    }

    /**
     * Set movement parameters for testing specific scenarios.
     */
    fun setMovement(speedMps: Float, bearingDegrees: Float) {
        currentSpeed = speedMps.coerceIn(0f, 20f)
        currentBearing = bearingDegrees.mod(360f)
    }
}
