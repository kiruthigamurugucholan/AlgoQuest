
package com.example.myapplication.data.remote

import android.content.Context
import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Custom base URL override
    var customBaseUrl: String? = null

    private var retrofit: Retrofit? = null
    private var apiServiceInstance: ApiService? = null
    private var okHttpClient: OkHttpClient? = null

    fun resetSession() {
        try {
            okHttpClient?.dispatcher?.cancelAll()
            okHttpClient?.connectionPool?.evictAll()
        } catch (_: Exception) {}
        apiServiceInstance = null
        retrofit = null
        okHttpClient = null
    }

    fun isEmulator(): Boolean {
        return (
                Build.FINGERPRINT.startsWith("generic") ||
                        Build.FINGERPRINT.startsWith("unknown") ||
                        Build.MODEL.contains("google_sdk") ||
                        Build.MODEL.contains("Emulator") ||
                        Build.MODEL.contains("Android SDK built for x86") ||
                        Build.MANUFACTURER.contains("Genymotion") ||
                        Build.HARDWARE.contains("goldfish") ||
                        Build.HARDWARE.contains("ranchu") ||
                        Build.PRODUCT.contains("sdk_gphone") ||
                        Build.PRODUCT.contains("sdk")
                )
    }

    fun getResolvedBaseUrl(): String {
        val override = customBaseUrl?.trim()

        if (!override.isNullOrBlank()) {
            return if (override.endsWith("/")) {
                override
            } else {
                "$override/"
            }
        }

        return if (isEmulator()) {
            "http://10.0.2.2/algoquest_backend/api/"
        } else {
            "http://172.23.176.205/algoquest_backend/api/"
        }
    }

    fun getApiService(context: Context): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: buildRetrofit(
                context.applicationContext
            ).create(ApiService::class.java).also {
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

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        okHttpClient = client

        val baseUrl = getResolvedBaseUrl()

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
