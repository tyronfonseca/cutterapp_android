package com.tf.clasificacioncutter.data.model.openlibrary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "isbn_cache")
data class IsbnCacheEntity(
    @PrimaryKey val isbn: String,
    val jsonResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)
