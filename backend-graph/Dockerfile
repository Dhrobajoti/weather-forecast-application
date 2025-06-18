# Stage 1: Build dependencies
FROM python:3.9-slim AS builder

WORKDIR /app

# Install build dependencies, install Python packages, then clean up
COPY requirements.txt .
RUN apt-get update && \
    apt-get install -y --no-install-recommends gcc python3-dev libffi-dev build-essential && \
    pip install --upgrade pip && \
    pip install --prefix=/install --no-cache-dir -r requirements.txt && \
    apt-get purge -y --auto-remove gcc python3-dev libffi-dev build-essential && \
    rm -rf /var/lib/apt/lists/*

# Stage 2: Production image
FROM python:3.9-slim

ENV PYTHONUNBUFFERED=1 \
    MPLBACKEND=Agg \
    DJANGO_SETTINGS_MODULE=app.settings

WORKDIR /app

# Copy installed Python packages
COPY --from=builder /install /usr/local

# Copy only necessary app code
COPY . .

EXPOSE 8082

CMD ["uvicorn", "app.asgi:application", "--host", "0.0.0.0", "--port", "8082"]
