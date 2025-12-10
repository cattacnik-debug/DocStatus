package com.example.docstatus.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Управляет данными сессии пользователя, такими как токен аутентификации, используя [SharedPreferences].
 *
 * Этот класс предоставляет простой и персистентный способ для хранения и извлечения
 * токена пользователя, а также для проверки его валидности на основе предопределенного
 * времени жизни.
 *
 * @param context Контекст приложения, используемый для доступа к [SharedPreferences].
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DocStatus_Prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val TOKEN_TIMESTAMP = "token_timestamp"
        private const val TOKEN_VALIDITY_DURATION_MS = 30 * 60 * 1000
    }

    /**
     * Сохраняет токен аутентификации и текущую временную метку в [SharedPreferences].
     *
     * @param token Токен аутентификации для сохранения.
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putLong(TOKEN_TIMESTAMP, System.currentTimeMillis())
        editor.apply()
    }

    /**
     * Извлекает сохраненный токен аутентификации.
     *
     * @return Токен аутентификации или `null`, если он не найден.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Проверяет, является ли сохраненный токен аутентификации все еще валидным.
     *
     * Токен считается валидным, если он существует и был сохранен менее чем
     * [TOKEN_VALIDITY_DURATION_MS] миллисекунд назад.
     *
     * @return `true`, если токен валиден, иначе `false`.
     */
    fun isTokenValid(): Boolean {
        val token = fetchAuthToken()
        if (token == null) {
            return false
        }

        val timestamp = prefs.getLong(TOKEN_TIMESTAMP, 0)
        if (timestamp == 0L) {
            return false
        }

        return (System.currentTimeMillis() - timestamp) < TOKEN_VALIDITY_DURATION_MS
    }
    
    /**
     * Очищает все данные аутентификации (токен и временную метку) из [SharedPreferences].
     */
    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(TOKEN_TIMESTAMP)
        editor.apply()
    }
}