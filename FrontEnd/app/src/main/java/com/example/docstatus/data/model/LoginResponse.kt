package com.example.docstatus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Представляет ответ сервера после успешной аутентификации пользователя.
 *
 * Этот data class моделирует JSON-объект, возвращаемый эндпоинтом входа,
 * и содержит токен доступа и его тип.
 *
 * @property accessToken JWT токен доступа, предоставленный сервером.
 * @property tokenType Тип токена (например, "Bearer").
 */
data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String
)
