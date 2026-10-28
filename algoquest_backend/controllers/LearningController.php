<?php
// ========================================================
// AlgoQuest — Learning Controller
// Quest Map nodes, Chapter Details, Lesson Completion, Algorithms
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

class LearningController {
    public static function getTopics(): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        $topicsStmt = $db->query('SELECT id, chapter_number, title, subtitle, description, total_xp, icon_type, order_index FROM topics ORDER BY order_index ASC');
        $topics = $topicsStmt->fetchAll();

        // Retrieve user lesson completion mapping
        $progressStmt = $db->prepare('
            SELECT l.topic_id, COUNT(ulp.id) as completed_count
            FROM user_learning_progress ulp
            INNER JOIN lessons l ON ulp.lesson_id = l.id
            WHERE ulp.user_id = :user_id AND ulp.is_completed = TRUE
            GROUP BY l.topic_id
        ');
        $progressStmt->execute(['user_id' => $userId]);
        $completedMap = [];
        while ($row = $progressStmt->fetch()) {
            $completedMap[(int)$row['topic_id']] = (int)$row['completed_count'];
        }

        // Count total lessons per topic
        $totalStmt = $db->query('SELECT topic_id, COUNT(*) as total_count FROM lessons GROUP BY topic_id');
        $totalMap = [];
        while ($row = $totalStmt->fetch()) {
            $totalMap[(int)$row['topic_id']] = (int)$row['total_count'];
        }

        $results = [];
        $unlockedNext = false;

        foreach ($topics as $index => $t) {
            $topicId = (int)$t['id'];
            $total = $totalMap[$topicId] ?? 5;
            $completed = $completedMap[$topicId] ?? 0;

            // Determine status
            $status = 'LOCKED';
            if ($topicId === 1 || $topicId === 2) {
                // Chapters 1 & 2 are completed in default progression or if completed
                $status = ($completed >= $total || $completed > 0) ? 'COMPLETED' : 'ACTIVE';
            } elseif ($topicId === 3) {
                $status = ($completed >= $total) ? 'COMPLETED' : 'ACTIVE';
            } elseif ($topicId === 4) {
                $prevCompleted = ($completedMap[3] ?? 0) >= ($totalMap[3] ?? 5);
                $status = $prevCompleted ? 'ACTIVE' : 'NEXT';
            } else {
                $status = 'LOCKED';
            }

            $results[] = [
                'id' => $topicId,
                'chapter_number' => (int)$t['chapter_number'],
                'chapter_label' => sprintf('Chapter %02d', $t['chapter_number']),
                'title' => $t['title'],
                'subtitle' => $t['subtitle'],
                'description' => $t['description'],
                'total_xp' => (int)$t['total_xp'],
                'icon_type' => $t['icon_type'],
                'completed_lessons' => $completed,
                'total_lessons' => $total,
                'lessons_label' => "{$completed}/{$total} Lessons",
                'status' => $status
            ];
        }

        Response::success('Topics retrieved', ['topics' => $results]);
    }

    public static function getTopicDetail(int $topicId): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        $stmt = $db->prepare('SELECT id, chapter_number, title, subtitle, description, total_xp FROM topics WHERE id = :id');
        $stmt->execute(['id' => $topicId]);
        $topic = $stmt->fetch();

        if (!$topic) {
            Response::notFound('Topic not found');
        }

        // Fetch lessons
        $lessonsStmt = $db->prepare('
            SELECT l.id, l.title, l.duration_minutes, l.xp_reward, l.lesson_type, l.order_index,
                   COALESCE(ulp.is_completed, FALSE) as is_completed
            FROM lessons l
            LEFT JOIN user_learning_progress ulp ON l.id = ulp.lesson_id AND ulp.user_id = :user_id
            WHERE l.topic_id = :topic_id
            ORDER BY l.order_index ASC
        ');
        $lessonsStmt->execute(['user_id' => $userId, 'topic_id' => $topicId]);
        $rawLessons = $lessonsStmt->fetchAll();

        $lessons = [];
        $foundCurrent = false;

        foreach ($rawLessons as $idx => $l) {
            $isCompleted = (bool)$l['is_completed'];
            $isCurrent = false;
            $isLocked = false;

            if ($isCompleted) {
                $isLocked = false;
            } elseif (!$foundCurrent) {
                $isCurrent = true;
                $isLocked = false;
                $foundCurrent = true;
            } else {
                $isLocked = true;
            }

            $lessons[] = [
                'id' => (int)$l['id'],
                'title' => $l['title'],
                'duration' => "{$l['duration_minutes']} min",
                'duration_minutes' => (int)$l['duration_minutes'],
                'xp' => "+{$l['xp_reward']} XP",
                'xp_reward' => (int)$l['xp_reward'],
                'lesson_type' => $l['lesson_type'],
                'is_completed' => $isCompleted,
                'is_current' => $isCurrent,
                'is_locked' => $isLocked,
            ];
        }

        $completedCount = count(array_filter($lessons, fn($x) => $x['is_completed']));
        $totalCount = count($lessons);
        $percent = $totalCount > 0 ? (int)round(($completedCount / $totalCount) * 100) : 0;

        Response::success('Topic details', [
            'topic' => [
                'id' => (int)$topic['id'],
                'chapter_number' => (int)$topic['chapter_number'],
                'chapter_label' => sprintf('Chapter %02d', $topic['chapter_number']),
                'title' => $topic['title'],
                'subtitle' => $topic['subtitle'],
                'description' => $topic['description'],
                'total_xp' => (int)$topic['total_xp'],
                'progress_percentage' => $percent,
                'completed_lessons' => $completedCount,
                'total_lessons' => $totalCount,
            ],
            'lessons' => $lessons
        ]);
    }

    public static function completeLesson(int $lessonId): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        $lStmt = $db->prepare('SELECT id, topic_id, title, xp_reward FROM lessons WHERE id = :id');
        $lStmt->execute(['id' => $lessonId]);
        $lesson = $lStmt->fetch();

        if (!$lesson) {
            Response::notFound('Lesson not found');
        }

        $xpEarned = (int)$lesson['xp_reward'];

        try {
            $db->beginTransaction();

            // Insert completion if not already completed
            $ins = $db->prepare('
                INSERT INTO user_learning_progress (user_id, lesson_id, is_completed, completed_at)
                VALUES (:user_id, :lesson_id, TRUE, NOW())
                ON DUPLICATE KEY UPDATE is_completed = TRUE
            ');
            $ins->execute(['user_id' => $userId, 'lesson_id' => $lessonId]);

            // Award XP to user profile
            $updProfile = $db->prepare('
                UPDATE user_profiles
                SET xp = xp + :xp, level = FLOOR((xp + :xp) / 500) + 1
                WHERE user_id = :user_id
            ');
            $updProfile->execute(['xp' => $xpEarned, 'user_id' => $userId]);

            // Increment daily quest step
            $today = date('Y-m-d');
            $updQuest = $db->prepare('
                UPDATE user_quests
                SET current_step = LEAST(current_step + 1, 3),
                    is_completed = (current_step + 1 >= 3)
                WHERE user_id = :user_id AND assigned_date = :today
            ');
            $updQuest->execute(['user_id' => $userId, 'today' => $today]);

            // Check if Binary Searcher achievement should be unlocked (Topic 3 complete or Lesson 3/5 complete)
            if ((int)$lesson['topic_id'] === 3) {
                $achievStmt = $db->prepare('
                    INSERT IGNORE INTO user_achievements (user_id, achievement_id, unlocked_at)
                    SELECT :user_id, id, NOW() FROM achievements WHERE code = "binary_searcher"
                ');
                $achievStmt->execute(['user_id' => $userId]);
            }

            // Fetch new total XP & Level
            $pStmt = $db->prepare('SELECT xp, level, current_streak FROM user_profiles WHERE user_id = :user_id');
            $pStmt->execute(['user_id' => $userId]);
            $profile = $pStmt->fetch();

            $db->commit();

            Response::success('Lesson completed!', [
                'xp_awarded' => $xpEarned,
                'total_xp' => (int)$profile['xp'],
                'level' => (int)$profile['level'],
                'streak' => (int)$profile['current_streak'],
            ]);
        } catch (Exception $e) {
            $db->rollBack();
            Response::error('Failed to complete lesson.', ['error' => 'Database error'], 500);
        }
    }

    public static function getAlgorithms(): void {
        AuthMiddleware::authenticate();
        $db = Database::getConnection();

        $stmt = $db->query('SELECT slug, name, time_complexity, space_complexity, description, code_snippet, default_data_json FROM algorithms ORDER BY id ASC');
        $algs = $stmt->fetchAll();

        foreach ($algs as &$a) {
            $a['default_data'] = json_decode($a['default_data_json'] ?? '{}', true);
            unset($a['default_data_json']);
        }

        Response::success('Algorithms list', ['algorithms' => $algs]);
    }
}

