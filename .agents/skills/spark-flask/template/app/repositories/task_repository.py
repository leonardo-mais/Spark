from typing import List, Optional
from app.models.task import Task
from app.repositories.base import BaseRepository

class TaskRepository(BaseRepository[Task]):
    def __init__(self):
        super().__init__(Task)

    def get_all_ordered(self) -> List[Task]:
        return Task.query.order_by(Task.is_completed.asc(), Task.id.desc()).all()

    def search_by_title(self, query: str) -> List[Task]:
        if not query:
            return self.get_all_ordered()
        return Task.query.filter(Task.title.ilike(f"%{query}%")).all()

    def get_active(self) -> List[Task]:
        return Task.query.filter_by(is_completed=False).order_by(Task.id.desc()).all()

    def get_completed(self) -> List[Task]:
        return Task.query.filter_by(is_completed=True).order_by(Task.id.desc()).all()

