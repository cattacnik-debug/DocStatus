package com.example.docstatus.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.pow


class ExponentialBackoffInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var tryCount = 0
        val maxRetries = 3

        while (tryCount < maxRetries) {
            try {
                response = chain.proceed(request)
                if (response.isSuccessful) return response
            } catch (e: IOException) {
            }

            tryCount++
            if (tryCount < maxRetries) {
                val backoffTime = 2.0.pow(tryCount.toDouble()).toLong() * 1000
                try {
                    Thread.sleep(backoffTime)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        return response ?: throw IOException("Не удалось выполнить запрос после $maxRetries попыток")
    }
}

/**
 *
 * - [OkHttpClient]
 * - [ExponentialBackoffInterceptor]
 * - [AuthInterceptor]
 * - [HttpLoggingInterceptor]
 */
object NetworkClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ExponentialBackoffInterceptor())
        .addInterceptor(AuthInterceptor())
        .addInterceptor(logging)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}