package com.bakhtiyor.testcase.di

import androidx.room.Room
import com.bakhtiyor.testcase.data.database.CatalogDatabase
import com.bakhtiyor.testcase.data.database.dao.CatalogItemDao
import com.bakhtiyor.testcase.data.database.dao.RemoteKeysDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {

        single<CatalogDatabase> {
            Room.databaseBuilder(
                androidContext(),
                CatalogDatabase::class.java,
                CatalogDatabase.DATABASE_NAME,
            ).fallbackToDestructiveMigration(true)
                .build()
        }

        single<CatalogItemDao> {
            get<CatalogDatabase>().catalogItemDao()
        }

        single<RemoteKeysDao> {
            get<CatalogDatabase>().remoteKeysDao()
        }
    } 
