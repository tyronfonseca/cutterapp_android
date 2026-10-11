package com.tf.clasificacioncutter.data.repository

import android.content.Context
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableDao
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableEntity
import java.util.UUID

class CutterTableRepository(private val dao: CutterTableDao) {
    suspend fun ensureDefaultTablesExist(context: Context): CutterTableEntity? {
        val allTables = dao.getAllTablesSync()
        if (allTables.isEmpty()) {
            val sanborn = CutterTableEntity(
                name = context.getString(R.string.title_sanborn),
                description = context.getString(R.string.description_sanborn),
                filename = "cutter_normal.csv",
                hasHeaders = true,
                isSelected = true,
                cannotDelete = true
            )
            val pha = CutterTableEntity(
                name = context.getString(R.string.title_pha),
                description = context.getString(R.string.description_pha),
                filename = "pha.csv",
                hasHeaders = true,
                isSelected = false,
                cannotDelete = true
            )
            dao.insert(sanborn)
            dao.insert(pha)
            return sanborn
        }

        return dao.getActiveTable()
    }

    suspend fun setActiveTable(table: CutterTableEntity) {
        dao.clearActiveTable()
        dao.update(table.copy(isSelected = true))
    }

    suspend fun saveCustomTableMetadata(
        name: String,
        description: String,
        uniqueFilename: String,
        hasHeaders: Boolean
    ): CutterTableEntity {
        val newTable = CutterTableEntity(
            id = UUID.randomUUID(),
            name = name,
            description = description,
            filename = uniqueFilename,
            hasHeaders = hasHeaders,
            isSelected = false,
            cannotDelete = false
        )
        dao.insert(newTable)
        return newTable
    }
}
