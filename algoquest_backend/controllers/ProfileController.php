<?php
// ========================================================
// AlgoQuest — Profile Controller
// Stats, Skill Mastery, Achievements, Profile Metadata
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

class ProfileController {
    public static function getProfile(): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        // 1. Fetch user & profile stats
        $stmt = $db->prepare('
            SELECT u.id, u.name, u.email, u.created_at,
                   p.xp, p.level, p.current_streak, p.selected_goal, p.skill_level, p.daily_minutes
            FROM users u
            LEFT JOIN user_profiles p ON u.id = p.user_id
            WHERE u.id = :user_id
            LIMIT 1
        ');
        $stmt->execute(['user_id' => $userId]);
        $profile = $stmt->fetch();

        // Count lessons solved
        $solvedStmt = $db->prepare('SELECT COUNT(*) FROM user_learning_progress WHERE user_id = :user_id AND is_completed = TRUE');
        $solvedStmt->execute(['user_id' => $userId]);
        $solvedCount = (int)$solvedStmt->fetchColumn();

        // 2. Compute Skill Masteries based on actual completed lessons per topic
        $topicsStmt = $db->query('SELECT id, title FROM topics ORDER BY order_index ASC');
        $allTopics = $topicsStmt->fetchAll();

        $compStmt = $db->prepare('
            SELECT l.topic_id, COUNT(ulp.id) as count
            FROM user_learning_progress ulp
            INNER JOIN lessons l ON ulp.lesson_id = l.id
            WHERE ulp.user_id = :user_id AND ulp.is_completed = TRUE
            GROUP BY l.topic_id
        ');
        $compStmt->execute(['user_id' => $userId]);
        $completedByTopic = [];
        while ($r = $compStmt->fetch()) {
            $completedByTopic[(int)$r['topic_id']] = (int)$r['count'];
        }

        $skills = [
            [
                'title' => 'Array & Two Pointers',
                'progress' => min(1.0, 0.40 + (($completedByTopic[1] ?? 0) + ($completedByTopic[2] ?? 0)) * 0.15),
                'percent' => (string)(int)(min(100, 40 + (($completedByTopic[1] ?? 0) + ($completedByTopic[2] ?? 0)) * 15)) . '%',
                'color_tag' => 'green'
            ],
            [
                'title' => 'Searching Algorithms',
                'progress' => min(1.0, (($completedByTopic[3] ?? 0) / 5.0)),
                'percent' => (string)(int)(min(100, (($completedByTopic[3] ?? 0) / 5.0) * 100)) . '%',
                'color_tag' => 'cyan'
            ],
            [
                'title' => 'Sorting Algorithms',
                'progress' => min(1.0, (($completedByTopic[4] ?? 0) / 5.0)),
                'percent' => (string)(int)(min(100, (($completedByTopic[4] ?? 0) / 5.0) * 100)) . '%',
                'color_tag' => 'gold'
            ],
            [
                'title' => 'Trees & Graphs',
                'progress' => min(1.0, (($completedByTopic[6] ?? 0) + ($completedByTopic[7] ?? 0)) / 10.0),
                'percent' => (string)(int)(min(100, ((($completedByTopic[6] ?? 0) + ($completedByTopic[7] ?? 0)) / 10.0) * 100)) . '%',
                'color_tag' => 'purple'
            ]
        ];

        // 3. Fetch achievements with unlock state
        $achStmt = $db->prepare('
            SELECT a.id, a.code, a.title, a.description, a.icon_emoji, a.xp_reward,
                   ua.unlocked_at,
                   (ua.id IS NOT NULL) as is_unlocked
            FROM achievements a
            LEFT JOIN user_achievements ua ON a.id = ua.achievement_id AND ua.user_id = :user_id
            ORDER BY a.id ASC
        ');
        $achStmt->execute(['user_id' => $userId]);
        $achievements = $achStmt->fetchAll();

        foreach ($achievements as &$ach) {
            $ach['is_unlocked'] = (bool)$ach['is_unlocked'];
        }

        // Generate initials
        $parts = explode(' ', trim($profile['name']));
        $initials = strtoupper(substr($parts[0], 0, 1) . (isset($parts[1]) ? substr($parts[1], 0, 1) : ''));

        Response::success('Profile loaded', [
            'user' => [
                'id' => (int)$profile['id'],
                'name' => $profile['name'],
                'email' => $profile['email'],
                'initials' => $initials ?: 'AQ',
                'joined' => 'Joined ' . date('F Y', strtotime($profile['created_at'])),
                'xp' => (int)($profile['xp'] ?? 0),
                'level' => (int)($profile['level'] ?? 1),
                'streak' => (int)($profile['current_streak'] ?? 1),
                'solved_lessons' => $solvedCount,
                'percentile' => 'Top 12% Global',
            ],
            'skills' => $skills,
            'achievements' => $achievements
        ]);
    }

    public static function updateProfile(array $body): void {
        $user = AuthMiddleware::authenticate();
        $name = trim($body['name'] ?? '');

        if (empty($name)) {
            Response::error('Name cannot be empty', ['name' => 'Name is required'], 422);
        }

        $db = Database::getConnection();
        $stmt = $db->prepare('UPDATE users SET name = :name WHERE id = :id');
        $stmt->execute(['name' => $name, 'id' => $user['id']]);

        Response::success('Profile updated', ['name' => $name]);
    }
}

