package com.example.docstatus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Представляет запрос, отправляемый на сервер для верификации документа.
 *
 * Этот data class моделирует JSON-объект запроса на верификацию, содержащий
 * данные, отсканированные с QR-кода, и опциональную информацию об устройстве.
 *
 * @property qrCodeData Необработанные строковые данные, полученные со сканера QR-кода.
 * @property deviceInfo Опциональная информация о клиентском устройстве.
 */
data class VerificationRequest(
    @SerializedName("qr_code_data")
    val qrCodeData: String,
    @SerializedName("device_info")
    val deviceInfo: String? = "Unknown Android Device"
)
