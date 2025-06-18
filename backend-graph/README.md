
# Option 1 : Standalone Python (Django) Service - `backend-graph`

This document describes how to set up, run, and test the Django-based standalone backend service. It also includes instructions for deploying it with Gunicorn for production use.

---

## 1. Start the Django Service

### Step 1.1: Navigate to Your Project Directory

```bash
cd backend-graph
```

### Step 1.2: Activate the Virtual Environment

#### On Windows:
```bash
myenv\Scripts\activate
```

#### On Linux/Mac:
```bash
source myenv/bin/activate
```

### Step 1.3: Install Required Python Packages

```bash
pip install -r requirements.txt
```

### Step 1.4: Run the Django Service (Development)

```bash
python manage.py runserver 8083
```

---

## Testing the API

You can test the API using `curl` with the following command:

```bash
curl -X POST http://localhost:8083/api/graph/plot \
    -H "Content-Type: application/json" \
    -d '[{"fetchTime":"2025-05-10","latitude":"19.11","longitude":"73.00","date":"2025-05-10","maxTemp":30.5,"minTemp":22.3}]'
```

## Expected Output

The API should return a JSON response similar to the following:

```json
{"plot": "base64-encoded-image"}
```

---

## 3. Auto-Restart on Crash (Production Deployment)

For production, it is recommended to use **uvicorn** as the WSGI HTTP server.

### Step 3.1: Install uvicorn

```bash
pip install uvicorn
```

### Step 3.2: Run the Django App with Gunicorn

```bash
uvicorn app.asgi:application -b 0.0.0.0:8083 --workers 4
```

This will start the application on port `8083` and automatically restart workers if they crash.

---

## Notes

- Make sure `uvicorn` is not used in development environments that require frequent code changes, unless paired with hot-reloading tools.
- Ensure all environment variables (e.g., for Django secret key, database config) are set properly for production.
---


# Option 2: Docker Container Python (Django) Service 
## 1. Create Dockerfile
## 2. Build and Run
```
# Build the image
docker build -t backend-graph .

# Run the container
docker run -d -p 8083:8083 --name weather-graph backend-graph
```

## 3. Verify
```
docker logs weather-graph  # Check startup logs
curl localhost:8083/api/graph/plot  # Test endpoint
```

### Integration with Other Services
### 1. For Python-to-Python Communication
```
import requests
response = requests.post(
    "http://backend-graph:8083/api/graph/plot",
    json=[{"date": "2025-05-10", "maxTemp": 30.5}]
)
```