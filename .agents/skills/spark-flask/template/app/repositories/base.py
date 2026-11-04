from typing import Generic, TypeVar, Type, Optional, List
from app.extensions import db

T = TypeVar("T")

class BaseRepository(Generic[T]):
    def __init__(self, model_class: Type[T]):
        self.model_class = model_class

    def get_by_id(self, item_id: int) -> Optional[T]:
        return db.session.get(self.model_class, item_id)

    def get_all(self) -> List[T]:
        return self.model_class.query.all()

    def add(self, entity: T) -> T:
        db.session.add(entity)
        db.session.commit()
        return entity

    def update(self, entity: T) -> T:
        db.session.commit()
        return entity

    def delete(self, entity: T) -> None:
        db.session.delete(entity)
        db.session.commit()

