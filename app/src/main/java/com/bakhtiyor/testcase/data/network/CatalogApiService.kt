package com.bakhtiyor.testcase.data.network

import com.bakhtiyor.testcase.data.model.CatalogItemDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApiService {
    @GET("items")
    suspend fun getItems(
        @Query("since_id") sinceId: String? = null,
        @Query("max_id") maxId: String? = null,
    ): List<CatalogItemDto>
} 
