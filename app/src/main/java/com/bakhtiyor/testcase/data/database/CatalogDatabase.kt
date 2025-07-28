package com.bakhtiyor.testcase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bakhtiyor.testcase.data.database.dao.CatalogItemDao
import com.bakhtiyor.testcase.data.database.dao.RemoteKeysDao
import com.bakhtiyor.testcase.data.database.entity.CatalogItemEntity
import com.bakhtiyor.testcase.data.database.entity.RemoteKeysEntity

@Database(
    entities = [CatalogItemEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun catalogItemDao(): CatalogItemDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "catalog_database"
    }
} 
