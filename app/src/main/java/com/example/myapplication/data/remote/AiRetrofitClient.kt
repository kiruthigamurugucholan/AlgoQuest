package com.example.myapplication.data.remote

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Dedicated Retrofit client for the Python Flask + Ollama AI Ally backend.
 *
 * Configured with extended timeouts suited for slow CPU model inference and independent
 * of the PHP/MySQL backend's RetrofitClient.
 */
object AiRetrofitClient {

    private var retrofit: Retrofit? = null
    private var apiServiceInstance: AiApiService? = null
    private var okHttpClient: OkHttpClient? = null
    private var lastUsedBaseUrl: String? = null

    fun resetSession() {
        try {
            okHttpClient?.dispatcher?.cancelAll()
            okHttpClient?.connectionPool?.evictAll()
        } catch (_: Exception) {}
        apiServiceInstance = null
        retrofit = null
        okHttpClient = null
        lastUsedBaseUrl = null
    }

    fun getAiApiService(): AiApiService {
        val currentBaseUrl = AiConfig.getResolvedBaseUrl()
        if (apiServiceInstance != null && currentBaseUrl == lastUsedBaseUrl) {
            return apiServiceInstance!!
        }

        return synchronized(this) {
            if (apiServiceInstance != null && currentBaseUrl == lastUsedBaseUrl) {
                apiServiceInstance!!
            } else {
                buildRetrofit(currentBaseUrl).create(AiApiService::class.java).also {
                    apiServiceInstance = it
                    lastUsedBaseUrl = currentBaseUrl
                }
            }
        }
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(AiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(AiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(AiConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(AiConfig.CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(2, 60, TimeUnit.SECONDS))
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()

        okHttpClient = client

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .also {
                retrofit = it
            }
    }
}
