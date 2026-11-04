from flask import Blueprint, render_template, request, jsonify, make_response
from app.services.task_service import TaskService

tasks_bp = Blueprint("tasks", __name__, url_prefix="/tasks")
service = TaskService()

def _get_nav_destinations(active_slug="tasks"):
    return [
        {"slug": "tasks", "label": "Tasks", "url": "/tasks", "icon": "check_circle", "active": active_slug == "tasks"},
        {"slug": "settings", "label": "Settings", "url": "/settings", "icon": "settings", "active": active_slug == "settings"},
    ]

@tasks_bp.route("", methods=["GET"])
def index():
    query = request.args.get("q", "")
    tasks = service.list_tasks(query=query)
    stats = service.get_stats()
    
    # If it's an HTMX search request, return just the list items fragment
    if request.headers.get("HX-Request") and request.args.get("search_only"):
        return render_template("pages/tasks/_task_items.html", tasks=tasks)

    return render_template(
        "pages/tasks/index.html",
        tasks=tasks,
        selected_task=None,
        stats=stats,
        nav_destinations=_get_nav_destinations("tasks"),
        search_query=query,
    )

@tasks_bp.route("/new", methods=["GET"])
def new_task_form():
    return render_template("pages/tasks/_task_detail.html", task=None, is_new=True)

@tasks_bp.route("/<int:task_id>", methods=["GET"])
def detail(task_id: int):
    task = service.get_task(task_id)
    if not task:
        return render_template(
            "components/_error_banner.html",
            error_message="Task not found.",
            retry_url="/tasks"
        ), 404

    # If HTMX request, render only the detail pane fragment
    if request.headers.get("HX-Request"):
        return render_template("pages/tasks/_task_detail.html", task=task, is_new=False)

    # Standard browser hit: render full shell with selected task
    tasks = service.list_tasks()
    stats = service.get_stats()
    return render_template(
        "pages/tasks/index.html",
        tasks=tasks,
        selected_task=task,
        stats=stats,
        nav_destinations=_get_nav_destinations("tasks"),
    )

@tasks_bp.route("", methods=["POST"])
def create():
    title = request.form.get("title")
    notes = request.form.get("notes")
    due_date = request.form.get("due_date")
    priority = request.form.get("priority", "medium")

    task, error = service.create_task(title=title, notes=notes, due_date=due_date, priority=priority)
    if error:
        return render_template("components/_error_banner.html", error_message=error), 400

    tasks = service.list_tasks()
    stats = service.get_stats()
    
    # Set HX-Trigger header so client can update stats badge or notify user
    response = make_response(
        render_template("pages/tasks/_task_items.html", tasks=tasks, active_id=task.id)
    )
    response.headers["HX-Trigger"] = "taskCreated"
    return response

@tasks_bp.route("/<int:task_id>/toggle", methods=["POST"])
def toggle(task_id: int):
    task, error = service.toggle_completion(task_id)
    if error or not task:
        return "", 404

    response = make_response(render_template("pages/tasks/_task_row.html", item=task))
    response.headers["HX-Trigger"] = "taskUpdated"
    return response

@tasks_bp.route("/<int:task_id>", methods=["POST"])
def update(task_id: int):
    title = request.form.get("title")
    notes = request.form.get("notes")
    due_date = request.form.get("due_date")
    priority = request.form.get("priority")

    task, error = service.update_task(
        task_id=task_id, title=title, notes=notes, due_date=due_date, priority=priority
    )
    if error or not task:
        return render_template("components/_error_banner.html", error_message=error or "Failed to update."), 400

    tasks = service.list_tasks()
    response = make_response(render_template("pages/tasks/_task_items.html", tasks=tasks, active_id=task.id))
    response.headers["HX-Trigger"] = "taskUpdated"
    return response

@tasks_bp.route("/<int:task_id>", methods=["DELETE"])
def delete(task_id: int):
    success = service.delete_task(task_id)
    if not success:
        return "", 404

    response = make_response("")
    response.headers["HX-Trigger"] = "taskDeleted"
    return response

@tasks_bp.route("/stats", methods=["GET"])
def stats():
    return jsonify(service.get_stats())

