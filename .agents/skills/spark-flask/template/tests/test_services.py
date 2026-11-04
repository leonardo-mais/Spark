import pytest
from app.services.task_service import TaskService
from app.models.task import Task

def test_create_task_success(app):
    with app.app_context():
        service = TaskService()
        task, error = service.create_task(
            title="Buy Groceries",
            notes="Milk, Eggs, Bread",
            due_date="2026-11-10",
            priority="high",
        )
        assert error is None
        assert task is not None
        assert task.id is not None
        assert task.title == "Buy Groceries"
        assert task.is_completed is False

def test_create_task_empty_title_fails(app):
    with app.app_context():
        service = TaskService()
        task, error = service.create_task(title="   ")
        assert task is None
        assert "cannot be empty" in error

def test_toggle_completion(app, sample_task_id):
    with app.app_context():
        service = TaskService()
        task, error = service.toggle_completion(sample_task_id)
        assert error is None
        assert task.is_completed is True

        # Toggle back
        task, error = service.toggle_completion(sample_task_id)
        assert error is None
        assert task.is_completed is False

def test_search_tasks(app):
    with app.app_context():
        service = TaskService()
        service.create_task(title="Plan Android release")
        service.create_task(title="Write Spark documentation")
        service.create_task(title="Review pull requests")

        results = service.list_tasks(query="Spark")
        assert len(results) == 1
        assert results[0].title == "Write Spark documentation"

def test_delete_task(app, sample_task_id):
    with app.app_context():
        service = TaskService()
        success = service.delete_task(sample_task_id)
        assert success is True
        assert service.get_task(sample_task_id) is None


def test_task_stats(app):
    with app.app_context():
        service = TaskService()
        t1, _ = service.create_task(title="Task 1")
        t2, _ = service.create_task(title="Task 2")
        service.toggle_completion(t1.id)

        stats = service.get_stats()
        assert stats["total"] == 2
        assert stats["completed"] == 1
        assert stats["active"] == 1
