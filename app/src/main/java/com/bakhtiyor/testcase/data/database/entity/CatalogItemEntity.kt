package com.bakhtiyor.testcase.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_items")
data class CatalogItemEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val confidence: Double,
    val imageUrl: String,
    val createdAt: Long = System.currentTimeMillis(),
) 
