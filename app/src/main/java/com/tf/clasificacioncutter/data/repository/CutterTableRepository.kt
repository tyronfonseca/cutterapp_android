package com.tf.clasificacioncutter.data.repository

import android.content.Context
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableDao
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableEntity
import java.util.UUID

class CutterTableRepository(private val dao: CutterTableDao) {
    suspend fun ensureDefaultTablesExist(context: Context): CutterTableEntity? {
        val defaultFiles = listOf(
            Triple("cutter_normal.csv", context.getString(R.string.title_sanborn), context.getString(R.string.description_sanborn)),
            Triple("pha.csv", context.getString(R.string.title_pha), context.getString(R.string.description_pha)),
            Triple("cutter_old.csv", context.getString(R.string.title_cutter_old), context.getString(R.string.description_cutter_old))
        )

        val currentTables = dao.getAllTablesSync()
        for ((filename, name, desc) in defaultFiles) {
            if (currentTables.none { it.filename == filename }) {
                val entity = CutterTableEntity(
                    id = UUID.randomUUID(),
                    name = name,
                    description = desc,
                    filename = filename,
                    hasHeaders = true,
                    isSelected = false,
                    cannotDelete = true
                )
                dao.insert(entity)
            }
        }

        // Ensure at least one table is selected as active if none is selected
        val updatedTables = dao.getAllTablesSync()
        if (updatedTables.none { it.isSelected }) {
            val first = updatedTables.firstOrNull()
            if (first != null) {
                dao.update(first.copy(isSelected = true))
            }
        }

        return dao.getActiveTable() ?: dao.getAllTablesSync().firstOrNull()
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
