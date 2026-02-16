package com.hudlink.core.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hudlink.core.storage.entity.GpsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GpsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GpsEntity>)

    @Query("SELECT * FROM gps ORDER BY epochMillis DESC LIMIT 1")
    suspend fun getLatest(): GpsEntity?

    @Query("SELECT * FROM gps ORDER BY epochMillis DESC LIMIT 1")
    fun observeLatest(): Flow<GpsEntity?>

    @Query("SELECT * FROM gps WHERE epochMillis BETWEEN :startMillis AND :endMillis ORDER BY epochMillis ASC")
    suspend fun getByTimeRange(startMillis: Long, endMillis: Long): List<GpsEntity>

    @Query("DELETE FROM gps WHERE epochMillis < :beforeMillis")
    suspend fun deleteOlderThan(beforeMillis: Long): Int

    @Query("DELETE FROM gps")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM gps")
    suspend fun count(): Int
}
