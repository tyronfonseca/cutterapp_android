package com.tf.clasificacioncutter.data.model.cutterdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

import androidx.room.Update

@Dao
interface CutterDataDao {
    @Query("SELECT * FROM cutter_data ORDER BY timestamp DESC")
    fun getAllSearches(): Flow<List<CutterDataEntity>>

    @Insert
    suspend fun insert(search: CutterDataEntity)

    @Update
    suspend fun update(search: CutterDataEntity)

    @Query("DELETE FROM cutter_data WHERE id IN (:ids)")
    suspend fun deleteSearches(ids: List<UUID>)

    @Query("DELETE FROM cutter_data")
    suspend fun deleteAllSearches()
}