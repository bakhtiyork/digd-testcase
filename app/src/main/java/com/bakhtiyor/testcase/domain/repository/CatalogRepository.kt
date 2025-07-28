package com.bakhtiyor.testcase.domain.repository

import androidx.paging.PagingData
import com.bakhtiyor.testcase.domain.model.CatalogItem
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun getCatalogItems(): Flow<PagingData<CatalogItem>>

    suspend fun getItemById(id: String): CatalogItem?

    suspend fun refreshItems()
} 
