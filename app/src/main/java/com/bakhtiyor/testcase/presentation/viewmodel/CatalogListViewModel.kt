package com.bakhtiyor.testcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.domain.usecase.GetCatalogItemsUseCase
import kotlinx.coroutines.flow.Flow

class CatalogListViewModel(
    private val getCatalogItemsUseCase: GetCatalogItemsUseCase,
) : ViewModel() {
    val catalogItems: Flow<PagingData<CatalogItem>> =
        getCatalogItemsUseCase()
            .cachedIn(viewModelScope)
} 
