"""
Модуль безопасности.

Содержит утилиты для хэширования паролей, проверки хэшей
и генерации JWT (JSON Web Tokens).
"""

from datetime import datetime, timedelta, timezone
from typing import Optional, Any, Union
from jose import jwt
from passlib.context import CryptContext
from app.core.config import settings

# Настройка контекста для хэширования паролей с использованием bcrypt
pwd_context = CryptContext(schemes=["argon2"], deprecated="auto")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """
    Проверяет, соответствует ли открытый пароль его хэшу.

    Args:
        plain_password (str): Пароль, введенный пользователем.
        hashed_password (str): Хэш пароля, сохраненный в БД.

    Returns:
        bool: True, если пароль верный, иначе False.
    """
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password: str) -> str:
    """
    Генерирует хэш для указанного пароля.

    Args:
        password (str): Пароль в открытом виде.

    Returns:
        str: Строка с хэшем.
    """
    return pwd_context.hash(password)


def create_access_token(data: dict[str, Any], expires_delta: Optional[timedelta] = None) -> str:
    """
    Создает закодированный JWT токен доступа.

    Args:
        data (dict[str, Any]): Данные (payload), которые нужно поместить в токен.
        expires_delta (Optional[timedelta]): Время жизни токена.
            Если не указано, используется значение из настроек.

    Returns:
        str: Строка JWT токена.
    """
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(minutes=settings.access_token_expire_minutes)
    
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, settings.secret_key, algorithm=settings.algorithm)
    return encoded_jwt