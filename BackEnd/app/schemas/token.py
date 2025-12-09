"""
Pydantic схемы для работы с токенами.
"""

from pydantic import BaseModel
from typing import Optional


class Token(BaseModel):
    """
    Схема ответа с JWT токеном.

    Attributes:
        access_token (str): Токен доступа.
        token_type (str): Тип токена (Bearer).
    """
    access_token: str
    token_type: str


class TokenData(BaseModel):
    """
    Данные, извлекаемые из токена.

    Attributes:
        username (Optional[str]): Логин пользователя.
    """
    username: Optional[str] = None