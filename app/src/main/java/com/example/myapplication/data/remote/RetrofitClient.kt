package com.example.myapplication.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // PC LAN IP for physical device on 172.23.x.x network.
    // Replace with http://10.0.2.2/algoquest_backend/api/ if testing on Android Emulator.
    var baseUrl: String = "http://172.23.176.205/algoquest_backend/api/"

    private var retrofit: Retrofit? = null
    private var apiServiceInstance: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: buildRetrofit(context.applicationContext).create(ApiService::class.java).also {
                apiServiceInstance = it
            }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val tokenManager = TokenManager.getInstance(context)

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")

            val token = tokenManager.getToken()
            if (!token.isNullOrBlank()) {
                builder.header("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

        val safeBaseUrl = try {
            val url = baseUrl.trim()
            if (url.endsWith("/")) url else "$url/"
        } catch (_: Exception) {
            "http://172.23.176.205/algoquest_backend/api/"
        }

        return try {
            Retrofit.Builder()
                .baseUrl(safeBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (_: Exception) {
            Retrofit.Builder()
                .baseUrl("http://172.23.176.205/algoquest_backend/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }.also { retrofit = it }
    }
}
