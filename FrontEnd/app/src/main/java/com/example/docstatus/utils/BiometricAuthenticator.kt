package com.example.docstatus.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Утилитарный объект для обработки биометрической аутентификации.
 *
 * Этот объект предоставляет простой интерфейс к Android BiometricPrompt API, инкапсулируя
 * логику проверки доступности биометрии и отображения диалогового окна аутентификации.
 */
object BiometricAuthenticator {

    /**
     * Отображает системное диалоговое окно биометрической аутентификации.
     *
     * Эта функция сначала проверяет, доступна ли биометрия на устройстве.
     * Если да, она настраивает и отображает [BiometricPrompt], обрабатывая результаты
     * через предоставленные колбэки.
     *
     * @param activity [FragmentActivity], который будет хостом для диалогового окна. Это
     * является требованием BiometricPrompt API.
     * @param onSuccess Лямбда-функция, которая будет вызвана после успешной аутентификации.
     * @param onError Лямбда-функция, которая получает сообщение об ошибке, если аутентификация
     * не удалась или недоступна.
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val biometricManager = BiometricManager.from(activity)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            onError("Биометрия недоступна")
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Подтвердите личность")
            .setSubtitle("Используйте отпечаток пальца или лицо для доступа к истории")
            .setNegativeButtonText("Отмена")
            .build()

        val biometricPrompt = BiometricPrompt(activity,
            ContextCompat.getMainExecutor(activity), 
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError("Ошибка аутентификации: $errString")
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}