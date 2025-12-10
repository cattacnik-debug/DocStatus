package com.example.docstatus.ui.scan

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.docstatus.data.local.AppDatabase
import com.example.docstatus.data.repository.DocRepository
import com.example.docstatus.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Представляет статус верификации документа, часто визуализируемый как "светофор".
 *
 * @property color Цвет, ассоциированный со статусом.
 * @property message Человекочитаемое сообщение, описывающее статус.
 */
enum class DocStatus(val color: Color, val message: String) {
    /** Документ валиден. */
    VALID(AppColors.StatusGreen, "Документ подлинный"),

    /** Документ валиден, но требует внимания (например, истекает срок действия). */
    WARNING(AppColors.StatusYellow, "Внимание!"),

    /** Документ невалиден, отозван или просрочен. */
    INVALID(AppColors.StatusRed, "Недействителен"),

    /** Начальное состояние до начала сканирования. */
    IDLE(Color.Transparent, "")
}

/**
 * Представляет состояние UI для экрана сканирования.
 *
 * @property lastScannedCode Данные последнего отсканированного QR-кода.
 * @property status Текущий статус верификации документа.
 * @property isLoading `true`, если запрос на верификацию в процессе, иначе `false`.
 * @property details Дополнительные метаданные или детали о результате верификации.
 */
data class ScanUiState(
    val lastScannedCode: String? = null,
    val status: DocStatus = DocStatus.IDLE,
    val isLoading: Boolean = false,
    val details: String = ""
)

/**
 * ViewModel, отвечающая за управление состоянием и бизнес-логикой экрана сканирования.
 *
 * Этот класс обрабатывает данные QR-кода, взаимодействует с [DocRepository] для верификации
 * документов и обновляет состояние UI в соответствии с результатом.
 */
class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState = _uiState.asStateFlow()
    private var isScanning = false

    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "doc-status-db").build()
    private val repository = DocRepository(db.verificationDao())

    /**
     * Обрабатывает новый отсканированный QR-код.
     *
     * Для предотвращения быстрых, дублирующихся сканирований одного и того же кода, эта функция
     * включает простой механизм "анти-дребезга" (debounce). Она не будет обрабатывать новый код,
     * если сканирование уже в процессе или если новый код идентичен предыдущему.
     *
     * @param code Строковые данные, декодированные из QR-кода.
     */
    fun onCodeScanned(code: String) {
        if (isScanning || code == _uiState.value.lastScannedCode) return
        isScanning = true
        verifyDocument(code)
    }

    private fun verifyDocument(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, lastScannedCode = code)

            val (status, desc) = repository.checkAndSaveDocument(code)

            _uiState.value = _uiState.value.copy(isLoading = false, status = status, details = desc)

            delay(4000)
            resetState()
        }
    }

    /**
     * Сбрасывает состояние UI к начальному (холостому) режиму.
     */
    fun resetState() {
        _uiState.value = ScanUiState()
        isScanning = false
    }
}