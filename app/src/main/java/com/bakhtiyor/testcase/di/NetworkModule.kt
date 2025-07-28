package com.bakhtiyor.testcase.di

import com.bakhtiyor.testcase.BuildConfig
import com.bakhtiyor.testcase.data.network.CatalogApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule =
    module {

        single<Json> {
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        single<Interceptor> {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        single<OkHttpClient> {
            OkHttpClient.Builder()
                .addInterceptor(get<Interceptor>())
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder =
                        original.newBuilder()
                            .header("Authorization", BuildConfig.AUTH_TOKEN)
                            .method(original.method, original.body)

                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        single<Retrofit> {
            Retrofit.Builder()
                .baseUrl("https://marlove.net/e/mock/v1/")
                .client(get())
                .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
                .build()
        }

        single<CatalogApiService> {
            get<Retrofit>().create(CatalogApiService::class.java)
        }
    } 
