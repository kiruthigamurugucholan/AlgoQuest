<?php
// ========================================================
// AlgoQuest — AI Chat Controller
// Server-backed DSA algorithmic tutor with history & achievements
// ========================================================

require_once __DIR__ . '/../config/database.php';
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../middleware/AuthMiddleware.php';

class AiChatController {
    public static function chat(array $body): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $message = trim($body['message'] ?? '');

        if (empty($message)) {
            Response::error('Message cannot be empty.', ['message' => 'Query is required.'], 400);
        }

        $db = Database::getConnection();

        // 1. Find or create conversation
        $convStmt = $db->prepare('SELECT id FROM ai_conversations WHERE user_id = :user_id ORDER BY id DESC LIMIT 1');
        $convStmt->execute(['user_id' => $userId]);
        $conv = $convStmt->fetch();

        if ($conv) {
            $convId = (int)$conv['id'];
        } else {
            $insConv = $db->prepare('INSERT INTO ai_conversations (user_id, title) VALUES (:user_id, "DSA Tutoring")');
            $insConv->execute(['user_id' => $userId]);
            $convId = (int)$db->lastInsertId();
        }

        // 2. Insert user message
        $insMsg = $db->prepare('
            INSERT INTO ai_messages (conversation_id, user_id, sender, message_text, created_at)
            VALUES (:conv_id, :user_id, "user", :text, NOW())
        ');
        $insMsg->execute(['conv_id' => $convId, 'user_id' => $userId, 'text' => $message]);

        // 3. Intelligent DSA Tutor Response Logic
        $lower = strtolower($message);
        $aiText = '';
        $codeSnippet = null;
        $tags = [];

        if (str_contains($lower, 'binary search') || str_contains($lower, 'divide and conquer') || str_contains($lower, 'bs')) {
            $aiText = "Binary Search operates by repeatedly dividing a sorted interval in half.\n\nKey intuition: Because the array is ordered, comparing the middle element lets you discard 50% of candidate items in a single comparison!\n\nInvariant: The target always resides in [low..high]. Compute mid = low + (high - low) / 2 to prevent integer overflow.";
            $codeSnippet = "fun binarySearch(arr: IntArray, target: Int): Int {\n    var low = 0; var high = arr.size - 1\n    while (low <= high) {\n        val mid = low + (high - low) / 2\n        if (arr[mid] == target) return mid\n        else if (arr[mid] < target) low = mid + 1\n        else high = mid - 1\n    }\n    return -1\n}";
            $tags = ["O(log N)", "Divide & Conquer", "Sorted Array"];
        } elseif (str_contains($lower, 'log') || str_contains($lower, 'complexity') || str_contains($lower, 'big o')) {
            $aiText = "O(log N) represents logarithmic time complexity (log base 2). Every step cuts your problem space in half.\n\nScale comparison:\n• 1,000 items -> ~10 checks\n• 1,000,000 items -> ~20 checks\n• 1,000,000,000 items -> ~30 checks!\n\nThis is why binary search scales effortlessly compared to O(N) linear scan.";
            $codeSnippet = "// 2^k = N -> k = log2(N)\n// Double the input size, add only 1 extra comparison step!";
            $tags = ["Complexity", "Big-O", "Math"];
        } elseif (str_contains($lower, 'two pointer') || str_contains($lower, 'sliding window')) {
            $aiText = "The Two Pointers pattern replaces nested O(N^2) loops with a single O(N) pass by moving two indices in coordinated directions.\n\nCommon variants:\n1. Opposite ends: Converging inward on sorted arrays (e.g., Two Sum II).\n2. Fast & Slow: Cycle detection in linked lists (Floyd's Tortoise and Hare).\n3. Sliding Window: Dynamic subarray intervals.";
            $codeSnippet = "var left = 0; var right = arr.size - 1\nwhile (left < right) {\n    val sum = arr[left] + arr[right]\n    if (sum == target) return true\n    else if (sum < target) left++\n    else right--\n}";
            $tags = ["Two Pointers", "O(N) Time", "Array Optimization"];
        } elseif (str_contains($lower, 'bubble sort') || str_contains($lower, 'sorting')) {
            $aiText = "Bubble Sort repeatedly traverses the array, comparing adjacent elements and swapping out-of-order pairs. After iteration `i`, the `i`-th largest element has bubbled into its final position at the end.\n\nTime Complexity: O(N^2) worst/average, O(N) best with swapped flag.";
            $codeSnippet = "for (i in 0 until n - 1) {\n    var swapped = false\n    for (j in 0 until n - i - 1) {\n        if (arr[j] > arr[j + 1]) {\n            val temp = arr[j]\n            arr[j] = arr[j + 1]\n            arr[j + 1] = temp\n            swapped = true\n        }\n    }\n    if (!swapped) break\n}";
            $tags = ["Sorting", "O(N^2)", "In-Place"];
        } elseif (str_contains($lower, 'tree') || str_contains($lower, 'graph') || str_contains($lower, 'dfs') || str_contains($lower, 'bfs')) {
            $aiText = "Graph and Tree traversals explore connected networks:\n\n• BFS (Breadth-First): Uses a Queue (FIFO) to explore nodes level-by-level. Ideal for shortest-path in unweighted graphs.\n• DFS (Depth-First): Uses a Stack (or recursion) to explore as deep as possible before backtracking.";
            $codeSnippet = "// BFS level-order traversal pattern\nval queue = ArrayDeque<Node>()\nqueue.add(root)\nwhile (queue.isNotEmpty()) {\n    val curr = queue.removeFirst()\n    // Process curr...\n    curr.left?.let { queue.add(it) }\n    curr.right?.let { queue.add(it) }\n}";
            $tags = ["Graphs", "Trees", "BFS / DFS"];
        } else {
            $aiText = "Great question! When approaching any DSA problem:\n1. Identify the input structure (sorted? unique? positive integers?).\n2. Determine constraints (N <= 10^5 indicates an O(N) or O(N log N) solution is expected).\n3. Formulate the state transition or invariant before coding.\n\nWould you like me to walk through a concrete visual trace or code example for this?";
            $tags = ["AlgoQuest AI", "Problem Solving", "Strategy"];
        }

        // 4. Save AI response
        $insAi = $db->prepare('
            INSERT INTO ai_messages (conversation_id, user_id, sender, message_text, code_snippet, tags_json, created_at)
            VALUES (:conv_id, :user_id, "ai", :text, :code, :tags, NOW())
        ');
        $insAi->execute([
            'conv_id' => $convId,
            'user_id' => $userId,
            'text' => $aiText,
            'code' => $codeSnippet,
            'tags' => json_encode($tags),
        ]);
        $aiMsgId = (int)$db->lastInsertId();

        // 5. Check if user unlocked AI Explorer achievement (>= 10 queries)
        $countStmt = $db->prepare('SELECT COUNT(*) FROM ai_messages WHERE user_id = :user_id AND sender = "user"');
        $countStmt->execute(['user_id' => $userId]);
        $userQueriesCount = (int)$countStmt->fetchColumn();

        if ($userQueriesCount >= 5) {
            $ach = $db->prepare('
                INSERT IGNORE INTO user_achievements (user_id, achievement_id, unlocked_at)
                SELECT :user_id, id, NOW() FROM achievements WHERE code = "ai_explorer"
            ');
            $ach->execute(['user_id' => $userId]);
        }

        Response::success('AI response', [
            'message' => [
                'id' => (string)$aiMsgId,
                'sender' => 'ai',
                'text' => $aiText,
                'code_snippet' => $codeSnippet,
                'timestamp' => 'Just now',
                'tags' => $tags
            ]
        ]);
    }

    public static function getHistory(): void {
        $user = AuthMiddleware::authenticate();
        $userId = (int)$user['id'];
        $db = Database::getConnection();

        $stmt = $db->prepare('
            SELECT id, sender, message_text as text, code_snippet, tags_json, created_at
            FROM ai_messages
            WHERE user_id = :user_id
            ORDER BY id ASC
            LIMIT 50
        ');
        $stmt->execute(['user_id' => $userId]);
        $rows = $stmt->fetchAll();

        $messages = [];
        foreach ($rows as $r) {
            $messages[] = [
                'id' => (string)$r['id'],
                'sender' => $r['sender'],
                'text' => $r['text'],
                'code_snippet' => $r['code_snippet'],
                'timestamp' => date('g:i A', strtotime($r['created_at'])),
                'tags' => json_decode($r['tags_json'] ?? '[]', true) ?: []
            ];
        }

        Response::success('Chat history', ['messages' => $messages]);
    }
}

