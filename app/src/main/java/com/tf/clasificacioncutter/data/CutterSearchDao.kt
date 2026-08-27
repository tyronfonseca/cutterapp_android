package com.tf.clasificacioncutter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CutterSearchDao {
    @Query("SELECT * FROM cutter_searches ORDER BY timestamp DESC")
    fun getAllSearches(): Flow<List<CutterSearch>>

    @Insert
    suspend fun insert(search: CutterSearch)

    @Query("DELETE FROM cutter_searches WHERE id IN (:ids)")
    suspend fun deleteSearches(ids: List<Long>)

    @Query("DELETE FROM cutter_searches")
    suspend fun deleteAllSearches()
}
