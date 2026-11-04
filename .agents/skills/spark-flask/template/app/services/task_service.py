from typing import List, Optional, Tuple
from app.models.task import Task
from app.repositories.task_repository import TaskRepository

class TaskService:
    def __init__(self, repository: Optional[TaskRepository] = None):
        self.repository = repository or TaskRepository()

    def list_tasks(self, query: Optional[str] = None) -> List[Task]:
        if query:
            return self.repository.search_by_title(query.strip())
        return self.repository.get_all_ordered()

    def get_task(self, task_id: int) -> Optional[Task]:
        return self.repository.get_by_id(task_id)

    def create_task(
        self, title: str, notes: Optional[str] = None, due_date: Optional[str] = None, priority: str = "medium"
    ) -> Tuple[Optional[Task], Optional[str]]:
        title = (title or "").strip()
        if not title:
            return None, "Task title cannot be empty."

        task = Task(
            title=title,
            notes=notes.strip() if notes else None,
            due_date=due_date.strip() if due_date else None,
            priority=priority or "medium",
            is_completed=False,
        )
        return self.repository.add(task), None

    def update_task(
        self,
        task_id: int,
        title: Optional[str] = None,
        notes: Optional[str] = None,
        due_date: Optional[str] = None,
        priority: Optional[str] = None,
    ) -> Tuple[Optional[Task], Optional[str]]:
        task = self.repository.get_by_id(task_id)
        if not task:
            return None, "Task not found."

        if title is not None:
            clean_title = title.strip()
            if not clean_title:
                return None, "Task title cannot be empty."
            task.title = clean_title

        if notes is not None:
            task.notes = notes.strip() if notes else None

        if due_date is not None:
            task.due_date = due_date.strip() if due_date else None

        if priority is not None:
            task.priority = priority

        return self.repository.update(task), None

    def toggle_completion(self, task_id: int) -> Tuple[Optional[Task], Optional[str]]:
        task = self.repository.get_by_id(task_id)
        if not task:
            return None, "Task not found."
        task.is_completed = not task.is_completed
        return self.repository.update(task), None

    def delete_task(self, task_id: int) -> bool:
        task = self.repository.get_by_id(task_id)
        if not task:
            return False
        self.repository.delete(task)
        return True

    def get_stats(self) -> dict:
        all_tasks = self.repository.get_all()
        completed = sum(1 for t in all_tasks if t.is_completed)
        return {
            "total": len(all_tasks),
            "completed": completed,
            "active": len(all_tasks) - completed,
        }

