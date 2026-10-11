package com.tf.clasificacioncutter.data.model.cutterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cutter_data")
data class CutterDataEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val code: String,

    @ColumnInfo(name = "author_name")
    var authorName: String = "",

    @ColumnInfo(name = "author_surname")
    var authorSurname: String = "",

    val isbn: String = "",

    @ColumnInfo(name = "book_name")
    val bookName: String = "",

    val ddcs: List<String> = emptyList(),

    @ColumnInfo(name = "ddc_selected")
    val ddcSelected: String = "",

    @ColumnInfo(name = "needs_review")
    val needsReview: Boolean = false,

    @ColumnInfo(name = "dont_separate_name")
    var dontSeparateName: Boolean = false,

    @ColumnInfo(name = "ignore_articles")
    var ignoreArticles: Boolean = true,

    @ColumnInfo(name = "use_format_mac")
    var useFormatMac: Boolean = true,

    val timestamp: Long = System.currentTimeMillis()
)
