package com.tf.clasificacioncutter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cutter_searches")
data class CutterSearch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalSearch: String,
    val result: String,
    val cutterUsedText: String,
    val timestamp: Long = System.currentTimeMillis()
)
