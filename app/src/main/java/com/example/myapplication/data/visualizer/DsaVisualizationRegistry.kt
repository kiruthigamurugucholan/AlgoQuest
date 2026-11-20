package com.example.myapplication.data.visualizer

import com.example.myapplication.data.model.visualizer.DsaVisualizerScenario
import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.visualizer.generators.AdvancedAlgorithmVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.ArrayVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.GraphVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.LinkedListVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.ProgrammingBasicsVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.QueueVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.RecursionVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.SearchingVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.SortingVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.StackVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.StringVisualizerGenerator
import com.example.myapplication.data.visualizer.generators.TreeVisualizerGenerator

/**
 * Universal Registry providing interactive visualization scenarios for all 65 DSA catalogue topics.
 */
object DsaVisualizationRegistry {

    private val scenarios: Map<String, DsaVisualizerScenario> = listOf(
        // Category 1: Programming Basics (5 subtopics)
        DsaVisualizerScenario(
            subtopicId = "prog_intro_ds",
            title = "Memory Allocation & Contiguity",
            categoryName = "Programming Basics",
            vizType = DsaVizType.ARRAY,
            defaultInput = "10, 20, 30, 40, 50",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated integers",
            timeComplexity = "O(1) access",
            spaceComplexity = "O(N) memory",
            generateSteps = { ProgrammingBasicsVisualizerGenerator.generateIntroDs(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "prog_intro_algo",
            title = "Algorithm Pipeline Execution",
            categoryName = "Programming Basics",
            vizType = DsaVizType.ARRAY,
            defaultInput = "5, 3, 8, 4, 2",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated integers",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { ProgrammingBasicsVisualizerGenerator.generateIntroAlgo(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "prog_time_complexity",
            title = "Asymptotic Scaling Simulation",
            categoryName = "Programming Basics",
            vizType = DsaVizType.FLOWCHART,
            defaultInput = "10, 100, 1000",
            supportsCustomInput = false,
            inputHelpText = "N scaling parameters",
            timeComplexity = "O(1) to O(N^2)",
            spaceComplexity = "O(1)",
            generateSteps = { ProgrammingBasicsVisualizerGenerator.generateTimeComplexity(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "prog_space_complexity",
            title = "Fixed vs Auxiliary Memory Allocation",
            categoryName = "Programming Basics",
            vizType = DsaVizType.FLOWCHART,
            defaultInput = "Heap vs Stack",
            supportsCustomInput = false,
            inputHelpText = "Memory allocation modes",
            timeComplexity = "O(1)",
            spaceComplexity = "O(1) vs O(N)",
            generateSteps = { ProgrammingBasicsVisualizerGenerator.generateSpaceComplexity(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "prog_big_o",
            title = "Big O Notation Boundary Growth",
            categoryName = "Programming Basics",
            vizType = DsaVizType.FLOWCHART,
            defaultInput = "f(N) = 3N^2 + 50N",
            supportsCustomInput = false,
            inputHelpText = "Polynomial terms",
            timeComplexity = "Worst-case upper bound",
            spaceComplexity = "O(1)",
            generateSteps = { ProgrammingBasicsVisualizerGenerator.generateBigONotation(it) }
        ),

        // Category 2: Arrays (6 subtopics)
        DsaVisualizerScenario(
            subtopicId = "array_traversal",
            title = "Array Traversal & Scanning",
            categoryName = "Arrays",
            vizType = DsaVizType.ARRAY,
            defaultInput = "14, 27, 33, 49, 58, 62",
            supportsCustomInput = true,
            inputHelpText = "2 to 10 comma-separated integers",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { ArrayVisualizerGenerator.generateArrayTraversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "array_insertion",
            title = "Array Insertion & Element Shifting",
            categoryName = "Arrays",
            vizType = DsaVizType.ARRAY,
            defaultInput = "10, 20, 30, 40, 50",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated integers",
            timeComplexity = "O(N) shifts",
            spaceComplexity = "O(1)",
            generateSteps = { ArrayVisualizerGenerator.generateArrayInsertion(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "array_deletion",
            title = "Array Deletion & Memory Compaction",
            categoryName = "Arrays",
            vizType = DsaVizType.ARRAY,
            defaultInput = "15, 25, 35, 45, 55, 65",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated integers",
            timeComplexity = "O(N) shifts",
            spaceComplexity = "O(1)",
            generateSteps = { ArrayVisualizerGenerator.generateArrayDeletion(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "array_operations",
            title = "Direct Index Access & In-Place Mutation",
            categoryName = "Arrays",
            vizType = DsaVizType.ARRAY,
            defaultInput = "12, 24, 36, 48, 60, 72",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated integers",
            timeComplexity = "O(1) random access",
            spaceComplexity = "O(1)",
            generateSteps = { ArrayVisualizerGenerator.generateArrayOperations(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "array_two_pointers",
            title = "Two Pointers Converging Search",
            categoryName = "Arrays",
            vizType = DsaVizType.ARRAY,
            defaultInput = "2, 4, 7, 11, 15, 18, 22",
            supportsCustomInput = true,
            inputHelpText = "Sorted comma-separated integers",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { ArrayVisualizerGenerator.generateTwoPointers(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "array_sliding_window",
            title = "Sliding Window Maximum Subarray",
            categoryName = "Arrays",
            vizType = DsaVizType.ARRAY,
            defaultInput = "2, 1, 5, 1, 3, 2",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated integers",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { ArrayVisualizerGenerator.generateSlidingWindow(it) }
        ),

        // Category 3: Searching (4 subtopics)
        DsaVisualizerScenario(
            subtopicId = "search_linear",
            title = "Linear Search (Sequential Scan)",
            categoryName = "Searching",
            vizType = DsaVizType.SEARCH,
            defaultInput = "18, 42, 7, 91, 53, 34, 88; 53",
            supportsCustomInput = true,
            inputHelpText = "Array numbers ; target (e.g. 10,20,30; 20)",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { SearchingVisualizerGenerator.generateLinearSearch(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "search_binary",
            title = "Binary Search (Interval Halving)",
            categoryName = "Searching",
            vizType = DsaVizType.SEARCH,
            defaultInput = "4, 9, 15, 23, 38, 56, 72, 91; 38",
            supportsCustomInput = true,
            inputHelpText = "Sorted numbers ; target (e.g. 2,4,6,8; 6)",
            timeComplexity = "O(log N)",
            spaceComplexity = "O(1)",
            generateSteps = { SearchingVisualizerGenerator.generateBinarySearch(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "search_jump",
            title = "Jump Search (Block Hopping)",
            categoryName = "Searching",
            vizType = DsaVizType.SEARCH,
            defaultInput = "0, 1, 1, 2, 3, 5, 8, 13, 21, 34; 13",
            supportsCustomInput = true,
            inputHelpText = "Sorted numbers ; target",
            timeComplexity = "O(√N)",
            spaceComplexity = "O(1)",
            generateSteps = { SearchingVisualizerGenerator.generateJumpSearch(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "search_interpolation",
            title = "Interpolation Search (Formula Probing)",
            categoryName = "Searching",
            vizType = DsaVizType.SEARCH,
            defaultInput = "10, 20, 30, 40, 50, 60, 70, 80; 50",
            supportsCustomInput = true,
            inputHelpText = "Uniformly distributed numbers ; target",
            timeComplexity = "O(log log N) avg",
            spaceComplexity = "O(1)",
            generateSteps = { SearchingVisualizerGenerator.generateInterpolationSearch(it) }
        ),

        // Category 4: Sorting (7 subtopics)
        DsaVisualizerScenario(
            subtopicId = "sort_bubble",
            title = "Bubble Sort (Adjacent Swaps)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "29, 10, 14, 37, 13",
            supportsCustomInput = true,
            inputHelpText = "2 to 8 comma-separated numbers",
            timeComplexity = "O(N^2)",
            spaceComplexity = "O(1)",
            generateSteps = { SortingVisualizerGenerator.generateBubbleSort(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "sort_selection",
            title = "Selection Sort (Minimum Placement)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "64, 25, 12, 22, 11",
            supportsCustomInput = true,
            inputHelpText = "2 to 8 comma-separated numbers",
            timeComplexity = "O(N^2)",
            spaceComplexity = "O(1)",
            generateSteps = { SortingVisualizerGenerator.generateSelectionSort(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "sort_insertion",
            title = "Insertion Sort (Sorted Prefix Sifting)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "12, 11, 13, 5, 6",
            supportsCustomInput = true,
            inputHelpText = "2 to 8 comma-separated numbers",
            timeComplexity = "O(N^2) avg, O(N) best",
            spaceComplexity = "O(1)",
            generateSteps = { SortingVisualizerGenerator.generateInsertionSort(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "sort_merge",
            title = "Merge Sort (Divide & Interleave)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "38, 27, 43, 3, 9, 82",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated numbers",
            timeComplexity = "O(N log N) guaranteed",
            spaceComplexity = "O(N)",
            generateSteps = { SortingVisualizerGenerator.generateMergeSort(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "sort_quick",
            title = "Quick Sort (Lomuto Partitioning)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "10, 80, 30, 90, 40, 50, 70",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated numbers",
            timeComplexity = "O(N log N) avg",
            spaceComplexity = "O(log N) stack",
            generateSteps = { SortingVisualizerGenerator.generateQuickSort(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "sort_heap",
            title = "Heap Sort (Max-Heap & Sift Down)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "4, 10, 3, 5, 1",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated numbers",
            timeComplexity = "O(N log N)",
            spaceComplexity = "O(1) in-place",
            generateSteps = { SortingVisualizerGenerator.generateHeapSort(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "sort_counting",
            title = "Counting Sort (Frequency Histogram)",
            categoryName = "Sorting",
            vizType = DsaVizType.SORT,
            defaultInput = "4, 2, 2, 8, 3, 3, 1",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated positive integers",
            timeComplexity = "O(N + K)",
            spaceComplexity = "O(K)",
            generateSteps = { SortingVisualizerGenerator.generateCountingSort(it) }
        ),

        // Category 5: Strings (5 subtopics)
        DsaVisualizerScenario(
            subtopicId = "string_traversal",
            title = "String Traversal & ASCII Inspection",
            categoryName = "Strings",
            vizType = DsaVizType.STRING,
            defaultInput = "ALGORITHM",
            supportsCustomInput = true,
            inputHelpText = "Text string (3-12 characters)",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { StringVisualizerGenerator.generateStringTraversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "string_reversal",
            title = "Two-Pointer In-Place Reversal",
            categoryName = "Strings",
            vizType = DsaVizType.STRING,
            defaultInput = "QUEST",
            supportsCustomInput = true,
            inputHelpText = "Text string",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { StringVisualizerGenerator.generateStringReversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "string_palindrome",
            title = "Symmetric Palindrome Checking",
            categoryName = "Strings",
            vizType = DsaVizType.STRING,
            defaultInput = "RACECAR",
            supportsCustomInput = true,
            inputHelpText = "Text string (e.g. LEVEL, RADAR)",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { StringVisualizerGenerator.generatePalindromeChecking(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "string_anagram",
            title = "Anagram Character Frequency Matching",
            categoryName = "Strings",
            vizType = DsaVizType.STRING,
            defaultInput = "LISTEN / SILENT",
            supportsCustomInput = false,
            inputHelpText = "Pair of strings",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) alphabet",
            generateSteps = { StringVisualizerGenerator.generateAnagramChecking(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "string_pattern_matching",
            title = "Sliding Window Pattern Matching",
            categoryName = "Strings",
            vizType = DsaVizType.STRING,
            defaultInput = "AABA in AABAACAADAABAABA",
            supportsCustomInput = false,
            inputHelpText = "Pattern in text",
            timeComplexity = "O(N * M)",
            spaceComplexity = "O(1)",
            generateSteps = { StringVisualizerGenerator.generatePatternMatching(it) }
        ),

        // Category 6: Linked Lists (5 subtopics)
        DsaVisualizerScenario(
            subtopicId = "ll_singly",
            title = "Singly Linked List Traversal",
            categoryName = "Linked Lists",
            vizType = DsaVizType.LINKED_LIST,
            defaultInput = "10 -> 20 -> 30 -> 40 -> 50",
            supportsCustomInput = false,
            inputHelpText = "Node chain",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { LinkedListVisualizerGenerator.generateSinglyLinkedList(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "ll_doubly",
            title = "Doubly Linked List Bidirectional Traversal",
            categoryName = "Linked Lists",
            vizType = DsaVizType.LINKED_LIST,
            defaultInput = "A <-> B <-> C <-> D",
            supportsCustomInput = false,
            inputHelpText = "Prev/Next nodes",
            timeComplexity = "O(N) traversal, O(1) delete",
            spaceComplexity = "O(1)",
            generateSteps = { LinkedListVisualizerGenerator.generateDoublyLinkedList(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "ll_circular",
            title = "Circular Linked List Ring Traversal",
            categoryName = "Linked Lists",
            vizType = DsaVizType.LINKED_LIST,
            defaultInput = "1 -> 2 -> 3 -> 4 -> (1)",
            supportsCustomInput = false,
            inputHelpText = "Circular ring chain",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { LinkedListVisualizerGenerator.generateCircularLinkedList(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "ll_insertion_deletion",
            title = "Node Insertion & Deletion Pointer Rewiring",
            categoryName = "Linked Lists",
            vizType = DsaVizType.LINKED_LIST,
            defaultInput = "Insert 25 into [10, 20, 30]",
            supportsCustomInput = false,
            inputHelpText = "Node insertion & bypass",
            timeComplexity = "O(1) pointer updates",
            spaceComplexity = "O(1)",
            generateSteps = { LinkedListVisualizerGenerator.generateInsertionDeletion(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "ll_reversal",
            title = "In-Place Iterative List Reversal",
            categoryName = "Linked Lists",
            vizType = DsaVizType.LINKED_LIST,
            defaultInput = "1 -> 2 -> 3 -> 4 -> null",
            supportsCustomInput = false,
            inputHelpText = "3-pointer flip",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            generateSteps = { LinkedListVisualizerGenerator.generateLinkedListReversal(it) }
        ),

        // Category 7: Stack (5 subtopics)
        DsaVisualizerScenario(
            subtopicId = "stack_operations",
            title = "Stack Operations (Push, Pop, Peek)",
            categoryName = "Stack",
            vizType = DsaVizType.STACK,
            defaultInput = "Alpha, Beta, Gamma",
            supportsCustomInput = false,
            inputHelpText = "LIFO stack items",
            timeComplexity = "O(1) all ops",
            spaceComplexity = "O(N)",
            generateSteps = { StackVisualizerGenerator.generateStackOperations(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "stack_push_pop",
            title = "Push & Pop Pointer Mechanics",
            categoryName = "Stack",
            vizType = DsaVizType.STACK,
            defaultInput = "10, 20, 30",
            supportsCustomInput = false,
            inputHelpText = "Sequential push/pop",
            timeComplexity = "O(1)",
            spaceComplexity = "O(N)",
            generateSteps = { StackVisualizerGenerator.generatePushPop(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "stack_infix_postfix",
            title = "Infix to Postfix Operator Stack",
            categoryName = "Stack",
            vizType = DsaVizType.STACK,
            defaultInput = "A + B * C",
            supportsCustomInput = false,
            inputHelpText = "Arithmetic expression",
            timeComplexity = "O(N)",
            spaceComplexity = "O(N)",
            generateSteps = { StackVisualizerGenerator.generateInfixToPostfix(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "stack_balanced_parentheses",
            title = "Balanced Parentheses & Brackets Validation",
            categoryName = "Stack",
            vizType = DsaVizType.STACK,
            defaultInput = "{ [ ( ) ] }",
            supportsCustomInput = false,
            inputHelpText = "Bracket expression",
            timeComplexity = "O(N)",
            spaceComplexity = "O(N)",
            generateSteps = { StackVisualizerGenerator.generateBalancedParentheses(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "stack_applications",
            title = "Next Greater Element (Monotonic Stack)",
            categoryName = "Stack",
            vizType = DsaVizType.STACK,
            defaultInput = "4, 5, 2, 25",
            supportsCustomInput = false,
            inputHelpText = "Monotonic sequence",
            timeComplexity = "O(N)",
            spaceComplexity = "O(N)",
            generateSteps = { StackVisualizerGenerator.generateStackApplications(it) }
        ),

        // Category 8: Queue (4 subtopics)
        DsaVisualizerScenario(
            subtopicId = "queue_simple",
            title = "Simple Queue (FIFO Enqueue & Dequeue)",
            categoryName = "Queue",
            vizType = DsaVizType.QUEUE,
            defaultInput = "10, 20, 30",
            supportsCustomInput = false,
            inputHelpText = "FIFO sequence",
            timeComplexity = "O(1) enqueue/dequeue",
            spaceComplexity = "O(N)",
            generateSteps = { QueueVisualizerGenerator.generateSimpleQueue(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "queue_circular",
            title = "Circular Queue Modulo Ring Buffer",
            categoryName = "Queue",
            vizType = DsaVizType.QUEUE,
            defaultInput = "Ring size = 5",
            supportsCustomInput = false,
            inputHelpText = "Modulo ring capacity",
            timeComplexity = "O(1)",
            spaceComplexity = "O(N) bounded",
            generateSteps = { QueueVisualizerGenerator.generateCircularQueue(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "queue_priority",
            title = "Priority Queue Urgency Extraction",
            categoryName = "Queue",
            vizType = DsaVizType.QUEUE,
            defaultInput = "Task Priority Orders",
            supportsCustomInput = false,
            inputHelpText = "Urgency order",
            timeComplexity = "O(log N)",
            spaceComplexity = "O(N)",
            generateSteps = { QueueVisualizerGenerator.generatePriorityQueue(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "queue_deque",
            title = "Double-Ended Queue (Deque)",
            categoryName = "Queue",
            vizType = DsaVizType.QUEUE,
            defaultInput = "Head / Tail endpoints",
            supportsCustomInput = false,
            inputHelpText = "Double ended operations",
            timeComplexity = "O(1) all ends",
            spaceComplexity = "O(N)",
            generateSteps = { QueueVisualizerGenerator.generateDeque(it) }
        ),

        // Category 9: Recursion (5 subtopics)
        DsaVisualizerScenario(
            subtopicId = "rec_intro",
            title = "Recursion Base Case & Stack Frames",
            categoryName = "Recursion",
            vizType = DsaVizType.RECURSION,
            defaultInput = "countdown(3)",
            supportsCustomInput = false,
            inputHelpText = "Recursive countdown",
            timeComplexity = "O(N)",
            spaceComplexity = "O(N) stack",
            generateSteps = { RecursionVisualizerGenerator.generateRecursionIntro(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "rec_factorial",
            title = "Factorial Recursive Unwinding",
            categoryName = "Recursion",
            vizType = DsaVizType.RECURSION,
            defaultInput = "fact(4)",
            supportsCustomInput = false,
            inputHelpText = "Factorial depth",
            timeComplexity = "O(N)",
            spaceComplexity = "O(N) stack",
            generateSteps = { RecursionVisualizerGenerator.generateFactorial(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "rec_fibonacci",
            title = "Fibonacci Branching Tree Recursion",
            categoryName = "Recursion",
            vizType = DsaVizType.RECURSION,
            defaultInput = "fib(4)",
            supportsCustomInput = false,
            inputHelpText = "Binary recursive branches",
            timeComplexity = "O(2^N) naive",
            spaceComplexity = "O(N) stack",
            generateSteps = { RecursionVisualizerGenerator.generateFibonacci(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "rec_binary_search",
            title = "Recursive Binary Search Stack Frames",
            categoryName = "Recursion",
            vizType = DsaVizType.RECURSION,
            defaultInput = "binarySearch(low=0, high=7)",
            supportsCustomInput = false,
            inputHelpText = "Interval bounds",
            timeComplexity = "O(log N)",
            spaceComplexity = "O(log N) stack",
            generateSteps = { RecursionVisualizerGenerator.generateRecursiveBinarySearch(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "rec_tower_of_hanoi",
            title = "Tower of Hanoi (3 Pegs, 3 Disks)",
            categoryName = "Recursion",
            vizType = DsaVizType.RECURSION,
            defaultInput = "3 Disks from A to C",
            supportsCustomInput = false,
            inputHelpText = "3 Disks",
            timeComplexity = "O(2^N)",
            spaceComplexity = "O(N) stack",
            generateSteps = { RecursionVisualizerGenerator.generateTowerOfHanoi(it) }
        ),

        // Category 10: Trees (7 subtopics)
        DsaVisualizerScenario(
            subtopicId = "tree_binary",
            title = "Binary Tree Anatomy & Height",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Root 4, Height 2",
            supportsCustomInput = false,
            inputHelpText = "Hierarchical nodes",
            timeComplexity = "O(N) traversal",
            spaceComplexity = "O(h) stack",
            generateSteps = { TreeVisualizerGenerator.generateBinaryTree(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "tree_bst",
            title = "Binary Search Tree Key Search",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Search Key = 5",
            supportsCustomInput = false,
            inputHelpText = "BST invariant search",
            timeComplexity = "O(log N) balanced",
            spaceComplexity = "O(h)",
            generateSteps = { TreeVisualizerGenerator.generateBst(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "tree_traversal",
            title = "Tree Traversal (Level-Order BFS)",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Queue level order",
            supportsCustomInput = false,
            inputHelpText = "Level by level",
            timeComplexity = "O(N)",
            spaceComplexity = "O(W) queue",
            generateSteps = { TreeVisualizerGenerator.generateTreeTraversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "tree_inorder",
            title = "Inorder Traversal (LNR -> Sorted Output)",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Left -> Node -> Right",
            supportsCustomInput = false,
            inputHelpText = "Sorted sequence",
            timeComplexity = "O(N)",
            spaceComplexity = "O(h) stack",
            generateSteps = { TreeVisualizerGenerator.generateInorderTraversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "tree_preorder",
            title = "Preorder Traversal (NLR -> Top-Down)",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Node -> Left -> Right",
            supportsCustomInput = false,
            inputHelpText = "Root first order",
            timeComplexity = "O(N)",
            spaceComplexity = "O(h) stack",
            generateSteps = { TreeVisualizerGenerator.generatePreorderTraversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "tree_postorder",
            title = "Postorder Traversal (LRN -> Bottom-Up)",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Left -> Right -> Node",
            supportsCustomInput = false,
            inputHelpText = "Bottom up order",
            timeComplexity = "O(N)",
            spaceComplexity = "O(h) stack",
            generateSteps = { TreeVisualizerGenerator.generatePostorderTraversal(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "tree_avl",
            title = "AVL Tree Right Rotation Balancing",
            categoryName = "Trees",
            vizType = DsaVizType.TREE,
            defaultInput = "Left-Left Imbalance (BF=+2)",
            supportsCustomInput = false,
            inputHelpText = "Single right rotation",
            timeComplexity = "O(1) rotation",
            spaceComplexity = "O(1)",
            generateSteps = { TreeVisualizerGenerator.generateAvlTree(it) }
        ),

        // Category 11: Graphs (6 subtopics)
        DsaVisualizerScenario(
            subtopicId = "graph_representation",
            title = "Graph Adjacency List Structure",
            categoryName = "Graphs",
            vizType = DsaVizType.GRAPH,
            defaultInput = "5 Vertices, 7 Edges",
            supportsCustomInput = false,
            inputHelpText = "Adjacency matrix & list",
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V + E)",
            generateSteps = { GraphVisualizerGenerator.generateGraphRepresentation(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "graph_bfs",
            title = "Breadth-First Search (Level Network Scan)",
            categoryName = "Graphs",
            vizType = DsaVizType.GRAPH,
            defaultInput = "Start at Source Vertex A",
            supportsCustomInput = false,
            inputHelpText = "FIFO queue scan",
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V) queue",
            generateSteps = { GraphVisualizerGenerator.generateBfs(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "graph_dfs",
            title = "Depth-First Search (Path Exploration)",
            categoryName = "Graphs",
            vizType = DsaVizType.GRAPH,
            defaultInput = "Start at Source Vertex A",
            supportsCustomInput = false,
            inputHelpText = "Backtracking search",
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V) call stack",
            generateSteps = { GraphVisualizerGenerator.generateDfs(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "graph_shortest_path",
            title = "Unweighted Shortest Path (BFS)",
            categoryName = "Graphs",
            vizType = DsaVizType.GRAPH,
            defaultInput = "Source A -> Target E",
            supportsCustomInput = false,
            inputHelpText = "Minimum edge hops",
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V)",
            generateSteps = { GraphVisualizerGenerator.generateShortestPath(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "graph_dijkstra",
            title = "Dijkstra's Weighted Shortest Paths",
            categoryName = "Graphs",
            vizType = DsaVizType.GRAPH,
            defaultInput = "Weighted graph from Source A",
            supportsCustomInput = false,
            inputHelpText = "Edge relaxation",
            timeComplexity = "O((V + E) log V)",
            spaceComplexity = "O(V)",
            generateSteps = { GraphVisualizerGenerator.generateDijkstra(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "graph_mst",
            title = "Minimum Spanning Tree (Kruskal's)",
            categoryName = "Graphs",
            vizType = DsaVizType.GRAPH,
            defaultInput = "Greedy Edge Selection",
            supportsCustomInput = false,
            inputHelpText = "Acyclic tree spanning V vertices",
            timeComplexity = "O(E log E)",
            spaceComplexity = "O(V)",
            generateSteps = { GraphVisualizerGenerator.generateMst(it) }
        ),

        // Category 12: Advanced Algorithms (6 subtopics)
        DsaVisualizerScenario(
            subtopicId = "adv_greedy",
            title = "Fractional Knapsack Greedy Selection",
            categoryName = "Advanced Algorithms",
            vizType = DsaVizType.GREEDY,
            defaultInput = "Items A, B, C (Cap=50kg)",
            supportsCustomInput = false,
            inputHelpText = "Value-to-weight ratio",
            timeComplexity = "O(N log N)",
            spaceComplexity = "O(1)",
            generateSteps = { AdvancedAlgorithmVisualizerGenerator.generateGreedy(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "adv_dp",
            title = "0/1 Knapsack DP Table Tabulation",
            categoryName = "Advanced Algorithms",
            vizType = DsaVizType.DP_TABLE,
            defaultInput = "3 Items, Capacity W=4",
            supportsCustomInput = false,
            inputHelpText = "State recurrence grid",
            timeComplexity = "O(N * W)",
            spaceComplexity = "O(N * W)",
            generateSteps = { AdvancedAlgorithmVisualizerGenerator.generateDp(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "adv_backtracking",
            title = "4-Queens Backtracking Constraint Board",
            categoryName = "Advanced Algorithms",
            vizType = DsaVizType.DP_TABLE,
            defaultInput = "4x4 Chessboard",
            supportsCustomInput = false,
            inputHelpText = "Non-attacking queen placement",
            timeComplexity = "O(N!)",
            spaceComplexity = "O(N)",
            generateSteps = { AdvancedAlgorithmVisualizerGenerator.generateBacktracking(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "adv_divide_conquer",
            title = "Divide and Conquer Merge Pipeline",
            categoryName = "Advanced Algorithms",
            vizType = DsaVizType.ARRAY,
            defaultInput = "14, 7, 22, 19, 31, 8",
            supportsCustomInput = true,
            inputHelpText = "Comma-separated numbers",
            timeComplexity = "O(N log N)",
            spaceComplexity = "O(N)",
            generateSteps = { AdvancedAlgorithmVisualizerGenerator.generateDivideAndConquer(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "adv_hashing",
            title = "Hash Table Mapping & Collision Chaining",
            categoryName = "Advanced Algorithms",
            vizType = DsaVizType.ARRAY,
            defaultInput = "h(k) = k % 7",
            supportsCustomInput = false,
            inputHelpText = "Bucket indexing",
            timeComplexity = "O(1) average lookup",
            spaceComplexity = "O(N)",
            generateSteps = { AdvancedAlgorithmVisualizerGenerator.generateHashing(it) }
        ),
        DsaVisualizerScenario(
            subtopicId = "adv_heap_pq",
            title = "Binary Heap Array Representation",
            categoryName = "Advanced Algorithms",
            vizType = DsaVizType.ARRAY,
            defaultInput = "90, 80, 70, 50, 40, 60, 30",
            supportsCustomInput = true,
            inputHelpText = "Heap numbers",
            timeComplexity = "O(log N) insert/extract",
            spaceComplexity = "O(1) in-place",
            generateSteps = { AdvancedAlgorithmVisualizerGenerator.generateHeapAndPriorityQueue(it) }
        )
    ).associateBy { it.subtopicId }

    fun getAllScenarios(): List<DsaVisualizerScenario> = scenarios.values.toList()

    private fun normalizeSlug(slugOrId: String): String {
        return when (slugOrId.trim().lowercase()) {
            "two-pointers" -> "array_two_pointers"
            "binary-search" -> "search_binary"
            "bubble-sort" -> "sort_bubble"
            else -> slugOrId.trim()
        }
    }

    fun getScenarioForSubtopic(subtopicId: String): DsaVisualizerScenario? {
        val normalized = normalizeSlug(subtopicId)
        return scenarios[normalized] ?: scenarios[subtopicId]
    }

    fun hasScenario(subtopicId: String): Boolean {
        val normalized = normalizeSlug(subtopicId)
        return scenarios.containsKey(normalized) || scenarios.containsKey(subtopicId)
    }

    fun generateStepsForSubtopic(subtopicId: String, customInput: String? = null): List<DsaVisualizerStep> {
        val scenario = getScenarioForSubtopic(subtopicId)
        return scenario?.generateSteps?.invoke(customInput) ?: emptyList()
    }
}
