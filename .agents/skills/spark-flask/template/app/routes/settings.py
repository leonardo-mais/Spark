import os
from flask import Blueprint, render_template, current_app

settings_bp = Blueprint("settings", __name__, url_prefix="/settings")

def _get_nav_destinations(active_slug="settings"):
    return [
        {"slug": "tasks", "label": "Tasks", "url": "/tasks", "icon": "check_circle", "active": active_slug == "tasks"},
        {"slug": "settings", "label": "Settings", "url": "/settings", "icon": "settings", "active": active_slug == "settings"},
    ]

@settings_bp.route("", methods=["GET"])
def index():
    spark_info = {
        "app_name": current_app.config.get("APP_NAME", "Spark! Tasks"),
        "app_slug": current_app.config.get("APP_SLUG", "spark-tasks"),
        "version": "1.0.0",
        "typography_archetype": current_app.config.get("TYPOGRAPHY_ARCHETYPE", "impact"),
        "port": os.environ.get("SPARK_FLASK_PORT", "8080"),
        "database_uri": current_app.config.get("SQLALCHEMY_DATABASE_URI", ""),
        "framework": "Flask 3.x + Jinja2 + HTMX + Material Web (@material/web)",
        "design_language": "Material 3 Expressive (Google Calendar / Tasks inspired)",
    }
    
    return render_template(
        "pages/settings.html",
        spark_info=spark_info,
        nav_destinations=_get_nav_destinations("settings"),
    )

