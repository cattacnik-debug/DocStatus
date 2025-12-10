package com.example.docstatus.data.network

import com.example.docstatus.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * [Interceptor], который автоматически добавляет заголовок Authorization к сетевым запросам.
 *
 * Этот перехватчик извлекает токен доступа из [TokenManager] и, если он доступен,
 * прикрепляет его как "Bearer" токен к заголовку `Authorization` исходящих запросов,
 * которые нацелены на защищенный эндпоинт (т.е. содержат "check" в пути).
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = TokenManager.accessToken

        val newRequest = if (token != null && originalRequest.url.encodedPath.contains("check")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}