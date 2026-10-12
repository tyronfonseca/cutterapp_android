package com.tf.clasificacioncutter.data.model.openlibrary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IsbnCacheDao {
    @Query("SELECT * FROM isbn_cache WHERE isbn = :isbn LIMIT 1")
    suspend fun getCache(isbn: String): IsbnCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: IsbnCacheEntity)

    @Query("DELETE FROM isbn_cache WHERE timestamp < :expirationTimestamp")
    suspend fun deleteExpired(expirationTimestamp: Long)
}
