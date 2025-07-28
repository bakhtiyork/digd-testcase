package com.bakhtiyor.testcase.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bakhtiyor.testcase.data.database.CatalogDatabase
import com.bakhtiyor.testcase.data.mapper.toDomain
import com.bakhtiyor.testcase.data.network.CatalogApiService
import com.bakhtiyor.testcase.data.paging.CatalogRemoteMediator
import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
class CatalogRepositoryImpl(
    private val apiService: CatalogApiService,
    private val database: CatalogDatabase,
) : CatalogRepository {
    override fun getCatalogItems(): Flow<PagingData<CatalogItem>> {
        return Pager(
            config =
                PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                ),
            remoteMediator =
                CatalogRemoteMediator(
                    apiService = apiService,
                    database = database,
                ),
            pagingSourceFactory = {
                database.catalogItemDao().getAllItemsPaged()
            },
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getItemById(id: String): CatalogItem? {
        return database.catalogItemDao().getItemById(id)?.toDomain()
    }

    override suspend fun refreshItems() { /* -> RemoteMediator */ }
} 
