package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object ProgrammingBasicsLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Introduction to Data Structures
        DsaLessonContent(
            lessonId = "prog_intro_ds_l1",
            subtopicId = "prog_intro_ds",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Introduction to Data Structures",
            subtitle = "How computers organize data in memory and why structure determines speed",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) to O(N)",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the formal definition of data structures and Abstract Data Types (ADTs).",
                "Distinguish between contiguous (array-based) and linked (pointer-based) memory layouts.",
                "Learn why selecting the proper data structure directly determines application speed and scalability.",
                "Identify common real-world systems modeled by linear and non-linear data structures."
            ),
            sections = listOf(
                LessonSection(
                    id = "intro_what_is_ds",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Data Structure?",
                    body = "A data structure is a specialized scheme for organizing, storing, and manipulating data within memory so operations like retrieval, insertion, and mutation can execute efficiently.\n\nWhile primitive types store isolated values, real-world software must process collections of millions of records—such as user feeds, geospatial coordinates, and financial transactions. A data structure establishes strict rules (invariants) governing element arrangement and manipulation.",
                    bulletPoints = listOf(
                        "Raw Data: Isolated values like an integer (100), a string (\"Aarav\"), or a timestamp.",
                        "Data Structure: A physical or logical arrangement (Array, Stack, Hash Table) maintaining definite invariants.",
                        "Abstract Data Type (ADT): High-level contract specifying available operations without committing to memory representation."
                    ),
                    highlightNote = "Core Invariant: Algorithms depend entirely upon the data structure's layout guarantees to achieve optimal asymptotic runtime."
                ),
                LessonSection(
                    id = "why_ds_needed",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Why Do We Need Data Structures?",
                    body = "Consider searching for one student's transcript out of 100,000 unorganized papers strewn across a floor. You must perform an exhaustive scan. Stored alphabetically in labeled file cabinets, the exact sheet is located in seconds.\n\nEven with gigahertz processors, suboptimal data arrangement causes memory bottlenecks, latency spikes, and server crashes under heavy load.",
                    bulletPoints = listOf(
                        "Rapid Retrieval: Find critical entries in logarithmic or constant time rather than linear scans.",
                        "Atomic & Efficient Updates: Insert or delete records without shifting entire memory segments unnecessarily.",
                        "Resource Minimization: Conserve RAM footprint and maximize hardware cache line utilization.",
                        "Scalability: Ensure seamless latency whether processing 1,000 requests or 100 million requests."
                    ),
                    highlightNote = "Every data structure makes deliberate trade-offs across four primitives: Access, Search, Insert, and Delete."
                ),
                LessonSection(
                    id = "memory_organization",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Contiguous vs. Linked Memory Storage",
                    body = "Computer RAM is a linear byte-addressable memory space. Data structures map to physical hardware using two foundational techniques:\n\n1. Contiguous Storage: Elements are located in adjacent memory cells. The CPU computes the address instantly via offset formula: Base + (Index * ElementSize).\n2. Linked / Pointer Storage: Elements (nodes) reside at arbitrary heap addresses, each carrying reference pointers to adjacent nodes.",
                    bulletPoints = listOf(
                        "Contiguous (Arrays): Instant O(1) index access and superior CPU L1/L2 cache locality, but rigid resizing.",
                        "Linked (Linked Lists, Trees): Flexible dynamic node allocation without memory shifting, but O(N) traversal overhead.",
                        "Linear Types: Sequential ordering where each item has clear predecessors and successors.",
                        "Non-Linear Types: Branching hierarchical (Trees) or interconnected (Graphs) topologies."
                    ),
                    highlightNote = "Contiguous memory favors random indexing; linked memory favors frequent structural rearrangements."
                ),
                LessonSection(
                    id = "real_world_examples",
                    type = LessonSectionType.REAL_WORLD_EXAMPLES,
                    title = "Real-World Analogies",
                    body = "Physical systems illustrate foundational data structure behaviors:\n\n- Numbered Bookshelf (Array): Instant lookup by shelf number; inserting into the middle requires sliding all subsequent books.\n- Scavenger Hunt Clues (Linked List): Each clue gives directions to the next spot. Inserting a new clue requires changing only one clue reference.\n- Cafeteria Tray Stack (Stack): Only the top tray is pushed or popped (Last-In, First-Out).\n- Ticket Counter Queue (Queue): Customers served in strict arrival sequence (First-In, First-Out).",
                    bulletPoints = listOf(
                        "Arrays: Digital audio buffers, image pixel bitmaps, lookup matrices.",
                        "Stacks: Browser back button history, syntax matching in code editors, undo/redo buffers.",
                        "Queues: Print job spoolers, backend background task queues, OS CPU task schedulers.",
                        "Trees & Graphs: OS directory structures, social networks, Google Maps GPS routing."
                    )
                ),
                LessonSection(
                    id = "performance_impact",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "Performance Implications of Structure Selection",
                    body = "Evaluating 1,000,000 records demonstrates the massive difference structure makes:\n\n- Unsorted Array: Scan from start to finish -> up to 1,000,000 comparisons (O(N)).\n- Sorted Array with Binary Search: Halve remaining space -> maximum 20 comparisons (O(log N)).\n- Hash Map: Direct key hash lookup -> approximately 1 operation (O(1)).",
                    bulletPoints = listOf(
                        "O(1) Constant Time: Operations independent of collection magnitude.",
                        "O(log N) Logarithmic: Operations scaling by dividing input size each step.",
                        "O(N) Linear: Direct proportional scaling with total element count."
                    ),
                    highlightNote = "No single data structure is universally optimal. Select structures tailored to your application's predominant access patterns."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Linear Scan vs. HashMap Direct Lookup",
                    language = "KOTLIN",
                    code = """data class Record(val id: Int, val name: String)

val recordList = listOf(
    Record(101, "Alice"),
    Record(102, "Bob"),
    Record(103, "Charlie")
)

// Linear search: scans sequentially O(N)
fun findInList(list: List<Record>, targetId: Int): Record? {
    for (item in list) {
        if (item.id == targetId) return item
    }
    return null
}

// HashMap index: hash calculation O(1) average
val recordMap = recordList.associateBy { it.id }
val found = recordMap[103] // Charlie retrieved directly""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 1-7", "Define data class Record and initialize list of items."),
                        CodeStepExplanation(2, "Lines 10-15", "Iterate through items linearly; in worst case checks every item N times."),
                        CodeStepExplanation(3, "Lines 18-19", "HashMap indexes each record key into buckets, allowing constant time retrieval.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "A data structure dictates physical/logical arrangement in memory and establishes operation complexity.",
                "Contiguous structures enable O(1) index access; linked structures support agile pointer mutations.",
                "Linear structures preserve sequential sequence; hierarchical and network structures represent relationships.",
                "Selecting the right data structure reduces computational complexity by orders of magnitude."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_prog_intro_ds_1",
                    question = "Why does an array provide O(1) constant-time access to any valid index?",
                    options = listOf(
                        "Because elements are stored contiguously in memory and computed via Base + (Index * Size)",
                        "Because arrays sort their items automatically in RAM",
                        "Because each array element holds a pointer to all other elements",
                        "Because the operating system keeps arrays in CPU registers only"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Arrays occupy contiguous memory addresses. The memory address of element i is calculated directly using baseAddress + (i * elementSize) in a single CPU cycle."
                ),
                LessonPracticeQuestion(
                    id = "pq_prog_intro_ds_2",
                    question = "Which data structure follows the Last-In, First-Out (LIFO) access invariant?",
                    options = listOf("Queue", "Stack", "Binary Search Tree", "Linked List"),
                    correctOptionIndex = 1,
                    explanation = "A Stack enforces LIFO semantics: the most recently added element is the first one removed."
                )
            )
        ),

        // 2. Introduction to Algorithms
        DsaLessonContent(
            lessonId = "prog_intro_algo_l1",
            subtopicId = "prog_intro_algo",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Introduction to Algorithms",
            subtitle = "Unambiguous step-by-step problem solving with guaranteed termination and correctness",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) to O(N!)",
            spaceComplexity = "O(1) to O(N)",
            learningObjectives = listOf(
                "Understand the five essential properties of a valid computer algorithm.",
                "Differentiate between an algorithm, pseudocode, and an implementation.",
                "Analyze algorithm correctness using loop invariants and termination proofs.",
                "Learn why algorithmic efficiency matters independently of hardware speed."
            ),
            sections = listOf(
                LessonSection(
                    id = "algo_definition",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is an Algorithm?",
                    body = "An algorithm is a finite, well-defined sequence of unambiguous computer-implementable instructions designed to solve a specific problem or compute a result from given inputs.\n\nUnlike an arbitrary code snippet, a formal algorithm must guarantee correctness for all valid input domains and must terminate in finite time.",
                    bulletPoints = listOf(
                        "Input: Zero or more externally supplied quantities with well-defined constraints.",
                        "Output: At least one result having a specific relationship to the inputs.",
                        "Definiteness: Each instruction must be clear, unambiguous, and mathematically precise.",
                        "Finiteness: The procedure must terminate after a finite number of computational steps.",
                        "Effectiveness: Every step must be basic enough to be executed in principle by a machine."
                    ),
                    highlightNote = "An algorithm is language-agnostic; Python, Kotlin, and C++ are simply implementation mediums for the underlying algorithm logic."
                ),
                LessonSection(
                    id = "algo_invariants",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Loop Invariants & Correctness Proofs",
                    body = "How do computer scientists prove an algorithm works for every possible input? The most common tool is the Loop Invariant — a condition that remains true before and after each iteration of a loop.",
                    bulletPoints = listOf(
                        "Initialization: The invariant is true prior to the first iteration of the loop.",
                        "Maintenance: If the invariant is true before an iteration, it remains true before the next iteration.",
                        "Termination: When the loop terminates, the invariant provides a useful property that proves algorithm correctness."
                    ),
                    highlightNote = "Loop invariants are the algorithmic equivalent of mathematical induction."
                ),
                LessonSection(
                    id = "algo_vs_hardware",
                    type = LessonSectionType.REAL_WORLD_EXAMPLES,
                    title = "Algorithm Efficiency vs. Hardware Power",
                    body = "A common beginner misconception is: 'Computers are so fast today that algorithm efficiency doesn't matter.' Mathematics proves otherwise.\n\nImagine Computer A runs an O(N^2) algorithm on a supercomputer capable of 10 billion operations/sec. Computer B runs an O(N log N) algorithm on a smartphone executing 10 million operations/sec.\nFor N = 10,000,000 elements:\n- Supercomputer A takes ~10,000 seconds (over 2.7 hours).\n- Smartphone B takes ~23 seconds.\n\nAlgorithm design always beats brute hardware force.",
                    bulletPoints = listOf(
                        "Recipe Analogy: A baking recipe specifying precise measurements and step sequence.",
                        "GPS Navigation: Dijkstra or A* finding shortest path among millions of road segments.",
                        "Data Compression: Huffman and LZW algorithms shrinking gigabytes into megabytes."
                    )
                ),
                LessonSection(
                    id = "algo_design_paradigms",
                    type = LessonSectionType.USE_CASES,
                    title = "Major Algorithmic Paradigms",
                    body = "Most algorithms belong to a small set of proven design strategies:\n\n- Brute Force: Systematically evaluate every possible candidate solution.\n- Divide and Conquer: Split problem into independent sub-problems, solve recursively, and merge results (Merge Sort, Quick Sort).\n- Greedy Approach: Make the locally optimal choice at each step hoping for global optimum (Dijkstra, Prim's).\n- Dynamic Programming: Store solutions to overlapping sub-problems to eliminate redundant computation (Knapsack, Fibonacci, LCS).",
                    bulletPoints = listOf(
                        "Sorting & Searching: Fundamental building blocks of all computational systems.",
                        "Optimization: Finding minimal cost, maximum throughput, or shortest path.",
                        "Machine Learning: Gradient descent iteratively minimizing loss functions."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Finding Maximum Element (Loop Invariant Demonstration)",
                    language = "KOTLIN",
                    code = """// Invariant: At start of iteration i, 'maxVal' holds maximum of array[0..i-1]
fun findMax(numbers: IntArray): Int {
    require(numbers.isNotEmpty()) { "Array must not be empty" }
    var maxVal = numbers[0] // Initialization: holds max of numbers[0..0]
    
    for (i in 1 until numbers.size) {
        // Maintenance: if numbers[i] > maxVal, update maxVal
        if (numbers[i] > maxVal) {
            maxVal = numbers[i]
        }
    }
    // Termination: loop terminates at i = numbers.size; maxVal holds max of numbers[0..n-1]
    return maxVal
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-4", "Check precondition non-empty, initialize maxVal with first element."),
                        CodeStepExplanation(2, "Lines 6-10", "Iterate from index 1 to end; update maxVal whenever a greater number is observed."),
                        CodeStepExplanation(3, "Lines 11-12", "On loop termination, maxVal is mathematically proven to be greatest in entire array.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "An algorithm must possess finiteness, definiteness, effectiveness, input, and output.",
                "Loop invariants prove algorithmic correctness via initialization, maintenance, and termination.",
                "Algorithmic time complexity dominates hardware execution speeds at scale.",
                "Mastering fundamental paradigms (Greedy, Divide & Conquer, DP) enables solving complex engineering problems."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_prog_intro_algo_1",
                    question = "Which characteristic is MANDATORY for a valid algorithm?",
                    options = listOf(
                        "It must guarantee termination in a finite number of steps",
                        "It must be written in an object-oriented programming language",
                        "It must execute in O(1) constant time",
                        "It must use recursive function calls"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Finiteness is a required property: an algorithm must always terminate after a countable number of discrete steps for all valid inputs."
                ),
                LessonPracticeQuestion(
                    id = "pq_prog_intro_algo_2",
                    question = "What are the three mandatory stages of proving an algorithm with a loop invariant?",
                    options = listOf(
                        "Initialization, Maintenance, and Termination",
                        "Compilation, Execution, and Garbage Collection",
                        "Input, Processing, and Output",
                        "Best Case, Worst Case, and Average Case"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Proving correctness via loop invariant requires demonstrating Initialization (holds before start), Maintenance (holds between iterations), and Termination (guarantees correct outcome at finish)."
                )
            )
        ),

        // 3. Time Complexity & Analysis
        DsaLessonContent(
            lessonId = "prog_time_complexity_l1",
            subtopicId = "prog_time_complexity",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Time Complexity & Analysis",
            subtitle = "Quantifying execution time mathematically as input magnitude grows towards infinity",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(1) to O(2^N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Learn why counting primitive CPU operations is independent of benchmark clocks.",
                "Analyze single loops, nested loops, and halving loops to derive exact complexities.",
                "Understand the rule of dominant terms and why lower-order terms and constants are dropped.",
                "Calculate Best-Case (Omega), Worst-Case (Big-O), and Average-Case (Theta) metrics."
            ),
            sections = listOf(
                LessonSection(
                    id = "time_comp_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Why Not Measure in Seconds?",
                    body = "Benchmarking code with a stopwatch (wall-clock time) is flawed because runtime depends on CPU architecture, background OS tasks, compiler optimizations, and temperature throttling.\n\nTime complexity measures the growth rate of required primitive operations as a function of input size N. It allows comparing algorithms scientifically across all hardware.",
                    bulletPoints = listOf(
                        "Primitive Operations: Assignment (x = 5), arithmetic (a + b), comparison (a < b), array indexing (arr[i]).",
                        "Input Size (N): Number of elements in an array, nodes in a graph, or characters in a string.",
                        "Asymptotic Analysis: Evaluating behavior as N grows arbitrarily large (N -> infinity)."
                    )
                ),
                LessonSection(
                    id = "loop_rules",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Rules for Analyzing Loops",
                    body = "Code patterns translate directly to mathematical complexity formulas:\n\n1. Single Loop (0 to N): Executes N times -> O(N) linear time.\n2. Nested Loops (N outer * N inner): Executes N * N times -> O(N^2) quadratic time.\n3. Halving/Doubling Loop (i *= 2 or i /= 2): Number of steps satisfies 2^k = N, so k = log2(N) -> O(log N) logarithmic time.\n4. Consecutive Statements: Add complexities: O(A) + O(B) = O(max(A, B)).",
                    bulletPoints = listOf(
                        "Drop Constants: 3N + 50 becomes O(N) because 3 is a constant multiplier.",
                        "Drop Non-Dominant Terms: N^2 + 100N + 500 becomes O(N^2) because as N grows to 10^6, N^2 dominates everything."
                    ),
                    highlightNote = "Always focus on the fastest-growing term as N approaches infinity."
                ),
                LessonSection(
                    id = "cases_analysis",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "Best, Worst, and Average Cases",
                    body = "An algorithm's runtime can vary dramatically depending on input arrangement:\n\n- Best Case (Omega): Minimal operations required under ideal conditions (e.g., target found at index 0 in Linear Search -> O(1)).\n- Worst Case (Big-O): Upper boundary under the most adversarial conditions (e.g., target missing from array -> O(N)).\n- Average Case (Theta): Expected runtime across uniform input distribution (e.g., target found halfway -> N/2 comparisons -> O(N)).",
                    bulletPoints = listOf(
                        "Software engineers usually design for the Worst Case to provide guaranteed SLA bounds.",
                        "QuickSort is O(N log N) average case, but O(N^2) worst case when poorly pivoted."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Analyzing O(1), O(N), O(N^2), and O(log N) Patterns",
                    language = "KOTLIN",
                    code = """// 1. O(1) Constant Time: zero loops
fun getFirst(arr: IntArray): Int = arr[0]

// 2. O(N) Linear Time: single loop over N
fun sumElements(arr: IntArray): Int {
    var total = 0
    for (x in arr) total += x
    return total
}

// 3. O(N^2) Quadratic Time: nested loops
fun printAllPairs(arr: IntArray) {
    for (i in arr.indices) {
        for (j in arr.indices) {
            println("${'$'}{arr[i]}, ${'$'}{arr[j]}")
        }
    }
}

// 4. O(log N) Logarithmic Time: index doubles each step
fun logSteps(n: Int): Int {
    var count = 0
    var i = 1
    while (i < n) {
        count++
        i *= 2 // Doubles each iteration
    }
    return count
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2", "Constant index access executes in 1 step regardless of array length."),
                        CodeStepExplanation(2, "Lines 5-9", "Loop executes exactly N times where N is arr.size."),
                        CodeStepExplanation(3, "Lines 12-18", "Outer loop executes N times; inner loop runs N times for each outer step = N * N operations."),
                        CodeStepExplanation(4, "Lines 21-29", "Counter i doubles every step: 1, 2, 4, 8... reaches N in log2(N) steps.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Time complexity evaluates operation scaling as input size N tends toward infinity.",
                "Constants (e.g., 5N) and lower-order terms (e.g., N^2 + N) are dropped under asymptotic analysis.",
                "Doubling or halving iterations yields O(log N) logarithmic runtime.",
                "Worst-case analysis provides reliable upper-bound performance guarantees for critical software."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_prog_time_comp_1",
                    question = "What is the time complexity of a loop where the index variable doubles each step (i = 1; while(i < N) i *= 2)?",
                    options = listOf("O(log N)", "O(N)", "O(N^2)", "O(1)"),
                    correctOptionIndex = 0,
                    explanation = "When the loop index multiplies or divides by a factor (like 2) each step, the total number of iterations k satisfies 2^k = N, meaning k = log2(N)."
                ),
                LessonPracticeQuestion(
                    id = "pq_prog_time_comp_2",
                    question = "If an algorithm's exact step count is f(N) = 7N^2 + 45N + 120, what is its asymptotic Big-O time complexity?",
                    options = listOf("O(N^2)", "O(N)", "O(7N^2)", "O(N^3)"),
                    correctOptionIndex = 0,
                    explanation = "Under asymptotic rules, constant multipliers (7) and lower-order terms (45N, 120) are eliminated, leaving the dominant term O(N^2)."
                )
            )
        ),

        // 4. Space Complexity & Memory
        DsaLessonContent(
            lessonId = "prog_space_complexity_l1",
            subtopicId = "prog_space_complexity",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Space Complexity & Memory",
            subtitle = "Evaluating total memory footprint and auxiliary memory allocated during execution",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) to O(N)",
            spaceComplexity = "O(1) to O(N)",
            learningObjectives = listOf(
                "Distinguish between Total Space Complexity and Auxiliary Space Complexity.",
                "Understand Stack memory allocations (call stacks) vs Heap memory allocations (dynamic objects).",
                "Recognize In-Place algorithms and why O(1) auxiliary space is prized in memory-constrained devices.",
                "Analyze memory leaks and recursion stack overflow risks."
            ),
            sections = listOf(
                LessonSection(
                    id = "space_comp_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Space Complexity?",
                    body = "Space complexity measures the total amount of working memory an algorithm requires as a function of the input size N.\n\nIn mobile apps (Android) and embedded systems, RAM is constrained. An algorithm running fast but allocating gigabytes of temporary arrays can trigger OutOfMemoryError and crash the application.",
                    bulletPoints = listOf(
                        "Input Space: Memory required to store the incoming inputs.",
                        "Auxiliary Space: Extra or temporary memory allocated exclusively by the algorithm logic (scratch arrays, recursion stack).",
                        "Total Space: Input Space + Auxiliary Space."
                    ),
                    highlightNote = "When interviewers ask for Space Complexity, they usually mean Auxiliary Space — the extra memory your algorithm creates."
                ),
                LessonSection(
                    id = "stack_vs_heap",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Call Stack vs. Heap Allocation",
                    body = "Memory consumed during execution is split between two primary zones:\n\n1. Stack Memory: Stores local primitive variables and function activation frames. Every recursive call pushes a new frame. A recursion of depth N consumes O(N) stack memory.\n2. Heap Memory: Stores dynamically instantiated objects, arrays, and lists (e.g., IntArray(N)).",
                    bulletPoints = listOf(
                        "In-Place Algorithm: An algorithm requiring O(1) auxiliary space beyond the input (e.g., Bubble Sort, In-Place Reversal).",
                        "Out-of-Place Algorithm: An algorithm copying data into auxiliary buffers of size O(N) (e.g., Merge Sort)."
                    ),
                    highlightNote = "A recursive algorithm with no local allocations still uses O(depth) auxiliary space due to the call stack!"
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "In-Place O(1) Space vs. Out-of-Place O(N) Space",
                    language = "KOTLIN",
                    code = """// 1. Out-of-Place: Allocates a new array of size N -> O(N) Auxiliary Space
fun reverseCopy(arr: IntArray): IntArray {
    val result = IntArray(arr.size) // O(N) memory allocated on Heap
    for (i in arr.indices) {
        result[arr.size - 1 - i] = arr[i]
    }
    return result
}

// 2. In-Place: Modifies array using two pointers -> O(1) Auxiliary Space
fun reverseInPlace(arr: IntArray) {
    var left = 0
    var right = arr.size - 1
    while (left < right) {
        val temp = arr[left] // single scalar variable: O(1)
        arr[left] = arr[right]
        arr[right] = temp
        left++
        right--
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-4", "reverseCopy allocates an entirely new array of size N, consuming O(N) heap memory."),
                        CodeStepExplanation(2, "Lines 10-14", "reverseInPlace allocates only three primitive integers (left, right, temp). Memory footprint is constant O(1).")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Auxiliary space measures additional memory created by the algorithm, excluding the input itself.",
                "Recursive function calls consume O(depth) stack space for execution frames.",
                "In-place algorithms modify existing data structures using O(1) auxiliary memory.",
                "Mobile and embedded systems heavily prioritize O(1) auxiliary space to prevent garbage collection pauses."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_prog_space_comp_1",
                    question = "What is the auxiliary space complexity of an algorithm that swaps elements inside the input array using one temporary variable?",
                    options = listOf("O(1) Constant", "O(N) Linear", "O(log N)", "O(N^2)"),
                    correctOptionIndex = 0,
                    explanation = "Because only one scalar temporary variable is allocated regardless of array size, the auxiliary memory footprint is O(1) constant."
                ),
                LessonPracticeQuestion(
                    id = "pq_prog_space_comp_2",
                    question = "Why does a recursive function that makes N recursive calls consume O(N) auxiliary space even without creating any lists?",
                    options = listOf(
                        "Each recursive call pushes an activation stack frame onto the Call Stack",
                        "Because the CPU doubles heap size automatically",
                        "Because JVM garbage collection requires duplicate memory",
                        "Because recursion converts variables to strings"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Every active function call allocates a stack frame storing parameters, return address, and local variables. A recursion depth of N requires N simultaneous stack frames."
                )
            )
        ),

        // 5. Asymptotic Notations & Big-O
        DsaLessonContent(
            lessonId = "prog_big_o_l1",
            subtopicId = "prog_big_o",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Asymptotic Notations & Big-O",
            subtitle = "Mathematical definitions of Big-O, Big-Omega, Big-Theta, and the growth hierarchy",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(1) to O(N!)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Master the formal mathematical definitions of Big-O, Big-Omega, and Big-Theta.",
                "Order the fundamental complexity classes from fastest to slowest.",
                "Learn why Big-O represents an asymptotic upper bound (f(N) <= c * g(N)).",
                "Apply the Limit Test and understand why exponential/factorial algorithms are intractable."
            ),
            sections = listOf(
                LessonSection(
                    id = "big_o_math",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Mathematical Definitions of Asymptotic Notations",
                    body = "Asymptotic notation provides a rigorous mathematical framework to categorize function growth rates:\n\n1. Big-O (O) - Asymptotic Upper Bound:\nf(N) = O(g(N)) means there exist positive constants c and N0 such that f(N) <= c * g(N) for all N >= N0. It guarantees runtime will never exceed this ceiling.\n\n2. Big-Omega (Ω) - Asymptotic Lower Bound:\nf(N) = Ω(g(N)) means f(N) >= c * g(N) for all N >= N0. It guarantees runtime will not drop below this floor.\n\n3. Big-Theta (Θ) - Asymptotically Tight Bound:\nf(N) = Θ(g(N)) means f(N) is bounded above and below by g(N): c1 * g(N) <= f(N) <= c2 * g(N).",
                    bulletPoints = listOf(
                        "Big-O: Worst-case ceiling (Safety guarantee).",
                        "Big-Omega: Best-case floor.",
                        "Big-Theta: Exact order of magnitude."
                    )
                ),
                LessonSection(
                    id = "growth_hierarchy",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Complexity Growth Hierarchy",
                    body = "From optimal to intractable, algorithms fall into well-defined mathematical complexity classes:\n\n1. O(1) Constant: Array index, push/pop stack, hash lookup.\n2. O(log N) Logarithmic: Binary search, balanced BST search.\n3. O(N) Linear: Linear scan, array copy, counting elements.\n4. O(N log N) Linearithmic: Merge sort, Quick sort (average), Heap sort.\n5. O(N^2) Quadratic: Bubble sort, selection sort, nested loops.\n6. O(2^N) Exponential: Generating all power set subsets, recursive Fibonacci.\n7. O(N!) Factorial: Generating all permutations, Traveling Salesperson brute force.",
                    bulletPoints = listOf(
                        "When N = 50: O(N) takes 50 steps; O(N^2) takes 2,500 steps; O(2^N) takes 1.12 * 10^15 steps (thousands of years)!"
                    ),
                    highlightNote = "Hierarchy Rule: O(1) < O(log N) < O(N) < O(N log N) < O(N^2) < O(2^N) < O(N!)."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Comparing Complexity Classes in Practice",
                    language = "KOTLIN",
                    code = """// O(1): Constant
fun isEven(n: Int): Boolean = n % 2 == 0

// O(log N): Binary Search
fun binarySearch(arr: IntArray, target: Int): Int {
    var low = 0
    var high = arr.size - 1
    while (low <= high) {
        val mid = low + (high - low) / 2
        when {
            arr[mid] == target -> return mid
            arr[mid] < target -> low = mid + 1
            else -> high = mid - 1
        }
    }
    return -1
}

// O(2^N): Exponential (Intractable for N > 40)
fun fibNaive(n: Int): Int {
    if (n <= 1) return n
    return fibNaive(n - 1) + fibNaive(n - 2) // Doubles work each level
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 2", "isEven executes in single modulo and check = O(1)."),
                        CodeStepExplanation(2, "Lines 5-17", "binarySearch cuts search window in half each step = O(log N)."),
                        CodeStepExplanation(3, "Lines 20-23", "fibNaive branches into 2 calls per level, creating a full binary tree of height N = O(2^N).")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Big-O represents an asymptotic upper bound (ceiling) on algorithm growth rate.",
                "Big-Omega is the lower bound (floor), and Big-Theta is the tight mathematical bound.",
                "The complexity hierarchy: O(1) < O(log N) < O(N) < O(N log N) < O(N^2) < O(2^N) < O(N!).",
                "Exponential O(2^N) and Factorial O(N!) algorithms become practically uncomputable even for modest N."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_prog_big_o_1",
                    question = "Which of the following complexity classes grows the FASTEST (i.e., is the least efficient)?",
                    options = listOf("O(2^N)", "O(N log N)", "O(N^2)", "O(N^3)"),
                    correctOptionIndex = 0,
                    explanation = "Exponential O(2^N) grows substantially faster than polynomial complexities (N^2, N^3) or linearithmic O(N log N) as N grows large."
                ),
                LessonPracticeQuestion(
                    id = "pq_prog_big_o_2",
                    question = "What does the formal statement f(N) = O(g(N)) guarantee?",
                    options = listOf(
                        "There exist positive constants c and N0 such that f(N) <= c * g(N) for all N >= N0",
                        "f(N) is equal to g(N) for all values of N",
                        "f(N) will always execute faster than g(N) on any CPU",
                        "The algorithm uses exactly g(N) bytes of RAM"
                    ),
                    correctOptionIndex = 0,
                    explanation = "By mathematical definition, Big-O specifies an upper bound: beyond threshold N0, f(N) is bounded above by a constant multiple of g(N)."
                )
            )
        )
    )
}
