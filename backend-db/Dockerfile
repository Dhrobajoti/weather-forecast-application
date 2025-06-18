# Stage 1: Build dependencies
FROM python:3.9-slim AS builder

# Install system build dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc python3-dev libffi-dev build-essential && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install Python dependencies
COPY requirements.txt .
RUN pip install --upgrade pip && \
    pip install --prefix=/install --no-cache-dir -r requirements.txt

# Stage 2: Production image
FROM python:3.9-slim

ENV PYTHONUNBUFFERED=1 \
    DJANGO_SETTINGS_MODULE=app.settings \
    SQLITE_TMPDIR=/tmp \
    PATH="/install/bin:$PATH"  

WORKDIR /app

# Install runtime dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends sqlite3 && \
    rm -rf /var/lib/apt/lists/*

# Copy installed Python packages
COPY --from=builder /install /usr/local

# Copy app code
COPY . .


EXPOSE 8084

# Use uvicorn to serve ASGI Django app
CMD ["uvicorn", "app.asgi:application", "--host", "0.0.0.0", "--port", "8084"]