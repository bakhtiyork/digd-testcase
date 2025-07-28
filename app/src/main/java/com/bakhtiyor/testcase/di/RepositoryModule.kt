package com.bakhtiyor.testcase.di

import com.bakhtiyor.testcase.data.repository.CatalogRepositoryImpl
import com.bakhtiyor.testcase.domain.repository.CatalogRepository
import org.koin.dsl.module

val repositoryModule =
    module {

        single<CatalogRepository> {
            CatalogRepositoryImpl(
                apiService = get(),
                database = get(),
            )
        }
    } 
