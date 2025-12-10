package com.example.docstatus.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.docstatus.data.local.AppDatabase
import com.example.docstatus.data.local.VerificationEntity
import com.example.docstatus.data.repository.DocRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel, которая предоставляет данные для экрана истории верификаций.
 *
 * Этот класс отвечает за получение списка записей о верификациях из [DocRepository]
 * и предоставление его для UI в виде [StateFlow].
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DocRepository

    /**
     * [StateFlow], который эмитит список записей истории верификаций.
     *
     * Поток настроен так, чтобы начинать эмиссию данных, когда подписчик становится активным,
     * и прекращать через 5 секунд после исчезновения последнего подписчика. Данные поставляются
     * из [DocRepository].
     */
    val history: StateFlow<List<VerificationEntity>>

    init {
        val db = Room.databaseBuilder(
            application,
            AppDatabase::class.java, "doc-status-db"
        ).build()
        repository = DocRepository(db.verificationDao())

        history = repository.getHistory().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}