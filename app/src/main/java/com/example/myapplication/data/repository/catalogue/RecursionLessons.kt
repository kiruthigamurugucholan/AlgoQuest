package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object RecursionLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Introduction to Recursion & Call Stack
        DsaLessonContent(
            lessonId = "rec_intro_l1",
            subtopicId = "rec_intro",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Introduction to Recursion & Call Stack",
            subtitle = "Self-referential function execution, base cases, and call stack frame allocation",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(N) call stack",
            learningObjectives = listOf(
                "Understand the two mandatory components of recursion: Base Case and Recursive Step.",
                "Trace how the CPU Call Stack manages function activation frames.",
                "Recognize how missing or unreachable base cases cause java.lang.StackOverflowError.",
                "Relate recursion directly to mathematical induction."
            ),
            sections = listOf(
                LessonSection(
                    id = "rec_intro_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Recursion?",
                    body = "Recursion is a computational problem-solving technique where a function solves a problem by calling itself with a smaller, simpler sub-instance of the same problem.\n\nA valid recursive function must always have two components:\n1. Base Case: The simplest stopping condition that returns an answer directly without making any further recursive calls.\n2. Recursive Step: The rule that reduces the problem towards the base case and invokes the function again.",
                    bulletPoints = listOf(
                        "Progress Towards Base Case: Every call must strictly reduce input magnitude (e.g., n - 1 or n / 2).",
                        "Stack Memory: Each pending recursive call consumes an activation frame on the CPU Call Stack."
                    )
                ),
                LessonSection(
                    id = "call_stack_frames",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "How the CPU Executes Recursive Calls",
                    body = "When function `countdown(3)` is executed:\n1. Frame for `countdown(3)` is pushed onto the Call Stack. It calls `countdown(2)`.\n2. Frame for `countdown(2)` is pushed above it. It calls `countdown(1)`.\n3. Frame for `countdown(1)` is pushed above it. It reaches base case 0.\n4. Base case returns; frames pop off the Call Stack in reverse LIFO order back down to `countdown(3)`.\n\nIf the base case is missing, frames accumulate indefinitely until RAM is exhausted, throwing `StackOverflowError`.",
                    bulletPoints = listOf(
                        "Space Complexity: Proportional to maximum recursion depth."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Basic Recursive Countdown in Kotlin",
                    language = "KOTLIN",
                    code = """fun countdown(n: Int) {
    // 1. Base Case: stop when n reaches 0
    if (n <= 0) {
        println("Blastoff!")
        return
    }
    
    // Work done before recursive call (winding)
    print("${'$'}n ")
    
    // 2. Recursive Step: call countdown with n - 1
    countdown(n - 1)
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 3-6", "Base case halts recursion when n <= 0, preventing infinite loops."),
                        CodeStepExplanation(2, "Line 9", "Print current n before pushing the next frame onto the stack."),
                        CodeStepExplanation(3, "Line 12", "Recursive step advances closer to base case by decrementing n.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Recursion requires a base case to terminate and a recursive step moving towards that base case.",
                "Every recursive call pushes an activation record onto the CPU Call Stack.",
                "Maximum call stack depth determines the algorithm's auxiliary space complexity.",
                "Missing base cases or un-reduced parameters cause StackOverflowError."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_rec_int_1",
                    question = "What causes a 'StackOverflowError' in a recursive function?",
                    options = listOf(
                        "A missing or unreachable base case causes infinite recursive calls until call stack memory is exhausted",
                        "The CPU runs out of disk storage",
                        "An array index is negative",
                        "The function returns a floating point number"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Every recursive call consumes a call stack frame. Without a valid base case to halt recursion, calls continue endlessly until the JVM stack memory boundary is breached."
                ),
                LessonPracticeQuestion(
                    id = "pq_rec_int_2",
                    question = "What are the two mandatory components that every valid recursive function must possess?",
                    options = listOf(
                        "At least one Base Case and a Recursive Step that moves toward the Base Case",
                        "A for-loop and a while-loop",
                        "A try-catch block and a synchronized lock",
                        "An array and a hash table"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A recursive function must have a terminating base case to stop recursion, and a recursive step that shrinks the problem size toward that base case."
                )
            )
        ),

        // 2. Factorial & Base Case Principle
        DsaLessonContent(
            lessonId = "rec_factorial_l1",
            subtopicId = "rec_factorial",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Factorial & Base Case Principle",
            subtitle = "Mathematical recurrence relations and Tail Call Optimization (TCO)",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(N) standard, O(1) tail-recursive",
            learningObjectives = listOf(
                "Model the Factorial recurrence relation: n! = n * (n - 1)! with base case 0! = 1.",
                "Trace call stack winding (accumulation) and unwinding (multiplication return).",
                "Understand Tail Recursion and how Kotlin's 'tailrec' keyword optimizes stack space to O(1).",
                "Handle numerical overflow with large integers."
            ),
            sections = listOf(
                LessonSection(
                    id = "factorial_math",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Mathematical Recurrence",
                    body = "The factorial of a non-negative integer n (written n!) is the product of all positive integers less than or equal to n:\n- Recurrence: `n! = n * (n - 1)!` for n > 0\n- Base Case: `0! = 1` and `1! = 1`\n\nNotice that calculating 5! requires 4!, which requires 3!, which requires 2!, which requires 1!. The problem naturally mirrors recursive stack execution.",
                    bulletPoints = listOf(
                        "Time: O(N) multiplications.",
                        "Space: O(N) call stack frames in naive recursion."
                    )
                ),
                LessonSection(
                    id = "tail_call_optimization",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Tail Recursion & Kotlin 'tailrec'",
                    body = "In naive recursion, `return n * factorial(n - 1)` cannot clear its stack frame because it must wait for `factorial(n - 1)` to return before it can multiply by n.\n\nIn Tail Recursion, the recursive call is the ABSOLUTE LAST operation in the function. We pass an accumulator variable holding the running product:\n`factorialTail(n - 1, n * acc)`\n\nKotlin's `tailrec` keyword compiles this directly into a high-performance while-loop with ZERO call stack overhead (O(1) space)!",
                    bulletPoints = listOf(
                        "Naive Space: O(N) stack frames.",
                        "tailrec Space: O(1) constant stack space."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Naive vs. Tail-Recursive Factorial in Kotlin",
                    language = "KOTLIN",
                    code = """// 1. Naive Recursion: O(N) time, O(N) stack space
fun factorial(n: Long): Long {
    if (n <= 1) return 1 // Base Case
    return n * factorial(n - 1) // Must wait for return to multiply!
}

// 2. Tail Recursion with 'tailrec': Compiled to O(1) space while-loop!
tailrec fun factorialTail(n: Long, accumulator: Long = 1): Long {
    if (n <= 1) return accumulator // Base Case
    // Tail call: recursive call is the final action, passing running product
    return factorialTail(n - 1, n * accumulator)
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-5", "Naive recursion leaves unresolved multiplication on call stack for every level."),
                        CodeStepExplanation(2, "Lines 8-12", "tailrec version carries running total in accumulator, allowing compiler to reuse a single stack frame.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Factorial is defined by recurrence n! = n * (n - 1)! with base case 0! = 1.",
                "Naive recursion consumes O(N) call stack frames.",
                "Tail recursion places the recursive invocation as the final action with an accumulator parameter.",
                "Kotlin's 'tailrec' keyword eliminates stack frames, optimizing recursion into O(1) space loops."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_rec_fac_1",
                    question = "Why can Kotlin's compiler optimize a function marked with 'tailrec' into an O(1) space loop?",
                    options = listOf(
                        "Because the recursive call is the final expression, meaning no local variables or calculations need to be remembered in a stack frame",
                        "Because tailrec functions run on a background thread",
                        "Because factorials can only be computed with loops",
                        "Because it forces variables to be stored in cache"
                    ),
                    correctOptionIndex = 0,
                    explanation = "When the recursive call is the absolute final action (tail call), the current stack frame has no remaining work to perform after the call returns, allowing the compiler to overwrite the existing frame repeatedly like a loop."
                ),
                LessonPracticeQuestion(
                    id = "pq_rec_fac_2",
                    question = "What is the base case for the factorial function?",
                    options = listOf("n <= 1 returns 1", "n == 0 returns 0", "n == 2 returns 4", "n < 0 returns -1"),
                    correctOptionIndex = 0,
                    explanation = "By mathematical convention and definition, 0! = 1 and 1! = 1; when n reaches 1 or 0, the function halts recursion and returns 1."
                )
            )
        ),

        // 3. Fibonacci & Recursion Trees
        DsaLessonContent(
            lessonId = "rec_fibonacci_l1",
            subtopicId = "rec_fibonacci",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Fibonacci & Recursion Trees",
            subtitle = "Overlapping subproblems, exponential tree branching, and memoization",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(2^N) naive, O(N) memoized",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Fibonacci recurrence: F(n) = F(n-1) + F(n-2) with base cases F(0)=0, F(1)=1.",
                "Visualize and analyze the binary recursion tree of height N.",
                "Prove why naive recursive Fibonacci has exponential O(2^N) time complexity.",
                "Recognize Overlapping Subproblems and how memoization collapses runtime to O(N)."
            ),
            sections = listOf(
                LessonSection(
                    id = "fib_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Fibonacci Sequence",
                    body = "The Fibonacci sequence begins: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34...\nEach term is the sum of the preceding two terms:\n- Recurrence: `F(n) = F(n - 1) + F(n - 2)`\n- Base Cases: `F(0) = 0`, `F(1) = 1`\n\nWhile mathematically simple, a naive recursive implementation is notoriously inefficient.",
                    bulletPoints = listOf(
                        "Branching Factor: Each call spawns 2 additional recursive calls.",
                        "Tree Height: N levels."
                    )
                ),
                LessonSection(
                    id = "fib_recursion_tree",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "The Exponential Explosion of Overlapping Subproblems",
                    body = "Drawing the recursion tree for `fib(5)` reveals massive redundant computation:\n- `fib(3)` is computed independently 2 separate times.\n- `fib(2)` is computed independently 3 separate times.\n- `fib(1)` is computed independently 5 separate times!\n\nAs N grows, total calls equal roughly 2^N (specifically O(1.618^N), the Golden Ratio). Calculating `fib(50)` naively requires over 1 quadrillion operations and freezes your device!\n\nMemoization (storing computed results in an array or map) computes each state once, reducing runtime to O(N) linear time.",
                    bulletPoints = listOf(
                        "Naive Time: O(2^N) exponential.",
                        "Memoized Time: O(N) linear.",
                        "Auxiliary Space: O(N) recursion stack."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Naive Exponential vs. Memoized Linear Fibonacci",
                    language = "KOTLIN",
                    code = """// 1. Naive Recursion: O(2^N) exponential time! (DO NOT RUN FOR N > 40)
fun fibNaive(n: Int): Long {
    if (n <= 0) return 0
    if (n == 1) return 1
    return fibNaive(n - 1) + fibNaive(n - 2) // Dual branching
}

// 2. Memoized Recursion (Top-Down DP): O(N) linear time!
fun fibMemoized(n: Int, memo: LongArray = LongArray(n + 1) { -1 }): Long {
    if (n <= 0) return 0
    if (n == 1) return 1
    if (memo[n] != -1L) return memo[n] // Return cached answer in O(1)!
    
    memo[n] = fibMemoized(n - 1, memo) + fibMemoized(n - 2, memo)
    return memo[n]
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-6", "fibNaive branches into 2 calls per step, forming an exponential binary tree."),
                        CodeStepExplanation(2, "Lines 9-16", "fibMemoized caches every sub-problem result in memo array, guaranteeing each state is solved exactly once.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Naive recursive Fibonacci has exponential O(2^N) time complexity due to redundant tree branching.",
                "Overlapping subproblems mean identical states (like fib(3)) are recomputed repeatedly.",
                "Memoization caches sub-problem solutions, collapsing exponential O(2^N) time into linear O(N) time.",
                "Serves as the textbook introduction to Dynamic Programming."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_rec_fib_1",
                    question = "Why does naive recursive Fibonacci run in exponential O(2^N) time?",
                    options = listOf(
                        "Each function call branches into two recursive calls, creating a binary tree of calls of height N with redundant computations",
                        "Because Fibonacci numbers grow larger than 64-bit integers",
                        "Because calculating additions takes exponential time in ALUs",
                        "Because the base cases return negative numbers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Every non-base call spawns 2 child calls: fib(n-1) and fib(n-2). This creates a binary recursion tree of height N containing approximately 2^N total nodes, most of which solve identical subproblems."
                ),
                LessonPracticeQuestion(
                    id = "pq_rec_fib_2",
                    question = "What is the primary benefit of adding Memoization to a recursive algorithm with overlapping subproblems?",
                    options = listOf(
                        "It stores previously computed answers so each sub-problem is solved only once in O(1) retrieval time",
                        "It eliminates the need for base cases",
                        "It reverses the Fibonacci sequence",
                        "It reduces space complexity to O(0)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Memoization caches the return value for each input state. If the state is encountered again, it is returned in O(1) from the lookup table instead of re-evaluating the entire recursive subtree."
                )
            )
        ),

        // 4. Recursive Binary Search
        DsaLessonContent(
            lessonId = "rec_binary_search_l1",
            subtopicId = "rec_binary_search",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Recursive Binary Search",
            subtitle = "Divide-and-conquer recurrence relations and halving search space",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(log N)",
            spaceComplexity = "O(log N) call stack",
            learningObjectives = listOf(
                "Formulate the divide-and-conquer recurrence relation: T(N) = T(N/2) + O(1).",
                "Identify termination base cases: target found (arr[mid] == target) and search space exhausted (low > high).",
                "Compare recursive binary search against iterative binary search.",
                "Analyze call stack memory overhead: log2(N) activation frames."
            ),
            sections = listOf(
                LessonSection(
                    id = "rec_bs_recurrence",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Recurrence Relation",
                    body = "Recursive Binary Search expresses the search process as a mathematical recurrence:\n- Recurrence: `T(N) = T(N / 2) + O(1)`\n  (Halve the search space, plus 1 comparison).\n- By Case 2 of the Master Theorem: `T(N) = O(log N)`.\n\nAt each step, we compute `mid`. If target != arr[mid], we make exactly ONE recursive call on either the left half or right half.",
                    bulletPoints = listOf(
                        "Single Branch: Unlike Fibonacci, binary search only branches into ONE subproblem per step.",
                        "Recursion Depth: At most log2(N) levels."
                    )
                ),
                LessonSection(
                    id = "rec_vs_iter_bs",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "Recursive vs. Iterative Trade-Offs",
                    body = "While both implementations share identical O(log N) time complexity:\n\n- Iterative Binary Search: Uses a while loop -> O(1) auxiliary space.\n- Recursive Binary Search: Uses function calls -> O(log N) stack frames on the Call Stack.\n\nIn memory-constrained production systems, the iterative approach is preferred because it avoids stack frame overhead entirely.",
                    bulletPoints = listOf(
                        "Time: O(log N) both.",
                        "Space: Iterative = O(1), Recursive = O(log N)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Recursive Binary Search in Kotlin",
                    language = "KOTLIN",
                    code = """fun binarySearchRecursive(
    arr: IntArray,
    target: Int,
    low: Int = 0,
    high: Int = arr.size - 1
): Int {
    // Base Case 1: Search space exhausted (not found)
    if (low > high) return -1
    
    val mid = low + (high - low) / 2
    
    return when {
        // Base Case 2: Target found!
        arr[mid] == target -> mid
        // Search left half
        arr[mid] > target -> binarySearchRecursive(arr, target, low, mid - 1)
        // Search right half
        else -> binarySearchRecursive(arr, target, mid + 1, high)
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 8", "Base Case 1 halts recursion when boundaries cross (low > high), indicating target absence."),
                        CodeStepExplanation(2, "Lines 14", "Base Case 2 returns index immediately when target is matched at mid."),
                        CodeStepExplanation(3, "Lines 16-18", "Single tail-recursive call into the valid half.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Recursive binary search adheres to recurrence T(N) = T(N/2) + O(1) = O(log N) time.",
                "Base cases: target found at mid, or boundaries crossed (low > high).",
                "Consumes O(log N) stack space for recursion frames, whereas iterative search uses O(1) space.",
                "Exemplifies single-branch divide-and-conquer."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_rec_bs_1",
                    question = "What is the maximum recursion depth for Recursive Binary Search on an array of 1,000,000 elements?",
                    options = listOf("Approximately 20 stack frames", "1,000,000 stack frames", "500,000 stack frames", "2 stack frames"),
                    correctOptionIndex = 0,
                    explanation = "Because the array is cut in half at each level, the maximum call stack depth is log2(1,000,000) ≈ 20 activation frames."
                ),
                LessonPracticeQuestion(
                    id = "pq_rec_bs_2",
                    question = "What is the primary architectural difference between iterative and recursive binary search?",
                    options = listOf(
                        "Iterative uses O(1) space, while recursive uses O(log N) stack frames on the Call Stack",
                        "Iterative is O(N) while recursive is O(log N)",
                        "Recursive binary search can search unsorted arrays",
                        "Iterative binary search requires double the comparisons"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Both have identical O(log N) time complexity, but recursive binary search allocates an execution stack frame for each halving step (O(log N) space), whereas a while loop uses O(1) auxiliary space."
                )
            )
        ),

        // 5. Tower of Hanoi Problem
        DsaLessonContent(
            lessonId = "rec_tower_of_hanoi_l1",
            subtopicId = "rec_tower_of_hanoi",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Tower of Hanoi Problem",
            subtitle = "Classic mathematical puzzle demonstrating exponential divide-and-conquer decomposition",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(2^N)",
            spaceComplexity = "O(N) call stack",
            learningObjectives = listOf(
                "Understand the Tower of Hanoi rules and invariants: only 1 disc moved at a time, larger discs never on smaller discs.",
                "Decompose problem into 3 steps: Move N-1 to auxiliary, move largest disc, move N-1 to destination.",
                "Derive the exact move count recurrence: T(N) = 2T(N-1) + 1 = 2^N - 1.",
                "Appreciate how pure recursive abstractions solve complex combinatorial puzzles."
            ),
            sections = listOf(
                LessonSection(
                    id = "hanoi_rules",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Legend & The Rules",
                    body = "According to legend, monks in a temple are moving 64 golden discs across 3 diamond pegs according to sacred rules:\n1. Only one disc can be moved at a time.\n2. Each move takes the upper disc from one stack and places it on top of another peg.\n3. No larger disc may ever be placed on top of a smaller disc.\n\nWhen all 64 discs are transferred to the destination peg, the puzzle is complete.",
                    bulletPoints = listOf(
                        "Pegs: Source, Auxiliary (helper), Destination.",
                        "Base Case: N = 1 (simply move disc directly from Source to Destination)."
                    )
                ),
                LessonSection(
                    id = "hanoi_decomposition",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The 3-Step Recursive Decomposition",
                    body = "To transfer N discs from Source to Destination using Auxiliary:\n\n1. Move top N - 1 discs from Source to Auxiliary (using Destination as temporary helper).\n2. Move the remaining single largest disc (disc N) directly from Source to Destination.\n3. Move the N - 1 discs from Auxiliary to Destination (using Source as temporary helper).\n\nNotice that steps 1 and 3 are the exact same Tower of Hanoi problem with N - 1 discs! Recursion solves this puzzle in just 4 lines of code.",
                    bulletPoints = listOf(
                        "Recurrence: T(N) = 2T(N - 1) + 1",
                        "Exact Moves: 2^N - 1 moves.",
                        "For N = 64 discs: 2^64 - 1 = 18.4 quintillion moves (over 584 billion years)!"
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Tower of Hanoi Solution in Kotlin",
                    language = "KOTLIN",
                    code = """fun solveHanoi(
    n: Int,
    source: Char = 'A',
    auxiliary: Char = 'B',
    destination: Char = 'C'
) {
    // Base Case: 1 disc moved directly
    if (n == 1) {
        println("Move disc 1 from ${'$'}source to ${'$'}destination")
        return
    }
    
    // Step 1: Move n - 1 discs from Source to Auxiliary
    solveHanoi(n - 1, source, destination, auxiliary)
    
    // Step 2: Move largest disc n from Source to Destination
    println("Move disc ${'$'}n from ${'$'}source to ${'$'}destination")
    
    // Step 3: Move n - 1 discs from Auxiliary to Destination
    solveHanoi(n - 1, auxiliary, source, destination)
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-11", "Base Case: with 1 disc, move directly from source to destination without auxiliary."),
                        CodeStepExplanation(2, "Line 14", "Recursively transfer n - 1 smaller discs out of the way onto auxiliary peg."),
                        CodeStepExplanation(3, "Line 17", "Transfer single largest bottom disc directly onto destination peg."),
                        CodeStepExplanation(4, "Line 20", "Recursively transfer n - 1 discs from auxiliary onto destination peg.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Tower of Hanoi reduces moving N discs into two sub-problems of moving N-1 discs plus 1 move.",
                "Recurrence relation T(N) = 2T(N-1) + 1 yields exactly 2^N - 1 total moves.",
                "Time complexity is strictly exponential O(2^N); auxiliary space is O(N) call stack depth.",
                "Showcases the supreme power of recursive abstraction for combinatorial problems."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_rec_han_1",
                    question = "How many total disc moves are mathematically required to solve the Tower of Hanoi for N = 4 discs?",
                    options = listOf("15 moves (2^4 - 1)", "16 moves", "8 moves", "24 moves"),
                    correctOptionIndex = 0,
                    explanation = "The formula for Tower of Hanoi moves is 2^N - 1. For N = 4: 2^4 - 1 = 16 - 1 = 15 moves."
                ),
                LessonPracticeQuestion(
                    id = "pq_rec_han_2",
                    question = "What is the maximum call stack depth (auxiliary space complexity) for Tower of Hanoi with N discs?",
                    options = listOf("O(N) stack frames", "O(2^N) stack frames", "O(1) stack frames", "O(log N) stack frames"),
                    correctOptionIndex = 0,
                    explanation = "Even though the algorithm executes 2^N - 1 steps over time, the maximum simultaneous depth of the call stack at any given moment is equal to N levels."
                )
            )
        )
    )
}
