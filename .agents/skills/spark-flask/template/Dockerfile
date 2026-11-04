FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    SPARK_FLASK_PORT=8080

# Create non-root user
RUN groupadd -r sparkuser && useradd -r -g sparkuser -d /app -s /sbin/nologin sparkuser

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application files
COPY . .

# Setup data directory with correct permissions for SQLite database volume
RUN mkdir -p /app/data && chown -R sparkuser:sparkuser /app

USER sparkuser

EXPOSE ${SPARK_FLASK_PORT}

CMD ["sh", "-c", "gunicorn --bind 0.0.0.0:${SPARK_FLASK_PORT} --workers 2 --threads 4 'run:app'"]

