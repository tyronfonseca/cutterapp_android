package com.tf.clasificacioncutter.data.model.cuttertable

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CutterTableDao {
    @Query("SELECT * FROM cutter_table_entity ORDER BY createdAt DESC")
    fun getAllTables(): Flow<List<CutterTableEntity>>

    @Query("SELECT * FROM cutter_table_entity ORDER BY createdAt DESC")
    suspend fun getAllTablesSync(): List<CutterTableEntity>

    @Query("SELECT * FROM cutter_table_entity WHERE isSelected = 1 LIMIT 1")
    suspend fun getActiveTable(): CutterTableEntity?

    @Query("UPDATE cutter_table_entity SET isSelected = 0")
    suspend fun clearActiveTable()

    @Update
    suspend fun update(table: CutterTableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(table: CutterTableEntity)

    @Delete
    suspend fun delete(table: CutterTableEntity)
}
