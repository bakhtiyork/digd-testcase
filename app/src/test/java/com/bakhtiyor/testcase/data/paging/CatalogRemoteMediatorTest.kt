package com.bakhtiyor.testcase.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bakhtiyor.testcase.data.database.CatalogDatabase
import com.bakhtiyor.testcase.data.database.entity.CatalogItemEntity
import com.bakhtiyor.testcase.data.database.entity.RemoteKeysEntity
import com.bakhtiyor.testcase.data.model.CatalogItemDto
import com.bakhtiyor.testcase.data.network.CatalogApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CatalogRemoteMediatorTest {
    private lateinit var remoteMediator: CatalogRemoteMediator
    private lateinit var apiService: CatalogApiService
    private lateinit var database: CatalogDatabase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        apiService = mockk()
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                CatalogDatabase::class.java,
            ).build()
        remoteMediator = CatalogRemoteMediator(apiService, database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `load with REFRESH type fetches initial data and clears cache`() =
        runTest(testDispatcher) {
            // Given
            val mockItems = createMockItems()
            coEvery { apiService.getItems() } returns mockItems

            val pagingState = createEmptyPagingState()

            // When
            val result = remoteMediator.load(LoadType.REFRESH, pagingState)

            // Then
            assertSuccessResult(result, endOfPaginationReached = false)
            assertDatabaseContainsItems(2)
            assertRemoteKeysCreated(mockItems)
        }

    @Test
    fun `load with APPEND type fetches next page correctly`() =
        runTest(testDispatcher) {
            // Given
            val nextPageItems = createNextPageItems()
            coEvery { apiService.getItems(maxId = "6880ea68308d7") } returns nextPageItems

            val existingItem = createExistingItem()
            setupDatabaseWithExistingItem(existingItem)

            val pagingState = createPagingStateWithExistingItem(existingItem)

            // When
            val result = remoteMediator.load(LoadType.APPEND, pagingState)

            // Then
            assertSuccessResult(result, endOfPaginationReached = false)
            assertDatabaseContainsItems(3) // 1 existing + 2 new items
        }

    // Helper methods
    private fun createMockItems(): List<CatalogItemDto> =
        listOf(
            CatalogItemDto("6880ea6836063", "20. nzbum", 0.41, "https://placehold.co/512x512?text=20.%20nzbum"),
            CatalogItemDto("6880ea68330ea", "19. exmkl", 0.14, "https://placehold.co/512x512?text=19.%20exmkl"),
        )

    private fun createNextPageItems(): List<CatalogItemDto> =
        listOf(
            CatalogItemDto("6880ea68308d7", "18. aarjc", 0.85, "https://placehold.co/512x512?text=18.%20aarjc"),
            CatalogItemDto("6880ea682df82", "17. qefqg", 0.43, "https://placehold.co/512x512?text=17.%20qefqg"),
        )

    private fun createExistingItem(): CatalogItemEntity =
        CatalogItemEntity("6880ea68330ea", "19. exmkl", 0.14, "https://placehold.co/512x512?text=19.%20exmkl")

    private fun createEmptyPagingState(): PagingState<Int, CatalogItemEntity> =
        PagingState(
            pages = emptyList(),
            anchorPosition = null,
            config = androidx.paging.PagingConfig(10),
            leadingPlaceholderCount = 0,
        )

    private fun createPagingStateWithExistingItem(item: CatalogItemEntity): PagingState<Int, CatalogItemEntity> =
        PagingState(
            pages =
                listOf(
                    androidx.paging.PagingSource.LoadResult.Page(
                        data = listOf(item),
                        prevKey = null,
                        nextKey = 1,
                    ),
                ),
            anchorPosition = 0,
            config = androidx.paging.PagingConfig(10),
            leadingPlaceholderCount = 0,
        )

    private suspend fun setupDatabaseWithExistingItem(item: CatalogItemEntity) {
        database.catalogItemDao().insertItems(listOf(item))

        val remoteKey =
            RemoteKeysEntity(
                itemId = item.id,
                prevKey = "6880ea6836063",
                nextKey = "6880ea68308d7",
            )
        database.remoteKeysDao().insertRemoteKeys(listOf(remoteKey))
    }

    private fun assertSuccessResult(result: androidx.paging.RemoteMediator.MediatorResult, endOfPaginationReached: Boolean) {
        assertTrue("Result should be Success", result is androidx.paging.RemoteMediator.MediatorResult.Success)
        val successResult = result as androidx.paging.RemoteMediator.MediatorResult.Success
        assertEquals(
            "endOfPaginationReached should be $endOfPaginationReached",
            endOfPaginationReached,
            successResult.endOfPaginationReached,
        )
    }

    private suspend fun assertDatabaseContainsItems(expectedCount: Int) {
        val savedItems =
            database.catalogItemDao().getAllItemsPaged().load(
                androidx.paging.PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 20,
                    placeholdersEnabled = false,
                ),
            )
        val pageResult = savedItems as androidx.paging.PagingSource.LoadResult.Page
        assertEquals("Database should contain $expectedCount items", expectedCount, pageResult.data.size)
    }

    private suspend fun assertRemoteKeysCreated(items: List<CatalogItemDto>) {
        val firstItemId = items.first().id
        val remoteKeys = database.remoteKeysDao().remoteKeyByItemId(firstItemId)
        assertNotNull("Remote keys should be created for first item", remoteKeys)
        assertEquals("First page should have no previous key", null, remoteKeys?.prevKey)
        assertEquals("Next key should be last item's ID", items.last().id, remoteKeys?.nextKey)
    }
} 
