package com.hudlink.core.storage.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hudlink.core.data.model.DataQuality
import com.hudlink.core.data.model.DataSource
import com.hudlink.core.data.model.GpsData
import com.hudlink.core.data.model.MetricTimestamp

@Entity(
    tableName = "gps",
    indices = [Index(value = ["epochMillis"])]
)
data class GpsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val epochMillis: Long,
    val monotonicNanos: Long,
    val source: String,
    val quality: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val speedMps: Float,
    val bearingDegrees: Float?,
    val horizontalAccuracyMeters: Float
) {
    fun toDomain(): GpsData = GpsData(
        timestamp = MetricTimestamp(epochMillis, monotonicNanos),
        source = DataSource.valueOf(source),
        quality = DataQuality.valueOf(quality),
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speedMps = speedMps,
        bearingDegrees = bearingDegrees,
        horizontalAccuracyMeters = horizontalAccuracyMeters
    )

    companion object {
        fun fromDomain(data: GpsData): GpsEntity = GpsEntity(
            epochMillis = data.timestamp.epochMillis,
            monotonicNanos = data.timestamp.monotonicNanos,
            source = data.source.name,
            quality = data.quality.name,
            latitude = data.latitude,
            longitude = data.longitude,
            altitude = data.altitude,
            speedMps = data.speedMps,
            bearingDegrees = data.bearingDegrees,
            horizontalAccuracyMeters = data.horizontalAccuracyMeters
        )
    }
}
