package com.bakhtiyor.testcase.di

import com.bakhtiyor.testcase.presentation.viewmodel.CatalogListViewModel
import com.bakhtiyor.testcase.presentation.viewmodel.ItemDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {

        viewModel {
            CatalogListViewModel(getCatalogItemsUseCase = get())
        }

        viewModel {
            ItemDetailViewModel(getItemDetailUseCase = get())
        }
    } 
