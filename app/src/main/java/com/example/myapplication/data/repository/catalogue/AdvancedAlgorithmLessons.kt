package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object AdvancedAlgorithmLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Greedy Algorithms Paradigm
        DsaLessonContent(
            lessonId = "adv_greedy_l1",
            subtopicId = "adv_greedy",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Greedy Algorithms Paradigm",
            subtitle = "Making locally optimal choices hoping to achieve a globally optimal solution",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N log N) sorting, O(N) selection",
            spaceComplexity = "O(1) to O(N)",
            learningObjectives = listOf(
                "Understand the two pillars of Greedy Algorithms: Greedy Choice Property and Optimal Substructure.",
                "Solve the Activity Selection / Interval Scheduling problem in O(N log N) time.",
                "Learn why Greedy works on Fractional Knapsack but FAILS on 0/1 Knapsack.",
                "Know when to switch from Greedy to Dynamic Programming."
            ),
            sections = listOf(
                LessonSection(
                    id = "greedy_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Greedy Algorithm?",
                    body = "A Greedy Algorithm builds a solution piece-by-piece, always choosing the immediate next piece that offers the most obvious and immediate benefit (locally optimal choice) without ever reconsidering past decisions.\n\nUnlike Dynamic Programming, greedy algorithms never backtrack. For a greedy algorithm to guarantee a globally optimal result, the problem must exhibit:\n1. Greedy Choice Property: A global optimum can be reached by making locally optimal choices.\n2. Optimal Substructure: An optimal solution contains optimal solutions to its sub-problems.",
                    bulletPoints = listOf(
                        "Speed: Extremely fast (often O(N log N) due to initial sorting).",
                        "Caveat: If greedy choice property does not hold, greedy choices produce sub-optimal answers."
                    )
                ),
                LessonSection(
                    id = "activity_selection_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Activity Selection: Sorting by Finish Time",
                    body = "Problem: Given N activities with start and end times, select the maximum number of non-overlapping activities.\n\nGreedy Rule: ALWAYS pick the activity that FINISHES EARLIEST!\nWhy? Finishing earliest leaves the maximum amount of remaining time available for future activities.\n\n1. Sort activities by finish time: O(N log N).\n2. Pick first activity.\n3. For each subsequent activity, if its start time >= finish time of the last chosen activity, select it!\nRuntime: O(N log N) sorting + O(N) single scan.",
                    bulletPoints = listOf(
                        "Sorting by start time fails; sorting by duration fails; sorting by FINISH TIME is mathematically optimal."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Activity Selection Problem in Kotlin",
                    language = "KOTLIN",
                    code = """data class Activity(val start: Int, val finish: Int)

fun selectMaxActivities(activities: List<Activity>): List<Activity> {
    if (activities.isEmpty()) return emptyList()
    
    // 1. Sort by finish time in ascending order
    val sorted = activities.sortedBy { it.finish }
    val selected = mutableListOf<Activity>()
    
    // 2. Greedily pick first activity
    selected.add(sorted[0])
    var lastFinish = sorted[0].finish
    
    // 3. Pick next non-overlapping activity
    for (i in 1 until sorted.size) {
        if (sorted[i].start >= lastFinish) {
            selected.add(sorted[i])
            lastFinish = sorted[i].finish
        }
    }
    return selected
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 7", "Sort activities by finish time in O(N log N)."),
                        CodeStepExplanation(2, "Lines 11-12", "Select the earliest-finishing activity to maximize remaining available time."),
                        CodeStepExplanation(3, "Lines 15-20", "Scan through remaining activities; add if start time >= finish time of previous.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Greedy algorithms make locally optimal decisions without backtracking.",
                "Only guarantees global optimum when the problem satisfies the Greedy Choice Property.",
                "Activity Selection maximizes tasks by sorting strictly by finish time.",
                "Greedy works for Fractional Knapsack, Dijkstra, Huffman Coding, and Kruskal's MST."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_adv_grd_1",
                    question = "Why does the Activity Selection algorithm sort by FINISH TIME rather than start time or duration?",
                    options = listOf(
                        "Finishing earliest frees up the maximum amount of remaining time to schedule subsequent activities",
                        "Because sorting by start time uses more memory",
                        "Because finish times are always even numbers",
                        "To prevent stack overflow"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Choosing the activity that finishes earliest leaves the largest possible remaining time window for future activities, satisfying the Greedy Choice Property."
                ),
                LessonPracticeQuestion(
                    id = "pq_adv_grd_2",
                    question = "Why does a greedy algorithm fail to solve the 0/1 Knapsack problem (where items cannot be divided)?",
                    options = listOf(
                        "Picking items with the highest value-to-weight ratio can leave empty space that could have been filled with a more valuable combination of other items",
                        "Because 0/1 Knapsack requires negative weights",
                        "Because greedy algorithms cannot sort numbers",
                        "Because arrays cannot store knapsacks"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In 0/1 knapsack, taking an item with high density might prevent taking two slightly lower-density items that together yield a higher total value. Dynamic Programming is required."
                )
            )
        ),

        // 2. Dynamic Programming & Memoization
        DsaLessonContent(
            lessonId = "adv_dp_l1",
            subtopicId = "adv_dp",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Dynamic Programming & Memoization",
            subtitle = "Overlapping subproblems, optimal substructure, and Top-Down vs. Bottom-Up paradigms",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N * W) pseudo-polynomial for Knapsack",
            spaceComplexity = "O(N * W) or O(W)",
            learningObjectives = listOf(
                "Identify the two prerequisites for Dynamic Programming: Overlapping Subproblems and Optimal Substructure.",
                "Compare Top-Down (Memoization) vs. Bottom-Up (Tabulation).",
                "Formulate State Transitions: dp[i] = f(dp[i-1], dp[i-2]...).",
                "Solve the classic 0/1 Knapsack and Coin Change problems."
            ),
            sections = listOf(
                LessonSection(
                    id = "dp_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Dynamic Programming?",
                    body = "Dynamic Programming (DP) is an optimization method invented by Richard Bellman in the 1950s. It solves complex problems by breaking them down into simpler sub-problems, solving each sub-problem once, and storing their solutions in a table.\n\nWhenever the same sub-problem occurs again, its answer is simply looked up in O(1) time instead of being recomputed.",
                    bulletPoints = listOf(
                        "Overlapping Subproblems: The same smaller calculations are needed again and again.",
                        "Optimal Substructure: The optimal solution to the main problem is constructed from optimal solutions to sub-problems."
                    )
                ),
                LessonSection(
                    id = "topdown_vs_bottomup",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Top-Down (Memoization) vs. Bottom-Up (Tabulation)",
                    body = "1. Top-Down (Memoization):\n- Follows natural recursion.\n- Before doing work, check if `memo[state]` exists. If so, return it. Otherwise compute, store in `memo`, and return.\n- Intuitive to write, but incurs call stack overhead.\n\n2. Bottom-Up (Tabulation):\n- Iterative with no recursion.\n- Solves base cases first (e.g. `dp[0]`, `dp[1]`), then uses for-loops to build up to `dp[target]`.\n- Avoids recursion limits and enables space-optimization tricks (e.g., keeping only the last 2 variables).",
                    bulletPoints = listOf(
                        "Coin Change: Finding minimum coins to make sum S."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Coin Change (Minimum Coins) Bottom-Up DP in Kotlin",
                    language = "KOTLIN",
                    code = """fun coinChange(coins: IntArray, amount: Int): Int {
    // dp[i] = minimum coins needed to make amount i
    val maxVal = amount + 1
    val dp = IntArray(amount + 1) { maxVal }
    dp[0] = 0 // Base Case: 0 coins needed for amount 0
    
    // Build solutions from 1 up to amount
    for (i in 1..amount) {
        for (coin in coins) {
            if (coin <= i) {
                // State Transition: take min of not taking coin vs taking 1 coin
                dp[i] = minOf(dp[i], dp[i - coin] + 1)
            }
        }
    }
    return if (dp[amount] > amount) -1 else dp[amount]
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 3-5", "Initialize DP table filled with sentinel amount + 1; base case dp[0] = 0."),
                        CodeStepExplanation(2, "Lines 8-15", "For every target sum i from 1 to amount, test each coin and choose minimum."),
                        CodeStepExplanation(3, "Line 16", "Return dp[amount] or -1 if amount cannot be composed.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Dynamic programming eliminates redundant recalculation by caching subproblem results.",
                "Requires Overlapping Subproblems and Optimal Substructure.",
                "Top-Down uses recursion + memoization table; Bottom-Up uses iteration + DP array.",
                "Transforms exponential O(2^N) algorithms into polynomial O(N * K) runtimes."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_adv_dp_1",
                    question = "What are the two mandatory properties a problem must possess to be solvable using Dynamic Programming?",
                    options = listOf(
                        "Overlapping Subproblems and Optimal Substructure",
                        "Sorting and Binary Search",
                        "Linear Time and Constant Space",
                        "Greedy Choice Property and Divide-and-Conquer"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Dynamic programming requires Optimal Substructure (global optimum formed from subproblem optima) and Overlapping Subproblems (identical subproblems solved repeatedly)."
                ),
                LessonPracticeQuestion(
                    id = "pq_adv_dp_2",
                    question = "What is the primary advantage of Bottom-Up Tabulation over Top-Down Memoization?",
                    options = listOf(
                        "It eliminates call stack frame overhead, avoiding StackOverflowError and allowing O(1) space optimization",
                        "It always uses a hash table",
                        "It solves problems in O(1) time",
                        "It requires no loops"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Bottom-up tabulation uses standard iterative loops, eliminating recursion call stack depth constraints and frequently allowing the developer to discard older table rows to save memory."
                )
            )
        ),

        // 3. Backtracking Paradigm (N-Queens, Sudoku)
        DsaLessonContent(
            lessonId = "adv_backtracking_l1",
            subtopicId = "adv_backtracking",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Backtracking Paradigm",
            subtitle = "State-space tree exploration with pruning for constraint satisfaction problems",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N!) for N-Queens",
            spaceComplexity = "O(N) recursion stack",
            learningObjectives = listOf(
                "Understand the Backtracking paradigm: Choose, Explore, Unchoose (Backtrack).",
                "Learn Pruning: abandoning invalid branches as soon as constraints are violated.",
                "Solve the famous N-Queens problem.",
                "Apply backtracking to Sudoku, Permutations, and Subset generation."
            ),
            sections = listOf(
                LessonSection(
                    id = "backtracking_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Backtracking?",
                    body = "Backtracking is an algorithmic technique for solving constraint satisfaction problems by incrementally building candidates for solutions, and abandoning a candidate ('backtracking') as soon as it is determined that the candidate cannot possibly lead to a valid solution.\n\nWhile brute force generates all N^N possibilities, Backtracking prunes dead-end branches early, discarding millions of invalid configurations with a single check.",
                    bulletPoints = listOf(
                        "Choose: Make a tentative choice (e.g., place a queen on row r, col c).",
                        "Explore: Recursively attempt to solve the rest of the board.",
                        "Unchoose (Backtrack): Undo the choice (remove queen) so other paths can be explored."
                    )
                ),
                LessonSection(
                    id = "n_queens_concept",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The N-Queens Problem",
                    body = "Problem: Place N chess queens on an N x N chessboard so that no two queens attack each other (no two queens share the same row, column, or diagonal).\n\nSince each row must have exactly one queen:\n- Try placing a queen in row `r` at column `c` from 0 to N-1.\n- Check if column `c`, main diagonal `r - c`, and anti-diagonal `r + c` are free.\n- If safe: place queen, recurse to row `r + 1`.\n- If `r == N`: all N queens placed safely! Record solution.\n- Backtrack: remove queen and try the next column.",
                    bulletPoints = listOf(
                        "Pruning: Eliminates entire subtrees of invalid board placements."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "N-Queens Solution in Kotlin",
                    language = "KOTLIN",
                    code = """fun solveNQueens(n: Int): List<List<String>> {
    val solutions = mutableListOf<List<String>>()
    val board = Array(n) { CharArray(n) { '.' } }
    
    // HashSets for O(1) safety checks
    val cols = mutableSetOf<Int>()
    val diag1 = mutableSetOf<Int>() // r - c
    val diag2 = mutableSetOf<Int>() // r + c
    
    fun backtrack(r: Int) {
        if (r == n) {
            solutions.add(board.map { String(it) })
            return
        }
        for (c in 0 until n) {
            if (c in cols || (r - c) in diag1 || (r + c) in diag2) continue // Prune!
            
            // 1. Choose
            board[r][c] = 'Q'; cols.add(c); diag1.add(r - c); diag2.add(r + c)
            // 2. Explore
            backtrack(r + 1)
            // 3. Unchoose (Backtrack)
            board[r][c] = '.'; cols.remove(c); diag1.remove(r - c); diag2.remove(r + c)
        }
    }
    backtrack(0)
    return solutions
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 6-8", "Sets provide O(1) checks for columns and diagonals."),
                        CodeStepExplanation(2, "Line 16", "Pruning: skip invalid positions without exploring child subtrees."),
                        CodeStepExplanation(3, "Lines 19-23", "Classic backtracking triplet: Choose (place Q), Explore (backtrack(r+1)), Unchoose (remove Q).")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Backtracking builds candidates incrementally and abandons (prunes) branches upon constraint violation.",
                "Follows the canonical 3-step pattern: Choose -> Explore -> Unchoose.",
                "Massively out-performs brute force by pruning impossible state-space branches early.",
                "Solves N-Queens, Sudoku, graph coloring, and Hamiltonian paths."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_adv_bck_1",
                    question = "What is the primary advantage of Backtracking over pure Brute Force search?",
                    options = listOf(
                        "Pruning: backtracking abandons dead-end search branches early as soon as a constraint is violated",
                        "Backtracking is guaranteed to run in O(N) time",
                        "Backtracking eliminates recursion",
                        "Backtracking sorts the output"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Pure brute force evaluates every complete candidate state (e.g. all N^N placements). Backtracking checks constraints at each step and prunes entire subtrees of millions of invalid placements."
                ),
                LessonPracticeQuestion(
                    id = "pq_adv_bck_2",
                    question = "In the canonical Backtracking template, what does the 'Unchoose' step do?",
                    options = listOf(
                        "It reverses the state modification (e.g. removes the placed queen) so parent caller can try other alternatives",
                        "It throws an exception to terminate the program",
                        "It clears the entire solution list",
                        "It shuts down the thread"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Backtracking must restore the shared game board or state array to its exact previous condition before moving on to test the next alternative branch."
                )
            )
        ),

        // 4. Divide and Conquer Strategy
        DsaLessonContent(
            lessonId = "adv_divide_conquer_l1",
            subtopicId = "adv_divide_conquer",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Divide and Conquer Strategy",
            subtitle = "Recursive partitioning, subproblem combination, and the Master Theorem",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N log N)",
            spaceComplexity = "O(N) or O(log N)",
            learningObjectives = listOf(
                "Understand the 3 phases of Divide and Conquer: Divide, Conquer, and Combine.",
                "Contrast Divide and Conquer (independent subproblems) with Dynamic Programming (overlapping subproblems).",
                "Apply the Master Theorem: T(N) = a * T(N/b) + O(N^d).",
                "Explore celebrated applications: MergeSort, QuickSort, Binary Search, Strassen's Matrix Multiplication."
            ),
            sections = listOf(
                LessonSection(
                    id = "dc_phases",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Three Phases",
                    body = "Divide and Conquer solves a problem using three structured phases:\n\n1. Divide: Break the original problem into two or more smaller subproblems of the same type.\n2. Conquer: Recursively solve the subproblems. If small enough (base case), solve directly.\n3. Combine: Merge the subproblem solutions into a solution for the original problem.",
                    bulletPoints = listOf(
                        "Independence: Subproblems in Divide and Conquer are independent and do not overlap (unlike DP)."
                    )
                ),
                LessonSection(
                    id = "master_theorem",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Master Theorem",
                    body = "For recurrences of the form `T(N) = a * T(N / b) + O(N^d)`:\n(where a = number of subproblems, b = subproblem reduction factor, d = combine work power):\n\n- Case 1: If d < log_b(a) => T(N) = O(N^(log_b(a)))\n- Case 2: If d == log_b(a) => T(N) = O(N^d * log N)  [e.g., Merge Sort: a=2, b=2, d=1 -> O(N log N)]\n- Case 3: If d > log_b(a) => T(N) = O(N^d)",
                    bulletPoints = listOf(
                        "Binary Search: a=1, b=2, d=0 -> Case 2 -> O(log N).",
                        "Merge Sort: a=2, b=2, d=1 -> Case 2 -> O(N log N)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Fast Power (x^n in O(log N)) via Divide and Conquer",
                    language = "KOTLIN",
                    code = """// Computes x^n in O(log N) instead of O(N) linear multiplications
fun fastPow(x: Double, n: Long): Double {
    if (n == 0L) return 1.0 // Base case
    if (n < 0) return 1.0 / fastPow(x, -n)
    
    // Divide: solve x^(n/2) once!
    val half = fastPow(x, n / 2)
    
    // Combine: if n is even, half * half; if odd, half * half * x
    return if (n % 2L == 0L) {
        half * half
    } else {
        half * half * x
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-4", "Base cases: x^0 = 1, handle negative exponents."),
                        CodeStepExplanation(2, "Line 7", "Divide: compute half exponent x^(n/2) once in O(log N)."),
                        CodeStepExplanation(3, "Lines 10-14", "Combine: square the half power, multiplying by x if odd.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Divide and Conquer splits problems into independent subproblems, solves recursively, and combines results.",
                "Recurrence relations are solved analytically using the Master Theorem.",
                "Subproblems do not overlap; if subproblems overlap, use Dynamic Programming instead.",
                "Powers MergeSort, QuickSort, Binary Search, and Fast Fourier Transform (FFT)."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_adv_dc_1",
                    question = "What is the primary difference between Divide and Conquer and Dynamic Programming?",
                    options = listOf(
                        "Divide and conquer solves non-overlapping independent subproblems, while DP solves overlapping subproblems by caching results",
                        "Divide and conquer is always iterative, while DP is always recursive",
                        "DP cannot be used on arrays",
                        "Divide and conquer only works on sorted numbers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Divide and conquer divides into distinct subproblems (e.g. left and right halves in MergeSort). Dynamic programming is applied when subproblems share overlapping identical states (e.g. Fibonacci or Knapsack)."
                ),
                LessonPracticeQuestion(
                    id = "pq_adv_dc_2",
                    question = "Using the Fast Power divide-and-conquer algorithm, how many multiplications are needed to compute 2^1024?",
                    options = listOf("Approximately 10 multiplications", "1,024 multiplications", "512 multiplications", "2,048 multiplications"),
                    correctOptionIndex = 0,
                    explanation = "Because the exponent is halved at each step, 2^1024 takes log2(1024) = 10 recursive multiplications, dropping runtime from O(N) to O(log N)."
                )
            )
        ),

        // 5. Hash Functions & Collision Resolution
        DsaLessonContent(
            lessonId = "adv_hashing_l1",
            subtopicId = "adv_hashing",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Hash Functions & Collision Resolution",
            subtitle = "Key-to-bucket mapping, uniform distribution, and Separate Chaining vs. Open Addressing",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(1) average, O(N) worst case",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Hash Function contract: Determinism, Fast Computation, and Uniform Distribution.",
                "Understand the Pigeonhole Principle and why hash collisions are mathematically inevitable.",
                "Compare Collision Resolution: Separate Chaining (Linked Lists / Red-Black Trees) vs. Open Addressing (Linear/Quadratic Probing).",
                "Learn the Load Factor (α = N / M) and dynamic Rehashing."
            ),
            sections = listOf(
                LessonSection(
                    id = "hash_function_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "How Hash Tables Achieve O(1) Lookups",
                    body = "A Hash Table maps arbitrary keys (strings, objects, UUIDs) to fixed array indices using a Hash Function:\n`index = hash(key) % table_capacity`\n\nProperties of an Effective Hash Function:\n1. Deterministic: Equal keys MUST always produce the same hash code.\n2. Uniform Distribution: Spreads keys evenly across all buckets to minimize collisions.\n3. Fast: Computes in O(1) time.",
                    bulletPoints = listOf(
                        "Pigeonhole Principle: If keys > buckets, collisions MUST occur.",
                        "Collision: When two distinct keys k1 != k2 produce hash(k1) % capacity == hash(k2) % capacity."
                    )
                ),
                LessonSection(
                    id = "collision_strategies",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Collision Resolution Strategies",
                    body = "1. Separate Chaining: Each bucket holds a linked list (or balanced BST in Java 8+) of colliding elements.\n- Insert: Append to bucket chain in O(1).\n- Search: Walk chain of length L in O(L) time.\n\n2. Open Addressing: All elements reside directly inside the array. If a collision occurs, probe for the next empty slot:\n- Linear Probing: Check (index + 1), (index + 2)...\n- Quadratic Probing: Check (index + 1^2), (index + 2^2)...\n\nLoad Factor (α = N / capacity): When α exceeds 0.75, the table doubles capacity and rehashes all keys to maintain O(1) average lookups.",
                    bulletPoints = listOf(
                        "Average Case: O(1) access, insert, delete.",
                        "Worst Case: O(N) if all keys hash to a single bucket."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Separate Chaining HashMap in Kotlin",
                    language = "KOTLIN",
                    code = """class SimpleHashMap<K, V>(private val capacity: Int = 16) {
    private data class Entry<K, V>(val key: K, var value: V)
    private val buckets = Array(capacity) { mutableListOf<Entry<K, V>>() }

    private fun getBucketIndex(key: K): Int {
        // Ensure non-negative index within capacity bounds
        return (key.hashCode() and 0x7fffffff) % capacity
    }

    // O(1) average put
    fun put(key: K, value: V) {
        val bucket = buckets[getBucketIndex(key)]
        for (entry in bucket) {
            if (entry.key == key) {
                entry.value = value // Key exists, update value
                return
            }
        }
        bucket.add(Entry(key, value)) // Collision chained!
    }

    // O(1) average get
    fun get(key: K): V? {
        val bucket = buckets[getBucketIndex(key)]
        for (entry in bucket) {
            if (entry.key == key) return entry.value
        }
        return null // Not found
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 5-8", "Hash function masks sign bit and applies modulo capacity to map key to bucket index."),
                        CodeStepExplanation(2, "Lines 11-19", "put checks bucket list for key collision; updates value if key exists or chains new entry."),
                        CodeStepExplanation(3, "Lines 22-28", "get locates bucket in O(1) and scans short bucket list to return value.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Hash tables map keys to array indices via hash(key) % capacity in O(1) average time.",
                "Collisions are inevitable and handled by Separate Chaining or Open Addressing.",
                "Java 8+ converts long separate chains (length >= 8) into Red-Black Trees to cap worst-case lookups at O(log N).",
                "Load factor threshold (typically 0.75) triggers rehashing into a doubled array."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_adv_hsh_1",
                    question = "What is the purpose of 'Rehashing' when a Hash Table's load factor exceeds 0.75?",
                    options = listOf(
                        "It doubles the table capacity and redistributes all keys to keep bucket chains short and maintain O(1) average lookups",
                        "It encrypts the keys for security",
                        "It converts the hash table into a binary search tree",
                        "It deletes duplicate keys"
                    ),
                    correctOptionIndex = 0,
                    explanation = "As load factor grows, collision chains lengthen, degrading lookups towards O(N). Doubling capacity and rehashing spreads keys across more buckets, restoring O(1) performance."
                ),
                LessonPracticeQuestion(
                    id = "pq_adv_hsh_2",
                    question = "Why does Java 8's HashMap convert a bucket's linked list into a Red-Black Tree when its chain length reaches 8?",
                    options = listOf(
                        "To guarantee that adversarial hash collision attacks have worst-case lookup time bounded to O(log N) instead of O(N)",
                        "Because trees use less memory than linked lists",
                        "Because linked lists cannot store more than 8 elements in JVM",
                        "To sort the keys alphabetically"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If malicious users craft thousands of keys that hash to the same bucket, a linked list degrades to O(N) linear time (Denial of Service). Converting to a Red-Black Tree bounds lookups to O(log N)."
                )
            )
        ),

        // 6. Heap Operations & Priority Queue Deep Dive
        DsaLessonContent(
            lessonId = "adv_heap_pq_l1",
            subtopicId = "adv_heap_pq",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Heap Operations & Priority Queue Deep Dive",
            subtitle = "Sift-Up, Sift-Down, Top-K elements, and continuous median streaming",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(log N) insert/extract, O(1) peek",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Master Sift-Up (bubbling up on insert) and Sift-Down (sinking down on extract-min).",
                "Solve the Top-K Frequent Elements problem in O(N log K) time using a Min-Heap.",
                "Implement the Continuous Running Median algorithm using Two Heaps (Max-Heap + Min-Heap).",
                "Understand array layout formulas: parent(i) = (i-1)/2, left(i) = 2i+1, right(i) = 2i+2."
            ),
            sections = listOf(
                LessonSection(
                    id = "heap_mechanics",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Array Representation of Binary Heaps",
                    body = "A Binary Heap is a Complete Binary Tree stored entirely in a contiguous array without explicit pointer nodes:\n\n- Root: index 0\n- Left Child of node i: `2 * i + 1`\n- Right Child of node i: `2 * i + 2`\n- Parent of node i: `(i - 1) / 2`\n\nMin-Heap Invariant: Every parent node's value is <= its children's values.\nMax-Heap Invariant: Every parent node's value is >= its children's values.",
                    bulletPoints = listOf(
                        "Insert: Append at end of array, then Sift-Up until heap property is restored in O(log N).",
                        "Extract Root: Replace root with last element, then Sift-Down in O(log N).",
                        "Peek: Inspect index 0 in strictly O(1) time."
                    )
                ),
                LessonSection(
                    id = "two_heaps_median",
                    type = LessonSectionType.USE_CASES,
                    title = "The Two-Heaps Running Median Algorithm",
                    body = "Problem: Find the median of a continuous stream of incoming numbers in real-time.\n\nMaintain Two Heaps:\n1. A Max-Heap storing the SMALLER half of numbers.\n2. A Min-Heap storing the LARGER half of numbers.\n\nKeep heaps balanced in size (|size1 - size2| <= 1):\n- If total numbers is odd: Median is the root of the larger heap!\n- If total numbers is even: Median is the average of both roots!\nOperations: Insert in O(log N), Median query in instant O(1)!",
                    bulletPoints = listOf(
                        "Insert: O(log N)",
                        "Find Median: O(1) instant lookup."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Top-K Frequent Elements in Kotlin (Min-Heap Pattern)",
                    language = "KOTLIN",
                    code = """import java.util.PriorityQueue

fun topKFrequent(nums: IntArray, k: Int): IntArray {
    // 1. Count frequencies in O(N)
    val freqMap = mutableMapOf<Int, Int>()
    for (n in nums) freqMap[n] = (freqMap[n] ?: 0) + 1
    
    // 2. Min-Heap bounded to size K based on frequency
    val minHeap = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
    
    for ((num, count) in freqMap) {
        minHeap.offer(Pair(num, count))
        // If heap exceeds K elements, eject the lowest frequency element!
        if (minHeap.size > k) {
            minHeap.poll()
        }
    }
    
    // 3. Remaining K elements in heap are the top K frequent
    return minHeap.map { it.first }.toIntArray()
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 4-6", "Build frequency map in O(N) time."),
                        CodeStepExplanation(2, "Lines 9-17", "Maintain Min-Heap bounded to size K; polling ejects lowest frequency, leaving top K in O(N log K)."),
                        CodeStepExplanation(3, "Line 20", "Extract surviving K most frequent elements.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Binary heaps store complete binary trees inside flat arrays without pointer overhead.",
                "Insert uses Sift-Up in O(log N); Extract uses Sift-Down in O(log N); Peek is O(1).",
                "Top-K problems are solved in O(N log K) time by capping a Min-Heap at size K.",
                "The Two-Heaps pattern (Min-Heap + Max-Heap) tracks running stream medians with O(1) lookup."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_adv_hp_1",
                    question = "Why does maintaining a Min-Heap of size K solve the Top-K Largest Elements problem in O(N log K) instead of O(N log N)?",
                    options = listOf(
                        "The heap never grows beyond size K, so each heap operation costs O(log K) rather than sorting all N elements in O(N log N)",
                        "Because min-heaps only accept positive integers",
                        "Because K is always equal to 1",
                        "Because min-heaps run on GPU hardware"
                    ),
                    correctOptionIndex = 0,
                    explanation = "By immediately evicting the smallest element when heap size exceeds K, the heap is capped at size K. Processing N elements takes N * log(K) = O(N log K), which is dramatically faster when K << N."
                ),
                LessonPracticeQuestion(
                    id = "pq_adv_hp_2",
                    question = "In the Continuous Running Median algorithm, which two data structures are paired together?",
                    options = listOf(
                        "A Max-Heap for the lower half of numbers and a Min-Heap for the upper half of numbers",
                        "Two stacks",
                        "A circular queue and a singly linked list",
                        "Two hash tables"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A Max-Heap gives instant O(1) access to the largest of the lower half, and a Min-Heap gives instant O(1) access to the smallest of the upper half, allowing immediate median calculation in O(1)."
                )
            )
        )
    )
}
