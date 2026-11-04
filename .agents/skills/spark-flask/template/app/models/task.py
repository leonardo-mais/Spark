from app.extensions import db
from app.models.base import BaseModel

class Task(BaseModel):
    __tablename__ = "tasks"

    title = db.Column(db.String(255), nullable=False)
    notes = db.Column(db.Text, nullable=True)
    is_completed = db.Column(db.Boolean, default=False, nullable=False)
    due_date = db.Column(db.String(50), nullable=True)
    priority = db.Column(db.String(20), default="medium", nullable=False)

    def to_dict(self):
        return {
            "id": self.id,
            "title": self.title,
            "notes": self.notes,
            "is_completed": self.is_completed,
            "due_date": self.due_date,
            "priority": self.priority,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None,
        }

