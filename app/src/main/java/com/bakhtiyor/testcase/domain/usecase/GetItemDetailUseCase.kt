package com.bakhtiyor.testcase.domain.usecase

import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.domain.repository.CatalogRepository

class GetItemDetailUseCase(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(id: String): CatalogItem? {
        return repository.getItemById(id)
    }
} 
