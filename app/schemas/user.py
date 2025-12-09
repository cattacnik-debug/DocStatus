"""
Pydantic схемы для пользователей.
"""

from pydantic import BaseModel
from datetime import datetime


class UserBase(BaseModel):
    """
    Базовые поля пользователя.

    Attributes:
        username (str): Логин.
        full_name (str): ФИО.
    """
    username: str
    full_name: str


class UserCreate(UserBase):
    """
    Схема для создания пользователя (регистрации).

    Attributes:
        password (str): Пароль в открытом виде.
    """
    password: str


class UserResponse(UserBase):
    """
    Схема ответа с данными пользователя (безопасная).

    Attributes:
        id (int): ID пользователя.
        is_active (bool): Статус активности.
        created_at (datetime): Дата создания.
    """
    id: int
    is_active: bool
    created_at: datetime

    class Config:
        from_attributes = True