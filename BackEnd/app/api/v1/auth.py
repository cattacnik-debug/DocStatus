"""
API Эндпоинты для аутентификации.
"""

from datetime import timedelta
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session

from app.api import deps
from app.core import security
from app.core.config import settings
from app.db import models
from app.schemas import user as user_schema, token as token_schema

router = APIRouter()


@router.post("/register", response_model=user_schema.UserResponse)
def register(
    user_in: user_schema.UserCreate,
    db: Session = Depends(deps.get_db)
):
    """
    Регистрация нового пользователя.

    Args:
        user_in (UserCreate): Данные для регистрации (логин, пароль, ФИО).
        db (Session): Сессия БД.

    Returns:
        models.User: Созданный пользователь.

    Raises:
        HTTPException: Если пользователь с таким логином уже существует.
    """
    user = db.query(models.User).filter(models.User.username == user_in.username).first()
    if user:
        raise HTTPException(status_code=400, detail="Username already registered")
    
    new_user = models.User(
        username=user_in.username,
        full_name=user_in.full_name,
        hashed_password=security.get_password_hash(user_in.password)
    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return new_user


@router.post("/login", response_model=token_schema.Token)
def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(deps.get_db)
):
    """
    Авторизация пользователя и выдача токена.

    Args:
        form_data (OAuth2PasswordRequestForm): Данные формы (username, password).
        db (Session): Сессия БД.

    Returns:
        dict: Access Token и его тип.

    Raises:
        HTTPException: Если логин или пароль неверны.
    """
    user = db.query(models.User).filter(models.User.username == form_data.username).first()
    if not user or not security.verify_password(form_data.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
        )
    
    access_token_expires = timedelta(minutes=settings.access_token_expire_minutes)
    access_token = security.create_access_token(
        data={"sub": user.username}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}