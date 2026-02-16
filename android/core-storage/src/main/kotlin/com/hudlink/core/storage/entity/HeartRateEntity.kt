package com.hudlink.core.storage.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hudlink.core.data.model.DataQuality
import com.hudlink.core.data.model.DataSource
import com.hudlink.core.data.model.HeartRateData
import com.hudlink.core.data.model.MeasurementType
import com.hudlink.core.data.model.MetricTimestamp

@Entity(
    tableName = "heart_rate",
    indices = [Index(value = ["epochMillis"])]
)
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val epochMillis: Long,
    val monotonicNanos: Long,
    val source: String,
    val quality: String,
    val beatsPerMinute: Int,
    val confidence: Float,
    val measurementType: String
) {
    fun toDomain(): HeartRateData = HeartRateData(
        timestamp = MetricTimestamp(epochMillis, monotonicNanos),
        source = DataSource.valueOf(source),
        quality = DataQuality.valueOf(quality),
        beatsPerMinute = beatsPerMinute,
        confidence = confidence,
        measurementType = MeasurementType.valueOf(measurementType)
    )

    companion object {
        fun fromDomain(data: HeartRateData): HeartRateEntity = HeartRateEntity(
            epochMillis = data.timestamp.epochMillis,
            monotonicNanos = data.timestamp.monotonicNanos,
            source = data.source.name,
            quality = data.quality.name,
            beatsPerMinute = data.beatsPerMinute,
            confidence = data.confidence,
            measurementType = data.measurementType.name
        )
    }
}
