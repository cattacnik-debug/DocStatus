"""
Главная точка входа приложения TriScan.

Инициализирует FastAPI, подключает роутеры и управляет жизненным циклом
приложения (создание таблиц, заполнение тестовыми данными) через lifespan.
"""

from contextlib import asynccontextmanager
from datetime import datetime, timedelta, timezone
from fastapi import FastAPI

from app.db import models
from app.db.session import engine, SessionLocal
from app.api.v1 import auth, users, verifier


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Контекстный менеджер жизненного цикла приложения.

    Заменяет устаревшие события @app.on_event("startup") и "shutdown".
    
    1. При старте: Создает таблицы и заполняет базу тестовыми данными.
    2. yield: Передает управление приложению (запуск приема запросов).
    3. При остановке: (Опционально) очищает ресурсы.
    """
    # --- ЛОГИКА ЗАПУСКА (STARTUP) ---
    
    # 1. Создаем таблицы в БД (если их нет)
    models.Base.metadata.create_all(bind=engine)
    
    # 2. Заполняем базу тестовыми данными (Seeding)
    db = SessionLocal()
    try:
        # Проверяем, есть ли хоть один документ в реестре
        if not db.query(models.RegistryDocument).first():
            print("--- LIFESPAN: ЗАПОЛНЕНИЕ БАЗЫ ТЕСТОВЫМИ ДАННЫМИ ---")
            
            # Документ 1: Действителен (Зеленый)
            db.add(models.RegistryDocument(
                doc_id="DOC-001", 
                doc_type="Паспорт", 
                owner_name="Иванов И.И.",
                expiration_date=datetime.now(timezone.utc) + timedelta(days=365)
            ))
            
            # Документ 2: Скоро истекает (Желтый)
            db.add(models.RegistryDocument(
                doc_id="DOC-002", 
                doc_type="Справка", 
                owner_name="Петров П.П.",
                expiration_date=datetime.now(timezone.utc) + timedelta(days=3)
            ))

            # Документ 3: Отозван (Красный)
            db.add(models.RegistryDocument(
                doc_id="DOC-003", 
                doc_type="Лицензия", 
                owner_name="Сидоров С.С.",
                expiration_date=datetime.now(timezone.utc) + timedelta(days=100), 
                is_revoked=True
            ))
            
            db.commit()
            print("--- LIFESPAN: ДАННЫЕ УСПЕШНО ЗАГРУЖЕНЫ ---")
    except Exception as e:
        print(f"--- LIFESPAN ERROR: {e} ---")
    finally:
        db.close()

    # Передача управления приложению
    yield
    
    # --- ЛОГИКА ЗАВЕРШЕНИЯ (SHUTDOWN) ---
    # Здесь можно закрыть соединения с Redis, Kafka и т.д.
    print("--- LIFESPAN: ЗАВЕРШЕНИЕ РАБОТЫ СЕРВЕРА ---")


# Инициализация приложения с передачей lifespan
app = FastAPI(
    title="TriScan Modular API",
    description="Бэкенд мобильного приложения для верификации документов.",
    version="2.1.0",
    lifespan=lifespan
)

# Подключение роутеров API v1
app.include_router(auth.router, prefix="/api/v1/auth", tags=["Auth"])
app.include_router(users.router, prefix="/api/v1/users", tags=["Users"])
app.include_router(verifier.router, prefix="/api/v1/verify", tags=["Scanner"])


@app.get("/")
def health_check():
    """Проверка работоспособности сервера."""
    return {
        "status": "active", 
        "system": "DocStatus API", 
        "time": datetime.now(timezone.utc)
    }