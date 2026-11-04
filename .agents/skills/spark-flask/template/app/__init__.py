import os
from pathlib import Path
from flask import Flask
from app.config import Config
from app.extensions import db, migrate

def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    # Ensure SQLite directory exists
    data_dir = Path(app.config.get("DATA_DIR", "data"))
    data_dir.mkdir(parents=True, exist_ok=True)

    # Initialize extensions
    db.init_app(app)
    migrate.init_app(app, db)

    # Register blueprints
    from app.routes.main import main_bp
    from app.routes.tasks import tasks_bp
    from app.routes.settings import settings_bp

    app.register_blueprint(main_bp)
    app.register_blueprint(tasks_bp)
    app.register_blueprint(settings_bp)

    # Create tables in development/testing mode
    with app.app_context():
        db.create_all()

    return app

