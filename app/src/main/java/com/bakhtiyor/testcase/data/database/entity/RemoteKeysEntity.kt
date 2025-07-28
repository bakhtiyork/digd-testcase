package com.bakhtiyor.testcase.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey
    val itemId: String,
    val prevKey: String?,
    val nextKey: String?,
) 
