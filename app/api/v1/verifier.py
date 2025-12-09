"""
API Эндпоинт для проверки документов (основная бизнес-логика).
"""

from datetime import datetime, timedelta, timezone
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.api import deps
from app.db import models
from app.schemas import document as doc_schema

router = APIRouter()


@router.post("/check", response_model=doc_schema.DocumentResponse)
def verify_document(
    request: doc_schema.VerifyRequest,
    current_user: models.User = Depends(deps.get_current_user),
    db: Session = Depends(deps.get_db)
):
    """
    Проверяет QR-код документа по реестру и сохраняет лог.

    Логика статусов:
    1. Красный: Не найден, отозван или просрочен.
    2. Желтый: Действителен, но срок истекает через < 7 дней.
    3. Зеленый: Полностью действителен.

    Args:
        request (VerifyRequest): Данные QR-кода.
        current_user (models.User): Кто проверяет.
        db (Session): Сессия БД.

    Returns:
        DocumentResponse: Результат проверки со статусом и сообщением.
    """
    doc_id = request.qr_code_data
    doc = db.query(models.RegistryDocument).filter(models.RegistryDocument.doc_id == doc_id).first()
    
    # Значения по умолчанию (Error case)
    status_res = models.ScanStatus.RED
    message = "Документ не найден в реестре"
    doc_type = None
    owner_name = None
    
    # Используем UTC для сравнения
    now = datetime.now(timezone.utc).replace(tzinfo=None)
    
    if doc:
        doc_type = doc.doc_type
        owner_name = doc.owner_name
        
        if doc.is_revoked:
            # status_res = models.ScanStatus.RED
            message = "ВНИМАНИЕ: Документ официально отозван"
        elif doc.expiration_date < now:
            # status_res = models.ScanStatus.RED
            message = f"Срок действия истёк ({doc.expiration_date.strftime('%d.%m.%Y')})"
        elif doc.expiration_date < now + timedelta(days=7):
            status_res = models.ScanStatus.YELLOW
            message = f"Действителен, но срок истекает {doc.expiration_date.strftime('%d.%m.%Y')}"
        else:
            status_res = models.ScanStatus.GREEN
            message = "Документ полностью действителен"
            
    # Сохранение в журнал (Audit Log)
    log_entry = models.VerificationLog(
        user_id=current_user.id,
        document_identifier=doc_id,
        status_result=status_res,
        server_message=message,
        device_info=request.device_info,
        scan_time=datetime.now(timezone.utc)
    )
    db.add(log_entry)
    db.commit()
    db.refresh(log_entry)
    
    return doc_schema.DocumentResponse(
        status=status_res,
        message=message,
        doc_type=doc_type,
        owner_name=owner_name,
        verification_id=log_entry.id,
        timestamp=log_entry.scan_time
    )