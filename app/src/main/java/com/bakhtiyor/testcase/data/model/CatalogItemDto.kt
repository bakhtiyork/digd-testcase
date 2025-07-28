package com.bakhtiyor.testcase.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatalogItemDto(
    @SerialName("_id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("confidence")
    val confidence: Double,
    @SerialName("image")
    val imageUrl: String,
) 
