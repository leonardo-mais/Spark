import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent

class Config:
    SECRET_KEY = os.environ.get("SECRET_KEY", "spark-flask-development-key")
    
    # SQLite Database stored in data directory
    DATA_DIR = os.environ.get("DATA_DIR", str(BASE_DIR / "data"))
    SQLALCHEMY_DATABASE_URI = os.environ.get(
        "DATABASE_URL", f"sqlite:///{DATA_DIR}/app.db"
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # Archetype: 'impact' (Spark Impact Flex) or 'standard' (Google Standard M3)
    TYPOGRAPHY_ARCHETYPE = os.environ.get("TYPOGRAPHY_ARCHETYPE", "impact")
    
    # App metadata
    APP_NAME = os.environ.get("APP_NAME", "Spark! Tasks")
    APP_SLUG = "spark-tasks"

class TestingConfig(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = "sqlite:///:memory:"
    WTF_CSRF_ENABLED = False

