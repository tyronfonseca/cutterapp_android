package com.tf.clasificacioncutter.data.model.cuttertable

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cutter_table_entity")
data class CutterTableEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String = "",
    val filename: String = "",
    val hasHeaders: Boolean = false,
    val isSelected: Boolean = false,
    val cannotDelete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
