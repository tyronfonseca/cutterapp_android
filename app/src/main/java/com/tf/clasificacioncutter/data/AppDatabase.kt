package com.tf.clasificacioncutter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tf.clasificacioncutter.data.model.cutterdata.CutterDataDao
import com.tf.clasificacioncutter.data.model.cutterdata.CutterDataEntity
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableDao
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableEntity
import com.tf.clasificacioncutter.data.model.openlibrary.IsbnCacheDao
import com.tf.clasificacioncutter.data.model.openlibrary.IsbnCacheEntity

@Database(
    entities = [
        CutterDataEntity::class,
        CutterTableEntity::class,
        IsbnCacheEntity::class
    ], 
    version = 3, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cutterDataDao(): CutterDataDao
    abstract fun cutterTableDao(): CutterTableDao
    abstract fun isbnCacheDao(): IsbnCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cutter_app_database"
                ).fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
