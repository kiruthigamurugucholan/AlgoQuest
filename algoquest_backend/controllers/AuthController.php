<?php
// ========================================================
// AlgoQuest — Authentication Controller
// Real registration, login, logout, profile preference sync
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

class AuthController {
    public static function register(array $body): void {
        $name = trim($body['name'] ?? '');
        $email = strtolower(trim($body['email'] ?? ''));
        $password = $body['password'] ?? '';

        $errors = [];
        if (empty($name)) {
            $errors['name'] = 'Name is required.';
        }
        if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $errors['email'] = 'A valid email address is required.';
        }
        if (strlen($password) < 8) {
            $errors['password'] = 'Password must be at least 8 characters long.';
        }

        if (!empty($errors)) {
            Response::error('Validation failed.', $errors, 422);
        }

        $db = Database::getConnection();

        // Check if email already exists
        $checkStmt = $db->prepare('SELECT id FROM users WHERE email = :email LIMIT 1');
        $checkStmt->execute(['email' => $email]);
        if ($checkStmt->fetch()) {
            Response::error('An account with this email address already exists.', ['email' => 'Email is already registered.'], 409);
        }

        try {
            $db->beginTransaction();

            $passwordHash = password_hash($password, PASSWORD_BCRYPT);
            $insertUser = $db->prepare('INSERT INTO users (name, email, password_hash) VALUES (:name, :email, :password_hash)');
            $insertUser->execute([
                'name' => $name,
                'email' => $email,
                'password_hash' => $passwordHash,
            ]);
            $userId = (int)$db->lastInsertId();

            // Initialize User Profile with 0 XP, Level 1, Streak 1, Today active
            $insertProfile = $db->prepare('
                INSERT INTO user_profiles (user_id, xp, level, current_streak, last_active_date)
                VALUES (:user_id, 0, 1, 1, CURDATE())
            ');
            $insertProfile->execute(['user_id' => $userId]);

            // Assign today's daily quest (Quest ID 1)
            $assignQuest = $db->prepare('
                INSERT INTO user_quests (user_id, quest_id, current_step, is_completed, assigned_date)
                VALUES (:user_id, 1, 0, FALSE, CURDATE())
            ');
            $assignQuest->execute(['user_id' => $userId]);

            // Generate secure bearer token
            $rawToken = bin2hex(random_bytes(32));
            $tokenHash = hash('sha256', $rawToken);
            $expiresAt = date('Y-m-d H:i:s', strtotime('+30 days'));

            $insertToken = $db->prepare('
                INSERT INTO auth_tokens (user_id, token_hash, expires_at)
                VALUES (:user_id, :token_hash, :expires_at)
            ');
            $insertToken->execute([
                'user_id' => $userId,
                'token_hash' => $tokenHash,
                'expires_at' => $expiresAt,
            ]);

            $db->commit();

            Response::success('Account created successfully.', [
                'token' => $rawToken,
                'expires_at' => $expiresAt,
                'user' => [
                    'id' => $userId,
                    'name' => $name,
                    'email' => $email,
                    'xp' => 0,
                    'level' => 1,
                    'streak' => 1,
                    'goal' => 'Build foundations',
                    'skill_level' => 'Beginner',
                    'daily_minutes' => 15,
                ]
            ], 201);
        } catch (Exception $e) {
            $db->rollBack();
            Response::error('Failed to create account. Please try again.', ['server' => 'Registration error'], 500);
        }
    }

    public static function login(array $body): void {
        $email = strtolower(trim($body['email'] ?? ''));
        $password = $body['password'] ?? '';

        if (empty($email) || empty($password)) {
            Response::error('Email and password are required.', ['credentials' => 'Missing email or password.'], 400);
        }

        $db = Database::getConnection();
        $stmt = $db->prepare('
            SELECT u.id, u.name, u.email, u.password_hash,
                   p.xp, p.level, p.current_streak, p.last_active_date,
                   p.selected_goal, p.skill_level, p.daily_minutes
            FROM users u
            LEFT JOIN user_profiles p ON u.id = p.user_id
            WHERE u.email = :email
            LIMIT 1
        ');
        $stmt->execute(['email' => $email]);
        $user = $stmt->fetch();

        if (!$user || !password_verify($password, $user['password_hash'])) {
            Response::error('Invalid email or password.', ['credentials' => 'Credentials do not match our records.'], 401);
        }

        // Streak maintenance logic
        $today = date('Y-m-d');
        $yesterday = date('Y-m-d', strtotime('-1 day'));
        $lastActive = $user['last_active_date'];
        $streak = (int)$user['current_streak'];

        if ($lastActive === $yesterday) {
            $streak += 1;
        } elseif ($lastActive !== $today) {
            // Gap day, streak resets to 1
            $streak = 1;
        }

        // Update profile last active & streak
        $updateProfile = $db->prepare('
            UPDATE user_profiles
            SET current_streak = :streak, last_active_date = :today
            WHERE user_id = :user_id
        ');
        $updateProfile->execute([
            'streak' => $streak,
            'today' => $today,
            'user_id' => $user['id'],
        ]);

        // Generate session token
        $rawToken = bin2hex(random_bytes(32));
        $tokenHash = hash('sha256', $rawToken);
        $expiresAt = date('Y-m-d H:i:s', strtotime('+30 days'));

        $insertToken = $db->prepare('
            INSERT INTO auth_tokens (user_id, token_hash, expires_at)
            VALUES (:user_id, :token_hash, :expires_at)
        ');
        $insertToken->execute([
            'user_id' => $user['id'],
            'token_hash' => $tokenHash,
            'expires_at' => $expiresAt,
        ]);

        Response::success('Login successful.', [
            'token' => $rawToken,
            'expires_at' => $expiresAt,
            'user' => [
                'id' => (int)$user['id'],
                'name' => $user['name'],
                'email' => $user['email'],
                'xp' => (int)($user['xp'] ?? 0),
                'level' => (int)($user['level'] ?? 1),
                'streak' => $streak,
                'goal' => $user['selected_goal'] ?? 'Build foundations',
                'skill_level' => $user['skill_level'] ?? 'Beginner',
                'daily_minutes' => (int)($user['daily_minutes'] ?? 15),
            ]
        ]);
    }

    public static function logout(): void {
        $headers = getallheaders();
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? '';
        if (preg_match('/Bearer\s+(\S+)/', $authHeader, $matches)) {
            $tokenHash = hash('sha256', $matches[1]);
            $db = Database::getConnection();
            $del = $db->prepare('DELETE FROM auth_tokens WHERE token_hash = :hash');
            $del->execute(['hash' => $tokenHash]);
        }
        Response::success('Logged out successfully.');
    }

    public static function me(): void {
        $user = AuthMiddleware::authenticate();
        Response::success('Authenticated profile', ['user' => $user]);
    }

    public static function personalize(array $body): void {
        $user = AuthMiddleware::authenticate();

        $goal = trim($body['goal'] ?? $user['selected_goal'] ?? 'Build foundations');
        $level = trim($body['level'] ?? $user['skill_level'] ?? 'Beginner');
        $dailyMinutes = (int)($body['daily_minutes'] ?? $user['daily_minutes'] ?? 15);

        $db = Database::getConnection();
        $stmt = $db->prepare('
            UPDATE user_profiles
            SET selected_goal = :goal, skill_level = :level, daily_minutes = :minutes
            WHERE user_id = :user_id
        ');
        $stmt->execute([
            'goal' => $goal,
            'level' => $level,
            'minutes' => $dailyMinutes,
            'user_id' => $user['id'],
        ]);

        Response::success('Learning preferences saved.', [
            'goal' => $goal,
            'skill_level' => $level,
            'daily_minutes' => $dailyMinutes
        ]);
    }
}

