"""
Модуль сессии базы данных.

Отвечает за создание движка (Engine) и фабрики сессий (SessionLocal).
"""

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.core.config import settings

# Создание движка SQLAlchemy.
# connect_args={"check_same_thread": False} используется только для SQLite.
engine = create_engine(
    settings.database_url, connect_args={"check_same_thread": False}
)

# Фабрика для создания сессий базы данных.
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)