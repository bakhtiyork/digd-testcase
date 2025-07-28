package com.bakhtiyor.testcase.domain.usecase

import com.bakhtiyor.testcase.domain.model.CatalogItem
import com.bakhtiyor.testcase.domain.repository.CatalogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetItemDetailUseCaseTest {
    private lateinit var useCase: GetItemDetailUseCase
    private val repository: CatalogRepository = mockk()

    @Before
    fun setup() {
        useCase = GetItemDetailUseCase(repository)
    }

    @Test
    fun `invoke returns item from repository`() =
        runTest {
            // Given
            val itemId = "test_id"
            val expectedItem =
                CatalogItem(
                    id = itemId,
                    text = "Test Item",
                    confidence = 0.85,
                    imageUrl = "https://test.com/image.jpg",
                )
            coEvery { repository.getItemById(itemId) } returns expectedItem

            // When
            val result = useCase(itemId)

            // Then
            assertEquals(expectedItem, result)
            coVerify { repository.getItemById(itemId) }
        }

    @Test
    fun `invoke returns null when repository returns null`() =
        runTest {
            // Given
            val itemId = "non_existent_id"
            coEvery { repository.getItemById(itemId) } returns null

            // When
            val result = useCase(itemId)

            // Then
            assertNull(result)
            coVerify { repository.getItemById(itemId) }
        }
} 
