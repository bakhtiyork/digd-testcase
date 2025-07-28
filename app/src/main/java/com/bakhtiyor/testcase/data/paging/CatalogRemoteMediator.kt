package com.bakhtiyor.testcase.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bakhtiyor.testcase.data.database.CatalogDatabase
import com.bakhtiyor.testcase.data.database.entity.CatalogItemEntity
import com.bakhtiyor.testcase.data.database.entity.RemoteKeysEntity
import com.bakhtiyor.testcase.data.mapper.toEntity
import com.bakhtiyor.testcase.data.network.CatalogApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CatalogRemoteMediator(
    private val apiService: CatalogApiService,
    private val database: CatalogDatabase,
) : RemoteMediator<Int, CatalogItemEntity>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CatalogItemEntity>,
    ): MediatorResult {
        return try {
            val loadKey =
                when (loadType) {
                    LoadType.REFRESH -> null
                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                        remoteKeys?.prevKey
                            ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state)
                        remoteKeys?.nextKey
                            ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                }

            val items =
                when (loadType) {
                    LoadType.REFRESH -> apiService.getItems()
                    LoadType.PREPEND -> apiService.getItems(sinceId = loadKey)
                    LoadType.APPEND -> apiService.getItems(maxId = loadKey)
                }

            val endOfPaginationReached = items.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.catalogItemDao().clearAll()
                }

                val prevKey = if (loadType == LoadType.APPEND) loadKey else null
                val nextKey = if (endOfPaginationReached) null else items.lastOrNull()?.id

                val keys =
                    items.map {
                        RemoteKeysEntity(
                            itemId = it.id,
                            prevKey = prevKey,
                            nextKey = nextKey,
                        )
                    }

                database.remoteKeysDao().insertRemoteKeys(keys)
                database.catalogItemDao().insertItems(items.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CatalogItemEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { item -> database.remoteKeysDao().remoteKeyByItemId(item.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CatalogItemEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { item -> database.remoteKeysDao().remoteKeyByItemId(item.id) }
    }
} 
