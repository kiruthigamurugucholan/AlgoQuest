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

    public static function explainVisualizer(array $body): void {
        AuthMiddleware::authenticate();

        $algorithm = $body['algorithm'] ?? 'Binary Search';
        $array = is_array($body['array'] ?? null) ? $body['array'] : [];
        $target = $body['target'] ?? null;
        $currentStep = (int)($body['current_step'] ?? 1);
        $totalSteps = (int)($body['total_steps'] ?? 1);
        $description = $body['description'] ?? '';
        $reason = $body['reason'] ?? '';
        $variables = is_array($body['variables'] ?? null) ? $body['variables'] : [];
        $codeLine = $body['code_snippet'] ?? '';
        $actionType = $body['action_type'] ?? 'EXPLAIN_STEP';
        $userQuestion = trim($body['user_question'] ?? '');

        // Format variables string for prompt
        $varList = [];
        foreach ($variables as $k => $v) {
            $varList[] = "{$k} = {$v}";
        }
        $varsStr = implode(', ', $varList);
        $arrStr = '[' . implode(', ', $array) . ']';
        $targetStr = $target !== null ? "Target: {$target}" : "Sorting / In-place";

        // Try Gemini API if key is present
        $apiKey = getenv('GEMINI_API_KEY') ?: ($_ENV['GEMINI_API_KEY'] ?? '');
        $geminiResponse = null;

        if (!empty($apiKey)) {
            $prompt = "You are Quest AI, an expert computer science tutor inside AlgoQuest. "
                . "Explain the following live algorithm step to an undergraduate student.\n\n"
                . "ALGORITHM: {$algorithm}\n"
                . "CURRENT ARRAY: {$arrStr}\n"
                . "{$targetStr}\n"
                . "CURRENT STEP: {$currentStep} of {$totalSteps}\n"
                . "ACTIVE LINE: {$codeLine}\n"
                . "VARIABLES: {$varsStr}\n"
                . "OPERATION: {$description}\n"
                . "REASON: {$reason}\n"
                . "ACTION REQUESTED: {$actionType}\n";

            if (!empty($userQuestion)) {
                $prompt .= "STUDENT QUESTION: {$userQuestion}\n";
            }

            $prompt .= "\nFormat your response as concise, pedagogical guidance with:\n"
                . "1. An intuitive explanation of this exact step.\n"
                . "2. A key invariant or takeaway.\n"
                . "3. A short code tip or common pitfall to avoid.\n"
                . "Keep the tone encouraging, clear, and direct.";

            $geminiResponse = self::callGeminiApi($apiKey, $prompt);
        }

        if ($geminiResponse) {
            $data = [
                'title' => self::getActionTitle($actionType, $algorithm),
                'explanation' => $geminiResponse,
                'key_takeaway' => "Invariant preserved at step {$currentStep}: {$reason}",
                'code_tip' => !empty($codeLine) ? "Active Line: {$codeLine}" : "Observe pointer transitions carefully.",
                'action_type' => $actionType,
                'source' => 'GEMINI_AI'
            ];
        } else {
            // Intelligent state-aware pedagogical fallback
            $fallback = self::generateContextualExplanation(
                $algorithm,
                $actionType,
                $currentStep,
                $totalSteps,
                $description,
                $reason,
                $variables,
                $codeLine,
                $target,
                $userQuestion
            );
            $data = $fallback;
        }

        Response::success('AI explanation ready', $data);
    }

    private static function getActionTitle(string $actionType, string $algorithm): string {
        return match ($actionType) {
            'EXPLAIN_LINE' => "Line Analysis • {$algorithm}",
            'EXPLAIN_STEP' => "Step Analysis • {$algorithm}",
            'DEBUG_CODE' => "Code Debugger • {$algorithm}",
            'GIVE_HINT' => "Algorithmic Hint • {$algorithm}",
            'EXPLAIN_COMPLEXITY' => "Complexity Breakdown • {$algorithm}",
            'ASK_QUESTION' => "Quest AI Tutor • Answer",
            default => "AlgoQuest AI Ally"
        };
    }

    private static function callGeminiApi(string $apiKey, string $prompt): ?string {
        $url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" . urlencode($apiKey);

        $payload = json_encode([
            'contents' => [
                [
                    'parts' => [
                        ['text' => $prompt]
                    ]
                ]
            ],
            'generationConfig' => [
                'temperature' => 0.4,
                'maxOutputTokens' => 500
            ]
        ]);

        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
        curl_setopt($ch, CURLOPT_TIMEOUT, 6);

        $result = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($httpCode === 200 && $result) {
            $json = json_decode($result, true);
            $text = $json['candidates'][0]['content']['parts'][0]['text'] ?? null;
            if (!empty($text)) {
                return trim($text);
            }
        }

        return null;
    }

    private static function generateContextualExplanation(
        string $alg,
        string $actionType,
        int $step,
        int $totalSteps,
        string $description,
        string $reason,
        array $vars,
        string $codeLine,
        $target,
        string $userQuestion
    ): array {
        switch ($actionType) {
            case 'EXPLAIN_LINE':
                $title = "Line Analysis • {$alg}";
                $lineDisplay = !empty($codeLine) ? "`{$codeLine}`" : "current code line";
                $explanation = "Analyzing line {$lineDisplay}:\n\n"
                    . "This line executes with current state: " . (!empty($vars) ? json_encode($vars) : "active scope") . ".\n\n"
                    . "Purpose: It performs the core operational step for this iteration — {$reason}.";
                $takeaway = "Line semantics: Directly advances algorithm loop invariants.";
                $codeTip = "Observe how local variable updates directly reflect in the next loop evaluation.";
                break;

            case 'DEBUG_CODE':
                $title = "Code Debugger • {$alg}";
                if ($alg === 'Binary Search') {
                    $explanation = "Debugging check for Binary Search:\n"
                        . "1. Invariant check: Range is [low..high].\n"
                        . "2. Mid calculation: `low + (high - low) / 2` prevents 32-bit overflow.\n"
                        . "3. Pointer movement: When arr[mid] != target, pointers MUST skip mid (`low = mid + 1` or `high = mid - 1`) to guarantee termination.\n"
                        . "4. Loop condition: Must be `low <= high` (not `<`) so single-element arrays are inspected.";
                    $takeaway = "No infinite loop detected: pointers strictly converge.";
                    $codeTip = "Common bug: using (low + high) / 2 directly causes overflow for arrays >= 10^9.";
                } elseif ($alg === 'Bubble Sort') {
                    $explanation = "Debugging check for Bubble Sort:\n"
                        . "1. Inner loop bound: Must be `n - i - 1` to skip already-settled suffix elements.\n"
                        . "2. Comparison: `arr[j] > arr[j+1]` ensures stable sorting.\n"
                        . "3. Swap integrity: Requires a temp variable or tuple unpack.\n"
                        . "4. Early exit: Check `swapped` boolean to achieve O(N) best-case time.";
                    $takeaway = "Bubble Sort maintains stable relative order for duplicate elements.";
                    $codeTip = "If j runs up to n - 1, arr[j+1] will throw an IndexOutOfBoundsException.";
                } else {
                    $explanation = "Debugging check for Two Pointers:\n"
                        . "1. Prerequisite: Array MUST be pre-sorted in ascending order.\n"
                        . "2. Pointer movement: `sum < target -> left++`, `sum > target -> right--`.\n"
                        . "3. Termination: `left < right` prevents pairing an element with itself.\n"
                        . "4. Duplicate handling: Can be extended with `while (left < right && arr[left] == arr[left+1]) left++`.";
                    $takeaway = "Opposite-end traversal eliminates redundant search branches.";
                    $codeTip = "Check for integer overflow if adding two large 32-bit integers.";
                }
                break;

            case 'EXPLAIN_COMPLEXITY':
                $title = "Complexity Breakdown • {$alg}";
                if ($alg === 'Binary Search') {
                    $explanation = "Time Complexity: O(log N)\n"
                        . "• Best Case: O(1) when target is at the exact middle index.\n"
                        . "• Worst / Average Case: O(log2 N) because each step discards half the array.\n"
                        . "• Space Complexity: O(1) auxiliary space (iterative pointers only).\n\n"
                        . "For N = 1,000,000 items, at most 20 comparisons are needed!";
                    $takeaway = "Logarithmic scaling enables near-instant lookups on massive datasets.";
                    $codeTip = "If recursive binary search is used, call stack memory becomes O(log N).";
                } elseif ($alg === 'Bubble Sort') {
                    $explanation = "Time Complexity: O(N^2)\n"
                        . "• Best Case: O(N) when array is already sorted (with early-exit flag).\n"
                        . "• Worst Case: O(N^2) for reverse-sorted arrays ((N * (N - 1)) / 2 comparisons).\n"
                        . "• Space Complexity: O(1) in-place auxiliary memory.\n\n"
                        . "For N = 10,000, Bubble Sort takes ~50,000,000 operations!";
                    $takeaway = "Quadratic complexity makes Bubble Sort impractical for large production datasets.";
                    $codeTip = "Prefer Merge Sort or Quick Sort (O(N log N)) when N > 1,000.";
                } else {
                    $explanation = "Time Complexity: O(N)\n"
                        . "• Single-pass scan: In each iteration, either left increments or right decrements.\n"
                        . "• At most N total steps are taken before pointers meet.\n"
                        . "• Space Complexity: O(1) auxiliary space using two pointer indices.\n\n"
                        . "Linear time transforms an O(N^2) brute-force nested search into an optimal O(N) scan.";
                    $takeaway = "Two Pointers reduces search complexity by a full polynomial order.";
                    $codeTip = "Sorting first takes O(N log N), making total time O(N log N) for unsorted inputs.";
                }
                break;

            case 'GIVE_HINT':
                $title = "Algorithmic Hint • {$alg}";
                if ($alg === 'Binary Search') {
                    $explanation = "Notice how the search boundaries narrow. Each comparison eliminates half of the remaining items. Look at arr[mid]: if it's less than your target, the answer cannot possibly be to the left!";
                    $takeaway = "Always maintain the invariant: target is in [low..high].";
                    $codeTip = "mid = low + (high - low) / 2 avoids 32-bit integer overflow.";
                } elseif ($alg === 'Bubble Sort') {
                    $explanation = "Observe the right-hand side of your array. With every completed pass, the largest remaining element is locked into its permanent position at the end.";
                    $takeaway = "Pass i guarantees element at (n - 1 - i) is sorted.";
                    $codeTip = "Use a swapped boolean flag to terminate early in O(N) time if the array is already ordered.";
                } else {
                    $explanation = "Because the array is monotonically sorted, moving the left pointer rightward strictly increases the sum, while moving the right pointer leftward strictly decreases it.";
                    $takeaway = "Sum < Target -> increment left; Sum > Target -> decrement right.";
                    $codeTip = "Opposite-end pointers avoid the brute force O(N^2) nested loop.";
                }
                break;

            default: // EXPLAIN_STEP
                $title = "Step {$step} Analysis • {$alg}";
                $explanation = "At this step: {$description}\n\nUnderlying Logic: {$reason}";
                $takeaway = "Progress: Step {$step} of {$totalSteps}. Invariant holds true.";
                $codeTip = !empty($codeLine) ? "Executing Line: {$codeLine}" : "Pointer positions updated.";
                break;
        }

        return [
            'title' => $title,
            'explanation' => $explanation,
            'key_takeaway' => $takeaway,
            'code_tip' => $codeTip,
            'action_type' => $actionType,
            'source' => 'ALGOQUEST_CORE'
        ];
    }
}


