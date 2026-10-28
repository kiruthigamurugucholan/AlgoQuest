<?php
// ========================================================
// AlgoQuest — Quiz Controller
// Server-side answer validation, scoring, and attempts
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

class QuizController {
    public static function getQuiz(int $lessonId): void {
        AuthMiddleware::authenticate();
        $db = Database::getConnection();

        $stmt = $db->prepare('SELECT id, lesson_id, title, description, pass_percentage FROM quizzes WHERE lesson_id = :lesson_id');
        $stmt->execute(['lesson_id' => $lessonId]);
        $quiz = $stmt->fetch();

        if (!$quiz) {
            Response::notFound('Quiz not found for this lesson.');
        }

        $quizId = (int)$quiz['id'];

        // Fetch questions
        $qStmt = $db->prepare('
            SELECT id, question_text, order_index
            FROM quiz_questions
            WHERE quiz_id = :quiz_id
            ORDER BY order_index ASC
        ');
        $qStmt->execute(['quiz_id' => $quizId]);
        $questions = $qStmt->fetchAll();

        // Fetch options without exposing is_correct
        $optStmt = $db->prepare('
            SELECT id, question_id, option_text
            FROM quiz_options
            WHERE question_id = :question_id
            ORDER BY id ASC
        ');

        foreach ($questions as &$q) {
            $optStmt->execute(['question_id' => $q['id']]);
            $q['options'] = $optStmt->fetchAll();
        }

        Response::success('Quiz loaded', [
            'quiz' => [
                'id' => $quizId,
                'lesson_id' => (int)$quiz['lesson_id'],
                'title' => $quiz['title'],
                'description' => $quiz['description'],
                'pass_percentage' => (int)$quiz['pass_percentage'],
                'questions' => $questions
            ]
        ]);
    }

    public static function submitQuiz(int $quizId, array $body): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        $qStmt = $db->prepare('SELECT id, lesson_id, title, pass_percentage FROM quizzes WHERE id = :id');
        $qStmt->execute(['id' => $quizId]);
        $quiz = $qStmt->fetch();

        if (!$quiz) {
            Response::notFound('Quiz not found.');
        }

        $userAnswers = $body['answers'] ?? []; // Map of question_id => option_id
        if (empty($userAnswers)) {
            Response::error('No answers submitted.', ['answers' => 'Answers array is required.'], 400);
        }

        // Fetch correct answers and explanations
        $answersStmt = $db->prepare('
            SELECT q.id as question_id, q.question_text, q.explanation,
                   o.id as correct_option_id, o.option_text as correct_option_text
            FROM quiz_questions q
            INNER JOIN quiz_options o ON q.id = o.question_id AND o.is_correct = TRUE
            WHERE q.quiz_id = :quiz_id
        ');
        $answersStmt->execute(['quiz_id' => $quizId]);
        $correctData = $answersStmt->fetchAll();

        $totalQuestions = count($correctData);
        $correctCount = 0;
        $review = [];

        // Build submitted map
        $submittedMap = [];
        foreach ($userAnswers as $ans) {
            if (isset($ans['question_id']) && isset($ans['option_id'])) {
                $submittedMap[(int)$ans['question_id']] = (int)$ans['option_id'];
            }
        }

        foreach ($correctData as $cd) {
            $qId = (int)$cd['question_id'];
            $selectedOptId = $submittedMap[$qId] ?? null;
            $isCorrect = ($selectedOptId !== null && $selectedOptId === (int)$cd['correct_option_id']);

            if ($isCorrect) {
                $correctCount++;
            }

            $review[] = [
                'question_id' => $qId,
                'question_text' => $cd['question_text'],
                'selected_option_id' => $selectedOptId,
                'correct_option_id' => (int)$cd['correct_option_id'],
                'correct_option_text' => $cd['correct_option_text'],
                'explanation' => $cd['explanation'],
                'is_correct' => $isCorrect
            ];
        }

        $percentage = $totalQuestions > 0 ? (int)round(($correctCount / $totalQuestions) * 100) : 0;
        $passed = $percentage >= (int)$quiz['pass_percentage'];

        try {
            $db->beginTransaction();

            // Record attempt
            $insAttempt = $db->prepare('
                INSERT INTO quiz_attempts (user_id, quiz_id, score, total_questions, passed, completed_at)
                VALUES (:user_id, :quiz_id, :score, :total, :passed, NOW())
            ');
            $insAttempt->execute([
                'user_id' => $userId,
                'quiz_id' => $quizId,
                'score' => $correctCount,
                'total' => $totalQuestions,
                'passed' => $passed ? 1 : 0,
            ]);

            $xpAwarded = 0;
            if ($passed) {
                // Complete lesson & award XP
                $lessonId = (int)$quiz['lesson_id'];
                $insProg = $db->prepare('
                    INSERT INTO user_learning_progress (user_id, lesson_id, is_completed, completed_at)
                    VALUES (:user_id, :lesson_id, TRUE, NOW())
                    ON DUPLICATE KEY UPDATE is_completed = TRUE
                ');
                $insProg->execute(['user_id' => $userId, 'lesson_id' => $lessonId]);

                $xpAwarded = 300;
                $updProfile = $db->prepare('
                    UPDATE user_profiles
                    SET xp = xp + :xp, level = FLOOR((xp + :xp) / 500) + 1
                    WHERE user_id = :user_id
                ');
                $updProfile->execute(['xp' => $xpAwarded, 'user_id' => $userId]);

                // Unlock Binary Searcher achievement
                $ach = $db->prepare('
                    INSERT IGNORE INTO user_achievements (user_id, achievement_id, unlocked_at)
                    SELECT :user_id, id, NOW() FROM achievements WHERE code = "binary_searcher"
                ');
                $ach->execute(['user_id' => $userId]);
            }

            $db->commit();

            Response::success('Quiz evaluated.', [
                'score' => $correctCount,
                'total_questions' => $totalQuestions,
                'percentage' => $percentage,
                'passed' => $passed,
                'pass_percentage' => (int)$quiz['pass_percentage'],
                'xp_awarded' => $xpAwarded,
                'review' => $review
            ]);
        } catch (Exception $e) {
            $db->rollBack();
            Response::error('Failed to submit quiz.', ['error' => 'Database error'], 500);
        }
    }
}

