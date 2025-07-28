package com.bakhtiyor.testcase.domain.usecase

import androidx.paging.PagingData
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow

class GetCatalogItemsUseCase(
    private val repository: CatalogRepository,
) {
    operator fun invoke(): Flow<PagingData<CatalogItem>> {
        return repository.getCatalogItems()
    }
} 
