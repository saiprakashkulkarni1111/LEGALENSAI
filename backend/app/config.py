"""
LEGALENS AI - Application Configuration
Strict configuration management with environment defaults.
"""
from typing import List, Optional
import os
from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import Field


class Settings(BaseSettings):
    APP_NAME: str = "LEGALENS AI"
    APP_VERSION: str = "1.0.0"
    APP_ENV: str = "development"
    DEBUG: bool = True
    API_PORT: int = 8000
    FRONTEND_PORT: int = 3000

    # Security
    SECRET_KEY: str = Field(
        default="legalens_ai_production_grade_secret_key_change_in_prod_2026",
        description="Secret key for JWT and encryption"
    )
    JWT_SECRET: str = Field(
        default="legalens_jwt_local_dev_signing_key_32_bytes_min",
        description="JWT signature secret"
    )
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24  # 24 hours

    # Database
    DATABASE_URL: str = Field(
        default="sqlite+aiosqlite:///./legalens.db",
        description="PostgreSQL async URL or SQLite fallback"
    )
    REDIS_URL: str = "redis://localhost:6379/0"

    # Storage
    STORAGE_PROVIDER: str = "local"
    STORAGE_DIR: str = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", "data", "uploads"))
    MAX_UPLOAD_SIZE_MB: int = 25
    ALLOWED_EXTENSIONS: List[str] = ["pdf", "docx", "txt", "png", "jpg", "jpeg"]

    # AI Models - Single Unified API Key (Google Gemini)
    GOOGLE_API_KEY: Optional[str] = Field(
        default_factory=lambda: os.environ.get("GEMINI_API_KEY") or os.environ.get("GOOGLE_API_KEY"),
        description="Single API key used for all AI tasks across the platform (Google Gemini)"
    )
    DEFAULT_LLM_PROVIDER: str = "gemini"
    EMBEDDING_DIMENSION: int = 384

    # Security & Guardrails
    AUTO_REDACT_PII: bool = True
    RATE_LIMIT_PER_MINUTE: int = 120

    # Operational Modes
    REAL_DATA_MODE_DEFAULT: bool = True
    DEMO_MODE_DEFAULT: bool = False
    SYNC_INTERVAL_MINUTES: int = 60
    LIVE_VERIFICATION_TIMEOUT_SECONDS: int = 15

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")


settings = Settings()
os.makedirs(settings.STORAGE_DIR, exist_ok=True)
