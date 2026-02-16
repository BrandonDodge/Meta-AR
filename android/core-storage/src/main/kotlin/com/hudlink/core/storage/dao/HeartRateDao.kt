package com.hudlink.core.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hudlink.core.storage.entity.HeartRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HeartRateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HeartRateEntity>)

    @Query("SELECT * FROM heart_rate ORDER BY epochMillis DESC LIMIT 1")
    suspend fun getLatest(): HeartRateEntity?

    @Query("SELECT * FROM heart_rate ORDER BY epochMillis DESC LIMIT 1")
    fun observeLatest(): Flow<HeartRateEntity?>

    @Query("SELECT * FROM heart_rate WHERE epochMillis BETWEEN :startMillis AND :endMillis ORDER BY epochMillis ASC")
    suspend fun getByTimeRange(startMillis: Long, endMillis: Long): List<HeartRateEntity>

    @Query("DELETE FROM heart_rate WHERE epochMillis < :beforeMillis")
    suspend fun deleteOlderThan(beforeMillis: Long): Int

    @Query("DELETE FROM heart_rate")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM heart_rate")
    suspend fun count(): Int
}
