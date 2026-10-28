-- ========================================================
-- AlgoQuest — Seed Data (MySQL / MariaDB)
-- Safe, repeatable inserts for DSA curriculum, quizzes, and achievements
-- ========================================================

USE algoquest_db;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Topics (7 Chapters corresponding to QuestMap)
INSERT INTO topics (id, chapter_number, title, subtitle, description, total_xp, order_index, icon_type) VALUES
(1, 1, 'Arrays & Memory', 'Continuous buffers, indexing, and memory layout', 'Master the fundamentals of contiguous memory, cache locality, and dynamic resizing in modern computing.', 400, 1, 'array'),
(2, 2, 'Two Pointers & Sliding', 'Optimal array traversals and windowing', 'Transform brute-force O(N^2) searches into optimal O(N) single-pass scans using directional pointers.', 500, 2, 'pointer'),
(3, 3, 'Searching Algorithms', 'Divide and conquer search spaces', 'Learn how cutting search boundaries in half reduces linear lookups into ultra-fast logarithmic operations.', 900, 3, 'search'),
(4, 4, 'Sorting & Divide/Conquer', 'Order from chaos: swaps, partitions, and merges', 'Understand comparison-based sorting barriers and discover why O(N log N) is the theoretical limit.', 650, 4, 'sort'),
(5, 5, 'Linked Lists & Pointers', 'Node links, cycles, and reversals', 'Build dynamic non-contiguous collections, detect fast-and-slow runner cycles, and invert pointer directions.', 700, 5, 'link'),
(6, 6, 'Trees & Binary Trees', 'Hierarchical data, traversals, and balance', 'Climb hierarchical structures, master recursion, and inspect BST invariants and tree traversals.', 800, 6, 'tree'),
(7, 7, 'Graphs & BFS / DFS', 'Networks, paths, and connectivity', 'Model complex real-world relationships and traverse frontiers using Depth-First and Breadth-First Search.', 1000, 7, 'graph')
ON DUPLICATE KEY UPDATE title=VALUES(title), subtitle=VALUES(subtitle), description=VALUES(description);

-- 2. Lessons for Chapter 03 (Searching Algorithms)
INSERT INTO lessons (id, topic_id, title, duration_minutes, xp_reward, lesson_type, content, order_index) VALUES
(1, 3, 'Linear vs Binary Search: The 1M Step Difference', 8, 100, 'READING', 'When searching through unsorted data, linear scan is mandatory: inspecting every single item takes O(N) operations. But on sorted arrays, binary search cuts the remaining items in half every iteration. For 1,000,000 items, linear scan takes up to 1,000,000 checks, while binary search takes at most 20 checks!', 1),
(2, 3, 'Binary Search Core Implementation & Invariants', 12, 150, 'READING', 'The invariant of binary search: the target must lie strictly within [low..high]. In each step, mid = low + (high - low) / 2 prevents 32-bit integer overflow. Compare arr[mid] to target, and narrow the boundary: low = mid + 1 or high = mid - 1.', 2),
(3, 3, 'Interactive Visualizer: Step Through Binary Search', 15, 150, 'VISUALIZER', 'Step through every pointer movement and comparison in the Visualizer Lab. Watch how low, mid, and high pointers converge on the target value.', 3),
(4, 3, 'Search in Rotated Sorted Array', 18, 200, 'READING', 'In a rotated sorted array (e.g., [4, 5, 6, 7, 0, 1, 2]), at least one half is always strictly sorted. Determine which half is sorted first, then check if target falls inside that range.', 4),
(5, 3, 'Chapter 03 Mastery Test & Challenge', 10, 300, 'QUIZ', 'Test your intuition and edge-case handling on searching algorithms to complete Chapter 03 and earn your badge.', 5)
ON DUPLICATE KEY UPDATE title=VALUES(title), xp_reward=VALUES(xp_reward);

-- 3. Algorithms for Visualizer Lab
INSERT INTO algorithms (id, lesson_id, slug, name, time_complexity, space_complexity, description, code_snippet, default_data_json) VALUES
(1, 3, 'binary-search', 'Binary Search', 'O(log N)', 'O(1)', 'Finds the position of a target value within a sorted array by repeatedly dividing the search interval in half.', 
'fun binarySearch(arr: IntArray, target: Int): Int {\n    var low = 0; var high = arr.size - 1\n    while (low <= high) {\n        val mid = low + (high - low) / 2\n        if (arr[mid] == target) return mid\n        else if (arr[mid] < target) low = mid + 1\n        else high = mid - 1\n    }\n    return -1\n}', 
'{"array": [2, 5, 8, 12, 16, 23, 38, 56, 72, 91], "target": 23}'),

(2, NULL, 'bubble-sort', 'Bubble Sort', 'O(N^2)', 'O(1)', 'Repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order.',
'fun bubbleSort(arr: IntArray) {\n    val n = arr.size\n    for (i in 0 until n - 1) {\n        for (j in 0 until n - i - 1) {\n            if (arr[j] > arr[j + 1]) {\n                val temp = arr[j]\n                arr[j] = arr[j + 1]\n                arr[j + 1] = temp\n            }\n        }\n    }\n}',
'{"array": [25, 12, 8, 34, 19, 6, 42, 15]}'),

(3, NULL, 'two-pointers', 'Two Pointers (Target Sum)', 'O(N)', 'O(1)', 'Two pointers start at opposite ends of a sorted array and move inwards based on the sum comparison.',
'fun twoSumSorted(arr: IntArray, target: Int): Pair<Int, Int>? {\n    var left = 0; var right = arr.size - 1\n    while (left < right) {\n        val sum = arr[left] + arr[right]\n        if (sum == target) return Pair(left, right)\n        else if (sum < target) left++\n        else right--\n    }\n    return null\n}',
'{"array": [1, 3, 4, 7, 10, 14, 18, 22], "target": 17}')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), code_snippet=VALUES(code_snippet);

-- 4. Quizzes (Chapter 03 Lesson 5 Mastery Test)
INSERT INTO quizzes (id, lesson_id, title, description, pass_percentage) VALUES
(1, 5, 'Chapter 03: Searching Mastery Challenge', 'Prove your mastery of binary search, boundary invariants, and logarithmic complexity.', 60)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 5. Quiz Questions
INSERT INTO quiz_questions (id, quiz_id, question_text, explanation, order_index) VALUES
(1, 1, 'What is the maximum number of comparisons Binary Search takes on a sorted array of 1,024 elements?', 'log2(1024) = 10. Each step eliminates half the elements, requiring at most 10 steps.', 1),
(2, 1, 'Why is calculating "mid = low + (high - low) / 2" preferred over "(low + high) / 2"?', 'In 32-bit signed integers, low + high can exceed 2,147,483,647 and overflow into negative numbers.', 2),
(3, 1, 'What is the required prerequisite condition for applying Binary Search to an array?', 'Binary Search requires random index access (O(1)) and monotonically sorted elements.', 3),
(4, 1, 'What is the time complexity of searching for an element in an unsorted array of size N?', 'Without order, every element must potentially be checked, yielding O(N) linear time.', 4),
(5, 1, 'If an array has duplicate target values, standard Binary Search is guaranteed to return which index?', 'Standard binary search returns ANY matching index; finding the first or last occurrence requires custom boundary refinement.', 5)
ON DUPLICATE KEY UPDATE question_text=VALUES(question_text), explanation=VALUES(explanation);

-- 6. Quiz Options
INSERT INTO quiz_options (id, question_id, option_text, is_correct) VALUES
-- Q1 options
(1, 1, '10 comparisons', TRUE),
(2, 1, '512 comparisons', FALSE),
(3, 1, '1,024 comparisons', FALSE),
(4, 1, '20 comparisons', FALSE),

-- Q2 options
(5, 2, 'It prevents integer overflow when low + high exceeds INT_MAX', TRUE),
(6, 2, 'It executes 2x faster on CPU arithmetic units', FALSE),
(7, 2, 'It works with floating point numbers', FALSE),
(8, 2, 'It is purely cosmetic with no difference', FALSE),

-- Q3 options
(9, 3, 'The array must be sorted', TRUE),
(10, 3, 'All numbers must be even', FALSE),
(11, 3, 'The array length must be a power of two', FALSE),
(12, 3, 'The elements must be unique with no duplicates', FALSE),

-- Q4 options
(13, 4, 'O(N)', TRUE),
(14, 4, 'O(log N)', FALSE),
(15, 4, 'O(1)', FALSE),
(16, 4, 'O(N^2)', FALSE),

-- Q5 options
(17, 5, 'Any matching index where target is found', TRUE),
(18, 5, 'Always the first occurrence', FALSE),
(19, 5, 'Always the last occurrence', FALSE),
(20, 5, 'Throws an error if duplicates exist', FALSE)
ON DUPLICATE KEY UPDATE option_text=VALUES(option_text), is_correct=VALUES(is_correct);

-- 7. Quests
INSERT INTO quests (id, title, description, xp_reward, required_steps, quest_type) VALUES
(1, 'Find it. Faster.', 'Master binary search, step through the visualizer, and test your instincts.', 150, 3, 'DAILY'),
(2, 'Daily Code Warmup', 'Solve 2 problems or complete 1 lesson today.', 100, 2, 'DAILY'),
(3, 'Weekend Algorithm Marathon', 'Explore 3 different algorithm visualizers.', 300, 3, 'WEEKLY')
ON DUPLICATE KEY UPDATE title=VALUES(title), xp_reward=VALUES(xp_reward);

-- 8. Achievements
INSERT INTO achievements (id, code, title, description, icon_emoji, required_criteria, xp_reward) VALUES
(1, 'streak_master', 'Streak Master', 'Maintained a 7-day learning streak', '🔥', 'streak >= 7', 200),
(2, 'binary_searcher', 'Binary Searcher', 'Mastered Binary Search and completed Chapter 03', '⚡', 'complete_topic_3', 250),
(3, 'ai_explorer', 'AI Explorer', 'Asked 10 questions to Quest AI', '🤖', 'ai_questions >= 10', 150),
(4, 'array_legend', 'Array Legend', 'Completed the Arrays & Memory chapter', '🎯', 'complete_topic_1', 200),
(5, 'graph_pioneer', 'Graph Pioneer', 'Unlocked Chapter 07 (Graphs & BFS/DFS)', '🌐', 'unlock_topic_7', 350),
(6, 'dp_titan', 'DP Titan', 'Conquered Dynamic Programming challenges', '🛡️', 'complete_topic_dp', 500)
ON DUPLICATE KEY UPDATE title=VALUES(title), description=VALUES(description);

SET FOREIGN_KEY_CHECKS = 1;

