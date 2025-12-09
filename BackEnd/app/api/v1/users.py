"""
API Эндпоинты для управления пользователями.
"""

from fastapi import APIRouter, Depends
from app.api import deps
from app.db import models
from app.schemas import user as user_schema

router = APIRouter()


@router.get("/me", response_model=user_schema.UserResponse)
def read_users_me(current_user: models.User = Depends(deps.get_current_user)):
    """
    Получение данных текущего авторизованного пользователя.

    Args:
        current_user (models.User): Пользователь, извлеченный из токена.

    Returns:
        models.User: Данные профиля.
    """
    return current_user