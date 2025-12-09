"""
SQLAlchemy модели данных.

Описывает структуру таблиц в базе данных.
"""

from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Enum
from sqlalchemy.orm import relationship, declarative_base
from datetime import datetime, timezone
import enum

Base = declarative_base()


class ScanStatus(str, enum.Enum):
    """
    Перечисление возможных статусов проверки документа.

    Attributes:
        GREEN (str): Документ действителен.
        YELLOW (str): Документ действителен, но требует внимания (истекает срок).
        RED (str): Документ недействителен (отозван, просрочен или не найден).
    """
    GREEN = "green"
    YELLOW = "yellow"
    RED = "red"


class User(Base):
    """
    Модель пользователя (сотрудника).

    Attributes:
        id (int): Уникальный идентификатор (PK).
        full_name (str): Полное имя.
        username (str): Логин (Unique).
        hashed_password (str): Хэш пароля.
        is_active (bool): Флаг активности учетной записи.
        created_at (datetime): Дата регистрации.
        logs (list[VerificationLog]): История проверок сотрудника.
    """
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String, nullable=False)
    username = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=False)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=lambda: datetime.now(timezone.utc))

    logs = relationship("VerificationLog", back_populates="user")


class VerificationLog(Base):
    """
    Модель журнала верификаций.

    Attributes:
        id (int): Уникальный идентификатор записи (PK).
        user_id (int): ID сотрудника, выполнившего проверку (FK).
        document_identifier (str): Данные из QR-кода.
        status_result (ScanStatus): Результат проверки (светофор).
        server_message (str): Сообщение от сервера.
        scan_time (datetime): Время проверки.
        device_info (str): Информация об устройстве.
        user (User): Объект пользователя.
    """
    __tablename__ = "verification_logs"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    document_identifier = Column(String, index=True)
    status_result = Column(Enum(ScanStatus), nullable=False)
    server_message = Column(String)
    scan_time = Column(DateTime, default=lambda: datetime.now(timezone.utc))
    device_info = Column(String, nullable=True)

    user = relationship("User", back_populates="logs")


class RegistryDocument(Base):
    """
    Модель документа в реестре (Эталонная база).

    Attributes:
        doc_id (str): ID документа (PK).
        doc_type (str): Тип документа.
        owner_name (str): Владелец.
        expiration_date (datetime): Срок действия.
        is_revoked (bool): Флаг отзыва документа.
    """
    __tablename__ = "documents"

    doc_id = Column(String, primary_key=True, index=True)
    doc_type = Column(String)
    owner_name = Column(String)
    expiration_date = Column(DateTime)
    is_revoked = Column(Boolean, default=False)