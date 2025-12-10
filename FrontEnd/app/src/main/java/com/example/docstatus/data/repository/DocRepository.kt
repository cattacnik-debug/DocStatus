package com.example.docstatus.data.repository

import com.example.docstatus.data.local.VerificationDao
import com.example.docstatus.data.local.VerificationEntity
import com.example.docstatus.data.model.VerificationRequest
import com.example.docstatus.data.network.NetworkClient
import com.example.docstatus.ui.scan.DocStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * A repository responsible for coordinating document verification data between the network API
 * and the local database.
 *
 * This class abstracts the data sources for document verification, providing a clean API
 * for the ViewModel to interact with.
 *
 * @property dao The Data Access Object for the local verification history database.
 */
class DocRepository(
    private val dao: VerificationDao
) {
    private val api = NetworkClient.api

    /**
     * Verifies a document via the network and saves the result to the local database.
     *
     * This function performs a network request to verify the document. Upon receiving a
     * response, it maps the server status to the local [DocStatus] enum and saves a
     * [VerificationEntity] to the local Room database. It handles network exceptions by
     * logging them as a failed verification.
     *
     * @param qrCode The raw string data from the scanned QR code.
     * @return A [Pair] containing the resulting [DocStatus] and a human-readable description.
     */
    suspend fun checkAndSaveDocument(qrCode: String): Pair<DocStatus, String> = withContext(Dispatchers.IO) {
        try {
            val response = api.verifyDocument(VerificationRequest(qrCodeData = qrCode))

            val status = when (response.status.lowercase()) {
                "green" -> DocStatus.VALID
                "yellow" -> DocStatus.WARNING
                "red" -> DocStatus.INVALID
                else -> DocStatus.INVALID
            }
            val desc = response.message

            dao.insertRecord(
                VerificationEntity(
                    docId = qrCode,
                    status = status.name,
                    details = desc,
                    timestampUnix = System.currentTimeMillis()
                )
            )

            return@withContext Pair(status, desc)
        } catch (e: Exception) {
            val errorMessage = "Ошибка сети: ${e.localizedMessage}"
            dao.insertRecord(
                VerificationEntity(
                    docId = qrCode,
                    status = DocStatus.INVALID.name,
                    details = errorMessage,
                    timestampUnix = System.currentTimeMillis()
                )
            )
            return@withContext Pair(DocStatus.INVALID, errorMessage)
        }
    }

    /**
     * Retrieves the entire verification history from the local database.
     *
     * @return A [Flow] that emits a list of all [VerificationEntity] records, ordered by
     * timestamp in descending order.
     */
    fun getHistory(): Flow<List<VerificationEntity>> = dao.getHistory()
}