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

        // 1. Verify that the requested lesson exists
        $lStmt = $db->prepare('SELECT id, topic_id, title, xp_reward FROM lessons WHERE id = :id');
        $lStmt->execute(['id' => $lessonId]);
        $lesson = $lStmt->fetch();

        if (!$lesson) {
            Response::notFound('Lesson not found');
        }

        $lessonXpReward = (int)$lesson['xp_reward'];
        $topicId = (int)$lesson['topic_id'];

        try {
            $db->beginTransaction();

            // 2. Lock user profile row to serialize concurrent requests for this user
            $pStmt = $db->prepare('SELECT xp, level, current_streak FROM user_profiles WHERE user_id = :user_id FOR UPDATE');
            $pStmt->execute(['user_id' => $userId]);
            $profile = $pStmt->fetch();

            if (!$profile) {
                if ($db->inTransaction()) {
                    $db->rollBack();
                }
                Response::error('User profile not found', [], 404);
            }

            $currentXp = (int)$profile['xp'];
            $currentLevel = (int)$profile['level'];
            $currentStreak = (int)$profile['current_streak'];

            // 3. Inspect existing progress with exclusive lock
            $progStmt = $db->prepare('
                SELECT is_completed 
                FROM user_learning_progress 
                WHERE user_id = :user_id AND lesson_id = :lesson_id 
                FOR UPDATE
            ');
            $progStmt->execute([
                'user_id' => $userId,
                'lesson_id' => $lessonId
            ]);
            $existingProgress = $progStmt->fetch();
            $alreadyCompleted = ($existingProgress && (bool)$existingProgress['is_completed']);

            // 4. Idempotent early-exit if already completed
            if ($alreadyCompleted) {
                $db->commit();

                Response::success('Lesson already completed. Progress preserved.', [
                    'xp_awarded' => 0,
                    'total_xp' => $currentXp,
                    'level' => $currentLevel,
                    'streak' => $currentStreak,
                ]);
            }

            // 5. First-time successful completion: mark completed and set completed_at
            $ins = $db->prepare('
                INSERT INTO user_learning_progress (user_id, lesson_id, is_completed, completed_at)
                VALUES (:user_id, :lesson_id, TRUE, NOW())
                ON DUPLICATE KEY UPDATE 
                    is_completed = TRUE, 
                    completed_at = NOW()
            ');
            $ins->execute([
                'user_id' => $userId,
                'lesson_id' => $lessonId
            ]);

            // 6. Update today\'s active assigned DAILY quest (matching DashboardController behavior)
            $today = date('Y-m-d');
            $qStmt = $db->prepare('
                SELECT uq.id, uq.quest_id, uq.current_step, uq.is_completed, q.required_steps
                FROM user_quests uq
                INNER JOIN quests q ON uq.quest_id = q.id
                WHERE uq.user_id = :user_id AND uq.assigned_date = :today
                LIMIT 1
                FOR UPDATE
            ');
            $qStmt->execute(['user_id' => $userId, 'today' => $today]);
            $userQuest = $qStmt->fetch();

            if (!$userQuest) {
                // If not assigned yet today, assign default quest 1 (matching DashboardController)
                $insQuest = $db->prepare('
                    INSERT IGNORE INTO user_quests (user_id, quest_id, current_step, is_completed, assigned_date)
                    VALUES (:user_id, 1, 0, FALSE, :today)
                ');
                $insQuest->execute([
                    'user_id' => $userId,
                    'today' => $today
                ]);

                $qStmt->execute(['user_id' => $userId, 'today' => $today]);
                $userQuest = $qStmt->fetch();
            }

            if ($userQuest) {
                $userQuestId = (int)$userQuest['id'];
                $currentStep = (int)$userQuest['current_step'];
                $requiredSteps = (int)$userQuest['required_steps'];
                $isQuestCompleted = (bool)$userQuest['is_completed'];

                // Handle completed quests consistently: do not increment if already completed or reached limit
                if (!$isQuestCompleted && $currentStep < $requiredSteps) {
                    $nextStep = $currentStep + 1;
                    $isNowCompleted = ($nextStep >= $requiredSteps);

                    $updQuest = $db->prepare('
                        UPDATE user_quests
                        SET current_step = :next_step,
                            is_completed = :is_completed,
                            completed_at = CASE WHEN :is_completed_flag = 1 AND completed_at IS NULL THEN NOW() ELSE completed_at END
                        WHERE id = :id
                    ');
                    $updQuest->execute([
                        'next_step' => $nextStep,
                        'is_completed' => $isNowCompleted ? 1 : 0,
                        'is_completed_flag' => $isNowCompleted ? 1 : 0,
                        'id' => $userQuestId
                    ]);
                }
            }

            // 7. Check Topic Completion & Binary Searcher Achievement
            $achievementXp = 0;
            if ($topicId === 3) {
                $totStmt = $db->prepare('SELECT COUNT(*) FROM lessons WHERE topic_id = :topic_id');
                $totStmt->execute(['topic_id' => $topicId]);
                $totalLessonsInTopic = (int)$totStmt->fetchColumn();

                $compStmt = $db->prepare('
                    SELECT COUNT(*) 
                    FROM user_learning_progress ulp
                    INNER JOIN lessons l ON ulp.lesson_id = l.id
                    WHERE ulp.user_id = :user_id 
                      AND l.topic_id = :topic_id 
                      AND ulp.is_completed = TRUE
                ');
                $compStmt->execute([
                    'user_id' => $userId,
                    'topic_id' => $topicId
                ]);
                $completedLessonsInTopic = (int)$compStmt->fetchColumn();

                $isTopicCompleted = ($totalLessonsInTopic > 0 && $completedLessonsInTopic >= $totalLessonsInTopic);

                // Upsert user_topic_progress using distinct placeholders for ATTR_EMULATE_PREPARES => false
                $updTopicProg = $db->prepare('
                    INSERT INTO user_topic_progress (user_id, topic_id, completed_lessons, total_lessons, is_completed)
                    VALUES (:user_id, :topic_id, :completed_ins, :total_ins, :is_completed_ins)
                    ON DUPLICATE KEY UPDATE 
                        completed_lessons = :completed_upd,
                        total_lessons = :total_upd,
                        is_completed = :is_completed_upd
                ');
                $updTopicProg->execute([
                    'user_id' => $userId,
                    'topic_id' => $topicId,
                    'completed_ins' => $completedLessonsInTopic,
                    'total_ins' => $totalLessonsInTopic,
                    'is_completed_ins' => $isTopicCompleted ? 1 : 0,
                    'completed_upd' => $completedLessonsInTopic,
                    'total_upd' => $totalLessonsInTopic,
                    'is_completed_upd' => $isTopicCompleted ? 1 : 0
                ]);

                // Unlock Binary Searcher achievement ONLY when all required Topic 3 lessons are complete
                if ($isTopicCompleted) {
                    $achStmt = $db->prepare('SELECT id, xp_reward FROM achievements WHERE code = "binary_searcher" LIMIT 1');
                    $achStmt->execute();
                    $achRow = $achStmt->fetch();

                    if ($achRow) {
                        $achId = (int)$achRow['id'];
                        $achReward = (int)$achRow['xp_reward']; // 250 XP from seed.sql / database

                        $insAch = $db->prepare('
                            INSERT IGNORE INTO user_achievements (user_id, achievement_id, unlocked_at)
                            VALUES (:user_id, :achievement_id, NOW())
                        ');
                        $insAch->execute([
                            'user_id' => $userId,
                            'achievement_id' => $achId
                        ]);

                        // Award achievement XP only if newly unlocked (first-time unlock)
                        if ($insAch->rowCount() > 0) {
                            $achievementXp = $achReward;
                        }
                    }
                }
            }

            // 8. Calculate total XP and derive level
            $totalAwardedXp = $lessonXpReward + $achievementXp;
            $newXp = $currentXp + $totalAwardedXp;
            $newLevel = (int)floor($newXp / 500) + 1;

            $updProfile = $db->prepare('
                UPDATE user_profiles
                SET xp = :xp, level = :level
                WHERE user_id = :user_id
            ');
            $updProfile->execute([
                'xp' => $newXp,
                'level' => $newLevel,
                'user_id' => $userId
            ]);

            // 9. Commit before invoking terminating Response helper
            $db->commit();

            Response::success('Lesson completed!', [
                'xp_awarded' => $totalAwardedXp,
                'total_xp' => $newXp,
                'level' => $newLevel,
                'streak' => $currentStreak,
            ]);
        } catch (Throwable $e) {
            if ($db->inTransaction()) {
                $db->rollBack();
            }
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

