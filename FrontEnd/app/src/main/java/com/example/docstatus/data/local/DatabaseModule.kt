package com.example.docstatus.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Представляет одну запись о верификации в локальной базе данных.
 *
 * Эта сущность хранит ключевые детали каждого события верификации документа, включая
 * идентификатор документа, статус и временную метку.
 *
 * @property id Уникальный идентификатор записи в базе данных (авто-генерируемый).
 * @property docId Идентификатор отсканированного документа (например, из QR-кода).
 * @property status Результат статуса верификации (например, "VALID", "INVALID").
 * @property details Человекочитаемое сообщение, описывающее результат верификации.
 * @property timestampUnix Временная метка Unix (в миллисекундах) на момент верификации.
 */
@Entity(tableName = "verification_journal")
data class VerificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val docId: String,
    val status: String,
    val details: String,
    val timestampUnix: Long
)

/**
 * Data Access Object (DAO) для истории верификаций.
 *
 * Этот интерфейс определяет операции с базой данных для вставки и извлечения
 * записей [VerificationEntity].
 */
@Dao
interface VerificationDao {
    /**
     * Вставляет новую запись о верификации в базу данных.
     *
     * Если запись с таким же первичным ключом уже существует, она будет заменена.
     *
     * @param record [VerificationEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: VerificationEntity)

    /**
     * Извлекает все записи о верификациях из базы данных, отсортированные по временной
     * метке в убывающем порядке.
     *
     * @return [Flow], который эмитит список всех записей [VerificationEntity] всякий раз,
     * когда изменяется соответствующая таблица.
     */
    @Query("SELECT * FROM verification_journal ORDER BY timestampUnix DESC")
    fun getHistory(): Flow<List<VerificationEntity>>
}

/**
 * Основной класс базы данных Room для приложения.
 *
 * Этот класс определяет конфигурацию базы данных и служит основной точкой доступа
 * к нижележащим данным. Он включает в себя таблицу [VerificationEntity].
 */
@Database(entities = [VerificationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Предоставляет экземпляр [VerificationDao].
     */
    abstract fun verificationDao(): VerificationDao
}