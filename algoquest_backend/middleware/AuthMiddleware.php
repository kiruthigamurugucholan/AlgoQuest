<?php
// ========================================================
// AlgoQuest — Authentication Middleware
// Validates Bearer Tokens against hashed database records
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';

class AuthMiddleware {
    public static function authenticate(): array {
        $headers = getallheaders();
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? '';

        if (empty($authHeader) || !preg_match('/Bearer\s+(\S+)/', $authHeader, $matches)) {
            Response::unauthorized('Bearer token missing or malformed.');
        }

        $rawToken = $matches[1];
        $tokenHash = hash('sha256', $rawToken);

        $db = Database::getConnection();
        $stmt = $db->prepare('
            SELECT u.id, u.name, u.email, u.created_at,
                   p.xp, p.level, p.current_streak, p.selected_goal, p.skill_level, p.daily_minutes
            FROM auth_tokens t
            INNER JOIN users u ON t.user_id = u.id
            LEFT JOIN user_profiles p ON u.id = p.user_id
            WHERE t.token_hash = :token_hash AND t.expires_at > NOW()
            LIMIT 1
        ');
        $stmt->execute(['token_hash' => $tokenHash]);
        $user = $stmt->fetch();

        if (!$user) {
            Response::unauthorized('Invalid or expired authentication session. Please log in again.');
        }

        return $user;
    }
}

