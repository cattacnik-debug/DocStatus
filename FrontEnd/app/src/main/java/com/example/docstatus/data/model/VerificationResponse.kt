package com.example.docstatus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Представляет ответ сервера после запроса на верификацию документа.
 *
 * Этот data class моделирует JSON-объект, возвращаемый эндпоинтом верификации,
 * и содержит статус проверки, человекочитаемое сообщение и другие метаданные документа.
 *
 * @property status Код статуса верификации (например, "green", "yellow", "red").
 * @property message Сообщение от сервера, описывающее результат.
 * @property docType Тип документа, если доступен.
 * @property ownerName Имя владельца документа, если доступно.
 * @property verificationId Уникальный ID этого события верификации в логе сервера.
 * @property timestamp Временная метка верификации на стороне сервера в формате ISO 8601.
 */
data class VerificationResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("doc_type")
    val docType: String?,
    @SerializedName("owner_name")
    val ownerName: String?,
    @SerializedName("verification_id")
    val verificationId: Int,
    @SerializedName("timestamp")
    val timestamp: String
)
