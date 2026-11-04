import pytest
from app import create_app
from app.config import TestingConfig
from app.extensions import db
from app.models.task import Task

@pytest.fixture
def app():
    app = create_app(config_class=TestingConfig)
    with app.app_context():
        db.create_all()
        yield app
        db.session.remove()
        db.drop_all()

@pytest.fixture
def client(app):
    return app.test_client()

@pytest.fixture
def sample_task_id(app):
    with app.app_context():
        task = Task(title="Sample Task", notes="Important notes", priority="high")
        db.session.add(task)
        db.session.commit()
        return task.id

