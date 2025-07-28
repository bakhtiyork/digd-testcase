package com.bakhtiyor.testcase.data.repository

import com.bakhtiyor.testcase.data.database.CatalogDatabase
import com.bakhtiyor.testcase.data.database.dao.CatalogItemDao
import com.bakhtiyor.testcase.data.database.entity.CatalogItemEntity
import com.bakhtiyor.testcase.data.network.CatalogApiService
import com.bakhtiyor.testcase.domain.model.CatalogItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CatalogRepositoryImplTest {
    private lateinit var repository: CatalogRepositoryImpl
    private val apiService: CatalogApiService = mockk()
    private val database: CatalogDatabase = mockk()
    private val catalogItemDao: CatalogItemDao = mockk()

    @Before
    fun setup() {
        coEvery { database.catalogItemDao() } returns catalogItemDao
        repository = CatalogRepositoryImpl(apiService, database)
    }

    @Test
    fun `getItemById returns mapped domain model when entity exists`() =
        runTest {
            // Given
            val itemId = "test_id"
            val text = "Test Item"
            val confidence = 0.75
            val imageUrl = "https://placehold.co/512x512?text=$text"
            val entity =
                CatalogItemEntity(
                    id = itemId,
                    text = text,
                    confidence = confidence,
                    imageUrl = imageUrl,
                )
            coEvery { catalogItemDao.getItemById(itemId) } returns entity

            val result = repository.getItemById(itemId)

            val expected =
                CatalogItem(
                    id = itemId,
                    text = text,
                    confidence = confidence,
                    imageUrl = imageUrl,
                )
            assertEquals(expected, result)
        }

    @Test
    fun `getItemById returns null when entity does not exist`() =
        runTest {
            // Given
            val itemId = "non_existent_id"
            coEvery { catalogItemDao.getItemById(itemId) } returns null

            // When
            val result = repository.getItemById(itemId)

            // Then
            assertNull(result)
        }
} 
