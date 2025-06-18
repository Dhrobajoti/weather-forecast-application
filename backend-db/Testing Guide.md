# Testing Guide for Backend-db Microservice

## 1. Testing API with Curl Commands

### Save Data to Database (Simulates Frontend Request)
```bash
curl -X POST http://localhost:8084/api/db/save \
-H "Content-Type: application/json" \
-d '[
    {
        "fetchTime": "2025-05-10 10:00:00",
        "latitude": "19.1116",
        "longitude": "73.0094",
        "date": "2025-05-10",
        "maxTemp": 32.5,
        "minTemp": 24.8
    },
    {
        "fetchTime": "2025-05-10 10:00:00",
        "latitude": "19.1116",
        "longitude": "73.0094",
        "date": "2025-05-11",
        "maxTemp": 33.1,
        "minTemp": 25.2
    }
]'
```

---

## 2. Accessing the Database Directly

### Steps to Access SQLite Database
1. Navigate to the `backend-db` directory:
     ```bash
     cd backend-db
     ```

2. Open the SQLite database (default name: `weather_db.sqlite3`):
     ```bash
     sqlite3 weather_db.sqlite3
     ```

3. Inside the SQLite shell, you can:
     - List all tables:
         ```sql
         .tables
         ```
     - View all saved records:
         ```sql
         SELECT * FROM db_weatherdata;
         ```
     - Show column headers:
         ```sql
         .headers on
         ```
     - Format output nicely:
         ```sql
         .mode column
         ```
     - Quit the SQLite shell:
         ```sql
         .q
         ```

---

## 3. Using the Django Admin Interface (Recommended)

### Steps to Access Django Admin
1. Create a superuser:
     ```bash
     python manage.py createsuperuser
     ```
     Follow the prompts to set up an admin user.

2. Start the Django development server:
     ```bash
     python manage.py runserver 8084
     ```

3. Access the admin interface:
     - Open your browser and visit: [http://localhost:8084/admin](http://localhost:8084/admin)
     - Log in using your superuser credentials.

4. View and manage saved records through the user-friendly admin interface.
