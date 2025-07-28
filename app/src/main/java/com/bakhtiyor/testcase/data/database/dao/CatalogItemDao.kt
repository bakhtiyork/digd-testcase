package com.bakhtiyor.testcase.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.bakhtiyor.testcase.data.database.entity.CatalogItemEntity

@Dao
interface CatalogItemDao {
    @Query("SELECT * FROM catalog_items ORDER BY id DESC")
    fun getAllItemsPaged(): PagingSource<Int, CatalogItemEntity>

    @Query("SELECT * FROM catalog_items WHERE id = :id")
    suspend fun getItemById(id: String): CatalogItemEntity?

    @Upsert
    suspend fun insertItems(items: List<CatalogItemEntity>)

    @Query("DELETE FROM catalog_items")
    suspend fun clearAll()
} 
