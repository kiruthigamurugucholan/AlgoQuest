# AlgoQuest — PHP REST API & MySQL Backend

Backend REST API for **AlgoQuest – AI-Powered DSA Learning & Visualization App**.

---

## 1. Requirements

- **PHP:** 8.0 or newer with PDO and pdo_mysql extensions enabled.
- **MySQL / MariaDB:** 10.4+ (Default in XAMPP).
- **Web Server:** Apache (via XAMPP) or built-in PHP development server (`php -S`).

---

## 2. Setup Instructions

### Option A: Using XAMPP
1. Copy or link this `algoquest_backend` folder into your XAMPP `htdocs` directory:
   ```cmd
   xcopy /E /I "algoquest_backend" "C:\xampp\htdocs\algoquest_backend"
   ```
2. Start **Apache** and **MySQL** in the XAMPP Control Panel.
3. Open **phpMyAdmin** at `http://localhost/phpmyadmin/`.
4. Import the database:
   - Create a new database named `algoquest_db` (utf8mb4).
   - Import `database/schema.sql`.
   - Import `database/seed.sql`.
   *(Or via command line:)*
   ```cmd
   C:\xampp\mysql\bin\mysql.exe -u root < database/schema.sql
   C:\xampp\mysql\bin\mysql.exe -u root algoquest_db < database/seed.sql
   ```
5. Test the API in your browser:
   ```
   http://localhost/algoquest_backend/api/index.php?route=health
   ```

### Option B: Built-in PHP CLI Server (Quick Local Testing)
Run inside the `algoquest_backend` directory:
```bash
C:\xampp\php\php.exe -S 0.0.0.0:8000 -t api
```
Then access at `http://localhost:8000/health`.

---

## 3. Android Emulator Network Address

- When running the Android app inside the **Android Emulator**, the host machine's `localhost` is accessible at:
  ```
  http://10.0.2.2/algoquest_backend/api/index.php?route=
  ```
  *(or `http://10.0.2.2:8000/` if using the PHP built-in server).*
- When testing on a **physical Android device** connected over Wi-Fi, use your computer's local LAN IP (e.g. `http://192.168.1.X/...`).

---

## 4. API Endpoints Reference

All requests and responses use `Content-Type: application/json`.
Protected endpoints require: `Authorization: Bearer <token>`.

### Authentication & Profile
- `POST /auth/register` — Register new user account.
  ```json
  { "name": "Alex Morgan", "email": "alex@test.com", "password": "password123" }
  ```
- `POST /auth/login` — Authenticate and receive bearer token.
  ```json
  { "email": "alex@test.com", "password": "password123" }
  ```
- `GET /auth/me` — Verify token session.
- `POST /auth/logout` — Invalidate token.
- `POST /user/personalize` — Save onboarding preferences (goal, level, daily minutes).

### Dashboard & Quests
- `GET /dashboard` — Retrieves user stats, daily streak, active daily quest, and current chapter.

### Learning Path & Topics
- `GET /topics` — Lists 7 roadmap chapters with user completion flags.
- `GET /topics/{id}` — Retrieves topic details, lesson roadmap, and progress percentage.
- `POST /lessons/{id}/complete` — Marks a lesson completed, awards XP, and checks achievements.

### Algorithm Visualizer Lab
- `GET /algorithms` — Lists available algorithms (Binary Search, Bubble Sort, Two Pointers).

### Quizzes & Mastery Challenges
- `GET /quizzes/lesson/{lessonId}` — Retrieves quiz questions and options (omitting answers).
- `POST /quizzes/{id}/submit` — Grades submitted answers, calculates score, and awards XP.
  ```json
  {
    "answers": [
      { "question_id": 1, "option_id": 1 },
      { "question_id": 2, "option_id": 5 }
    ]
  }
  ```

### AI Tutor Assistant
- `POST /ai/chat` — Sends a query to the DSA algorithmic tutor engine.
  ```json
  { "message": "Why is binary search so fast?" }
  ```
- `GET /ai/history` — Retrieves conversation history.

### Profile & Gamification
- `GET /profile` — Full user statistics, skill masteries, and earned badges.
- `PUT /profile` — Updates user profile name.

