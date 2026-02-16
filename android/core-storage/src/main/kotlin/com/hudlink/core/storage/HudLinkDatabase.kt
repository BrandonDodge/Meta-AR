package com.hudlink.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hudlink.core.storage.dao.GpsDao
import com.hudlink.core.storage.dao.HeartRateDao
import com.hudlink.core.storage.entity.GpsEntity
import com.hudlink.core.storage.entity.HeartRateEntity

@Database(
    entities = [
        HeartRateEntity::class,
        GpsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HudLinkDatabase : RoomDatabase() {
    abstract fun heartRateDao(): HeartRateDao
    abstract fun gpsDao(): GpsDao

    companion object {
        const val DATABASE_NAME = "hudlink_db"
    }
}
