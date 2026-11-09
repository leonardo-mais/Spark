def test_root_redirects_to_tasks(client):
    response = client.get("/")
    assert response.status_code == 302
    assert "/tasks" in response.headers["Location"]

def test_tasks_page_renders_shell(client):
    response = client.get("/tasks")
    assert response.status_code == 200
    html = response.get_data(as_text=True)
    assert "My Tasks" in html
    assert "global-search-input" in html
    assert "m3-nav-drawer" in html
    assert "m3-bottom-nav-bar" in html

def test_create_task_via_post(client):
    response = client.post(
        "/tasks",
        data={"title": "Test New Task", "notes": "Details", "priority": "high"},
    )
    assert response.status_code == 200
    assert "HX-Trigger" in response.headers
    assert response.headers["HX-Trigger"] == "taskCreated"
    html = response.get_data(as_text=True)
    assert "Test New Task" in html

def test_toggle_task_endpoint(client, sample_task_id):
    response = client.post(f"/tasks/{sample_task_id}/toggle")
    assert response.status_code == 200
    assert "HX-Trigger" in response.headers
    html = response.get_data(as_text=True)
    assert "checked" in html

def test_delete_task_endpoint(client, sample_task_id):
    response = client.delete(f"/tasks/{sample_task_id}")
    assert response.status_code == 200
    assert response.headers["HX-Trigger"] == "taskDeleted"


def test_settings_page_contains_built_with_spark(client):
    response = client.get("/settings")
    assert response.status_code == 200
    html = response.get_data(as_text=True)
    assert "Built with Spark!" in html
    assert "Spark! Tasks" in html
    assert "spark-tasks" in html

def test_404_page_renders_m3_error_view(client):
    response = client.get("/non-existent-route")
    assert response.status_code == 404
    html = response.get_data(as_text=True)
    assert "404" in html
    assert "Page Not Found" in html

def test_manifest_json_accessible(client):
    response = client.get("/static/manifest.json")
    assert response.status_code == 200
    assert "Spark! Tasks" in response.get_data(as_text=True)
