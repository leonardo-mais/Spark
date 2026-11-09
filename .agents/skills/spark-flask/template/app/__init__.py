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

    # Register error handlers
    @app.errorhandler(404)
    def not_found_error(error):
        from flask import render_template
        return render_template(
            "errors/error.html",
            error_code=404,
            error_title="Page Not Found",
            error_message="The requested resource could not be found on this server.",
            nav_destinations=[
                {"slug": "tasks", "label": "Tasks", "url": "/tasks", "icon": "check_circle", "active": False},
                {"slug": "settings", "label": "Settings", "url": "/settings", "icon": "settings", "active": False},
            ],
        ), 404

    @app.errorhandler(500)
    def internal_error(error):
        from flask import render_template
        db.session.rollback()
        return render_template(
            "errors/error.html",
            error_code=500,
            error_title="Internal Server Error",
            error_message="An unexpected error occurred while processing your request.",
            nav_destinations=[
                {"slug": "tasks", "label": "Tasks", "url": "/tasks", "icon": "check_circle", "active": False},
                {"slug": "settings", "label": "Settings", "url": "/settings", "icon": "settings", "active": False},
            ],
        ), 500

    # Create tables in development/testing mode
    with app.app_context():
        db.create_all()

    return app

