"""
Pydantic схемы для процесса верификации.
"""

from pydantic import BaseModel
from typing import Optional
from datetime import datetime
from app.db.models import ScanStatus


class VerifyRequest(BaseModel):
    """
    Запрос на проверку QR-кода.

    Attributes:
        qr_code_data (str): Данные, считанные сканером.
        device_info (Optional[str]): Информация об устройстве Android.
    """
    qr_code_data: str
    device_info: Optional[str] = "Unknown Android Device"


class DocumentResponse(BaseModel):
    """
    Ответ сервера с результатами проверки.

    Attributes:
        status (ScanStatus): Статус (green/yellow/red).
        message (str): Сообщение для пользователя.
        doc_type (Optional[str]): Тип документа.
        owner_name (Optional[str]): Владелец.
        verification_id (int): ID записи в журнале (лог).
        timestamp (datetime): Время проверки.
    """
    status: ScanStatus
    message: str
    doc_type: Optional[str] = None
    owner_name: Optional[str] = None
    verification_id: int
    timestamp: datetime