package com.tf.clasificacioncutter.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvDate
import com.opencsv.bean.CsvIgnore
import java.util.Date

@Entity(tableName = "cutter_searches")
data class CutterSearch(
    @PrimaryKey(autoGenerate = true)
    @CsvIgnore
    val id: Long = 0,

    @CsvBindByName(column = "Original Search")
    val originalSearch: String,

    @CsvBindByName(column = "Result")
    val result: String,

    @CsvBindByName(column = "Cutter Used")
    val cutterUsedText: String,

    @CsvBindByName(column = "Timestamp")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    val timestamp: Date = Date()
)
