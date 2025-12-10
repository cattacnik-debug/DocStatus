package com.example.docstatus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Представляет учетные данные пользователя для запроса аутентификации.
 *
 * Этот data class используется (в настоящее время, в основном, для справки, так как приложение
 * использует @Field для кодирования формы) для моделирования JSON-объекта запроса на вход.
 *
 * @property username Имя пользователя.
 * @property password Пароль пользователя.
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)
