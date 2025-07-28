package com.bakhtiyor.testcase.data.database.dao

import androidx.room.*
import com.bakhtiyor.testcase.data.database.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Query("SELECT * FROM remote_keys WHERE itemId = :itemId")
    suspend fun remoteKeyByItemId(itemId: String): RemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(keys: List<RemoteKeysEntity>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
} 
