package com.hudlink.app

import com.hudlink.core.data.model.GpsData
import com.hudlink.core.data.model.HeadingData
import com.hudlink.core.data.model.HeartRateData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

/**
 * UI model for debug log entries displayed in RecyclerView.
 */
data class LogEntry(
    val id: Long,
    val timestamp: String,
    val type: LogType,
    val message: String
) {
    enum class LogType {
        HEART_RATE,
        GPS,
        HEADING
    }

    companion object {
        // Thread-safe ID counter
        private val idCounter = AtomicLong(0L)

        // Thread-safe date formatter (Java 8 Time API)
        private val timeFormatter: DateTimeFormatter = DateTimeFormatter
            .ofPattern("HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault())

        private fun formatTimestamp(epochMillis: Long): String =
            timeFormatter.format(Instant.ofEpochMilli(epochMillis))

        fun fromHeartRate(data: HeartRateData): LogEntry = LogEntry(
            id = idCounter.getAndIncrement(),
            timestamp = formatTimestamp(data.timestamp.epochMillis),
            type = LogType.HEART_RATE,
            message = "${data.beatsPerMinute} BPM (${data.measurementType.name.lowercase()})"
        )

        fun fromGps(data: GpsData): LogEntry = LogEntry(
            id = idCounter.getAndIncrement(),
            timestamp = formatTimestamp(data.timestamp.epochMillis),
            type = LogType.GPS,
            message = "%.4f, %.4f @ %.1f km/h".format(
                data.latitude,
                data.longitude,
                data.speedKph
            )
        )

        fun fromHeading(data: HeadingData): LogEntry = LogEntry(
            id = idCounter.getAndIncrement(),
            timestamp = formatTimestamp(data.timestamp.epochMillis),
            type = LogType.HEADING,
            message = "${data.cardinalDirection.abbreviation} (${data.azimuthDegrees.toInt()}°)"
        )
    }
}
