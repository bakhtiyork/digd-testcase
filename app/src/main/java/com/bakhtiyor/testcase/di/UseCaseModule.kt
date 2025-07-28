package com.bakhtiyor.testcase.di

import com.bakhtiyor.testcase.domain.usecase.GetCatalogItemsUseCase
import com.bakhtiyor.testcase.domain.usecase.GetItemDetailUseCase
import org.koin.dsl.module

val useCaseModule =
    module {

        factory {
            GetCatalogItemsUseCase(repository = get())
        }

        factory {
            GetItemDetailUseCase(repository = get())
        }
    } 
