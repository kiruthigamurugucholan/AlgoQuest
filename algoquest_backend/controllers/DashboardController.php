<?php
// ========================================================
// AlgoQuest — Dashboard Controller
// Aggregated data for Home Dashboard: XP, Streak, Quests, Path
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

class DashboardController {
    public static function index(): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        // 1. Fetch or assign today's daily quest
        $today = date('Y-m-d');
        $questStmt = $db->prepare('
            SELECT uq.id as user_quest_id, uq.current_step, uq.is_completed,
                   q.id as quest_id, q.title, q.description, q.xp_reward, q.required_steps
            FROM user_quests uq
            INNER JOIN quests q ON uq.quest_id = q.id
            WHERE uq.user_id = :user_id AND uq.assigned_date = :today
            LIMIT 1
        ');
        $questStmt->execute(['user_id' => $userId, 'today' => $today]);
        $dailyQuest = $questStmt->fetch();

        if (!$dailyQuest) {
            // Assign default quest 1
            $assign = $db->prepare('
                INSERT IGNORE INTO user_quests (user_id, quest_id, current_step, is_completed, assigned_date)
                VALUES (:user_id, 1, 0, FALSE, :today)
            ');
            $assign->execute(['user_id' => $userId, 'today' => $today]);

            $questStmt->execute(['user_id' => $userId, 'today' => $today]);
            $dailyQuest = $questStmt->fetch();
        }

        // 2. Fetch Active Chapter progress (Defaults to Chapter 3 Searching Algorithms or user current)
        $topicStmt = $db->prepare('
            SELECT t.id, t.chapter_number, t.title, t.subtitle, t.total_xp,
                   COALESCE(utp.completed_lessons, 0) as completed_lessons,
                   (SELECT COUNT(*) FROM lessons l WHERE l.topic_id = t.id) as total_lessons
            FROM topics t
            LEFT JOIN user_topic_progress utp ON t.id = utp.topic_id AND utp.user_id = :user_id
            WHERE t.id = 3
            LIMIT 1
        ');
        $topicStmt->execute(['user_id' => $userId]);
        $activeTopic = $topicStmt->fetch();

        // If no user_topic_progress row yet, query actual lessons completed
        $completedLessonsCountStmt = $db->prepare('
            SELECT COUNT(*) FROM user_learning_progress ulp
            INNER JOIN lessons l ON ulp.lesson_id = l.id
            WHERE ulp.user_id = :user_id AND l.topic_id = 3 AND ulp.is_completed = TRUE
        ');
        $completedLessonsCountStmt->execute(['user_id' => $userId]);
        $realCompleted = (int)$completedLessonsCountStmt->fetchColumn();
        if ($activeTopic) {
            $activeTopic['completed_lessons'] = $realCompleted;
        }

        // 3. User stats calculation (total solved lessons count)
        $solvedCountStmt = $db->prepare('SELECT COUNT(*) FROM user_learning_progress WHERE user_id = :user_id AND is_completed = TRUE');
        $solvedCountStmt->execute(['user_id' => $userId]);
        $totalSolvedLessons = (int)$solvedCountStmt->fetchColumn();

        Response::success('Dashboard loaded', [
            'user' => [
                'id' => $userId,
                'name' => $user['name'],
                'xp' => (int)($user['xp'] ?? 0),
                'level' => (int)($user['level'] ?? 1),
                'streak' => (int)($user['current_streak'] ?? 1),
                'percentile' => 'top 12%',
                'total_lessons_solved' => $totalSolvedLessons
            ],
            'daily_quest' => [
                'id' => (int)($dailyQuest['quest_id'] ?? 1),
                'title' => $dailyQuest['title'] ?? 'Find it. Faster.',
                'description' => $dailyQuest['description'] ?? 'Master binary search, visualize the logic, then test your instincts.',
                'xp_reward' => (int)($dailyQuest['xp_reward'] ?? 150),
                'current_step' => (int)($dailyQuest['current_step'] ?? 1),
                'required_steps' => (int)($dailyQuest['required_steps'] ?? 3),
                'is_completed' => (bool)($dailyQuest['is_completed'] ?? false),
            ],
            'active_topic' => $activeTopic ? [
                'id' => (int)$activeTopic['id'],
                'chapter_number' => (int)$activeTopic['chapter_number'],
                'title' => $activeTopic['title'],
                'subtitle' => $activeTopic['subtitle'],
                'completed_lessons' => (int)$activeTopic['completed_lessons'],
                'total_lessons' => (int)($activeTopic['total_lessons'] ?: 5),
            ] : null
        ]);
    }
}

