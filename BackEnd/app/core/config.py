"""
Модуль конфигурации приложения.

Загружает настройки из переменных окружения (файла .env) и предоставляет
к ним доступ через типизированный объект Settings.
"""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """
    Глобальные настройки приложения.

    Attributes:
        secret_key (str): Секретный ключ для подписи JWT токенов.
        algorithm (str): Алгоритм шифрования (например, HS256).
        access_token_expire_minutes (int): Время жизни токена доступа в минутах.
        database_url (str): Строка подключения к базе данных (DSN).
    """
    secret_key: str
    algorithm: str
    access_token_expire_minutes: int
    database_url: str

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")


# Создаем единственный экземпляр настроек для использования во всем приложении
settings = Settings()