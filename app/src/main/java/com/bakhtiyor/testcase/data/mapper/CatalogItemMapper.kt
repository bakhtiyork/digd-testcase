package com.bakhtiyor.testcase.data.mapper

import com.bakhtiyor.testcase.data.database.entity.CatalogItemEntity
import com.bakhtiyor.testcase.data.model.CatalogItemDto
import com.bakhtiyor.testcase.domain.model.CatalogItem

fun CatalogItemDto.toEntity(): CatalogItemEntity {
    return CatalogItemEntity(
        id = id,
        text = text,
        confidence = confidence,
        imageUrl = imageUrl,
    )
}

fun CatalogItemEntity.toDomain(): CatalogItem {
    return CatalogItem(
        id = id,
        text = text,
        confidence = confidence,
        imageUrl = imageUrl,
    )
}

fun CatalogItemDto.toDomain(): CatalogItem {
    return CatalogItem(
        id = id,
        text = text,
        confidence = confidence,
        imageUrl = imageUrl,
    )
} 
