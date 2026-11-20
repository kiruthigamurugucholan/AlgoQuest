package com.example.myapplication.data.repository

/**
 * Data model representing a Chapter in the Adventure Roadmap (7 Chapters).
 */
data class AdventureChapter(
    val id: Int,
    val chapterNumber: Int,
    val chapterLabel: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val primarySubtopicId: String,
    val primaryVisualizerSlug: String,
    val totalXp: Int,
    val iconType: String,
    val lessons: List<AdventureLesson>
)

/**
 * Data model representing a Lesson within an Adventure Roadmap Chapter.
 */
data class AdventureLesson(
    val id: Int,
    val chapterId: Int,
    val title: String,
    val duration: String,
    val xp: String,
    val xpReward: Int,
    val lessonType: String, // "THEORY", "VISUALIZER", "QUIZ"
    val subtopicId: String,
    val visualizerSlug: String?,
    val catalogueLessonId: String = "${subtopicId}_l1",
    val quizLessonId: Int? = null,
    val isQuiz: Boolean = (lessonType == "QUIZ")
)

/**
 * Centralized, data-driven Adventure Roadmap Repository.
 *
 * Maps Adventure Chapters (1-7) and their specific Lessons to DSA Catalogue Subtopics
 * and Visualization Slugs, guaranteeing no lesson ever accidentally defaults to Binary Search.
 */
object AdventureRoadmapRepository {

    val chapters: List<AdventureChapter> = listOf(
        // Chapter 01: Arrays & Memory
        AdventureChapter(
            id = 1,
            chapterNumber = 1,
            chapterLabel = "Chapter 01",
            title = "Arrays & Memory",
            subtitle = "Continuous buffers, indexing, and memory layout",
            description = "Master the fundamentals of contiguous memory, cache locality, and dynamic resizing in modern computing.",
            primarySubtopicId = "array_traversal",
            primaryVisualizerSlug = "array_traversal",
            totalXp = 400,
            iconType = "array",
            lessons = listOf(
                AdventureLesson(
                    id = 101,
                    chapterId = 1,
                    title = "Memory Allocation & Contiguous Buffers",
                    duration = "8 min",
                    xp = "+80 XP",
                    xpReward = 80,
                    lessonType = "THEORY",
                    subtopicId = "array_traversal",
                    visualizerSlug = "array_traversal",
                    catalogueLessonId = "array_traversal_l1"
                ),
                AdventureLesson(
                    id = 102,
                    chapterId = 1,
                    title = "Array Traversal & Scanning Mechanics",
                    duration = "10 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "THEORY",
                    subtopicId = "array_traversal",
                    visualizerSlug = "array_traversal",
                    catalogueLessonId = "array_traversal_l1"
                ),
                AdventureLesson(
                    id = 103,
                    chapterId = 1,
                    title = "Interactive Visualizer: Array Traversal Lab",
                    duration = "12 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "VISUALIZER",
                    subtopicId = "array_traversal",
                    visualizerSlug = "array_traversal",
                    catalogueLessonId = "array_traversal_viz"
                ),
                AdventureLesson(
                    id = 104,
                    chapterId = 1,
                    title = "Array Operations & Memory Mastery Test",
                    duration = "10 min",
                    xp = "+120 XP",
                    xpReward = 120,
                    lessonType = "QUIZ",
                    subtopicId = "array_operations",
                    visualizerSlug = "array_operations",
                    catalogueLessonId = "array_operations_l1",
                    quizLessonId = 1
                )
            )
        ),

        // Chapter 02: Two Pointers & Sliding
        AdventureChapter(
            id = 2,
            chapterNumber = 2,
            chapterLabel = "Chapter 02",
            title = "Two Pointers & Sliding",
            subtitle = "Optimal array traversals and windowing",
            description = "Transform brute-force O(N^2) searches into optimal O(N) single-pass scans using directional pointers.",
            primarySubtopicId = "array_two_pointers",
            primaryVisualizerSlug = "two-pointers",
            totalXp = 500,
            iconType = "pointer",
            lessons = listOf(
                AdventureLesson(
                    id = 201,
                    chapterId = 2,
                    title = "Opposite-Direction Pointers (Two Sum Sorted)",
                    duration = "10 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "THEORY",
                    subtopicId = "array_two_pointers",
                    visualizerSlug = "two-pointers",
                    catalogueLessonId = "array_two_pointers_l1"
                ),
                AdventureLesson(
                    id = 202,
                    chapterId = 2,
                    title = "Interactive Visualizer: Two Pointers Lab",
                    duration = "12 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "VISUALIZER",
                    subtopicId = "array_two_pointers",
                    visualizerSlug = "two-pointers",
                    catalogueLessonId = "array_two_pointers_viz"
                ),
                AdventureLesson(
                    id = 203,
                    chapterId = 2,
                    title = "Equi-Directional Pointers & Sliding Window",
                    duration = "12 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "THEORY",
                    subtopicId = "array_sliding_window",
                    visualizerSlug = "array_sliding_window",
                    catalogueLessonId = "array_sliding_window_l1"
                ),
                AdventureLesson(
                    id = 204,
                    chapterId = 2,
                    title = "Interactive Visualizer: Sliding Window Scan",
                    duration = "14 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "VISUALIZER",
                    subtopicId = "array_sliding_window",
                    visualizerSlug = "array_sliding_window",
                    catalogueLessonId = "array_sliding_window_viz"
                ),
                AdventureLesson(
                    id = 205,
                    chapterId = 2,
                    title = "Two Pointers & Window Mastery Challenge",
                    duration = "10 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "QUIZ",
                    subtopicId = "array_two_pointers",
                    visualizerSlug = "two-pointers",
                    catalogueLessonId = "array_two_pointers_l1",
                    quizLessonId = 1
                )
            )
        ),

        // Chapter 03: Searching Algorithms
        AdventureChapter(
            id = 3,
            chapterNumber = 3,
            chapterLabel = "Chapter 03",
            title = "Searching Algorithms",
            subtitle = "Linear scan vs binary search cuts the world in half",
            description = "Learn how cutting search boundaries in half reduces linear lookups into ultra-fast logarithmic operations.",
            primarySubtopicId = "search_binary",
            primaryVisualizerSlug = "binary-search",
            totalXp = 900,
            iconType = "search",
            lessons = listOf(
                AdventureLesson(
                    id = 1,
                    chapterId = 3,
                    title = "Linear vs Binary Search: The 1M Step Difference",
                    duration = "8 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "THEORY",
                    subtopicId = "search_linear",
                    visualizerSlug = "search_linear",
                    catalogueLessonId = "search_linear_l1"
                ),
                AdventureLesson(
                    id = 2,
                    chapterId = 3,
                    title = "Binary Search Core Implementation & Invariants",
                    duration = "12 min",
                    xp = "+150 XP",
                    xpReward = 150,
                    lessonType = "THEORY",
                    subtopicId = "search_binary",
                    visualizerSlug = "binary-search",
                    catalogueLessonId = "search_binary_l1"
                ),
                AdventureLesson(
                    id = 3,
                    chapterId = 3,
                    title = "Interactive Visualizer: Step Through Binary Search",
                    duration = "15 min",
                    xp = "+150 XP",
                    xpReward = 150,
                    lessonType = "VISUALIZER",
                    subtopicId = "search_binary",
                    visualizerSlug = "binary-search",
                    catalogueLessonId = "search_binary_viz"
                ),
                AdventureLesson(
                    id = 4,
                    chapterId = 3,
                    title = "Search in Rotated Sorted Array",
                    duration = "18 min",
                    xp = "+200 XP",
                    xpReward = 200,
                    lessonType = "THEORY",
                    subtopicId = "search_binary",
                    visualizerSlug = "binary-search",
                    catalogueLessonId = "search_binary_l1"
                ),
                AdventureLesson(
                    id = 5,
                    chapterId = 3,
                    title = "Chapter 03 Mastery Test & Challenge",
                    duration = "10 min",
                    xp = "+300 XP",
                    xpReward = 300,
                    lessonType = "QUIZ",
                    subtopicId = "search_binary",
                    visualizerSlug = "binary-search",
                    catalogueLessonId = "search_binary_l1",
                    quizLessonId = 1
                )
            )
        ),

        // Chapter 04: Sorting & Divide/Conquer
        AdventureChapter(
            id = 4,
            chapterNumber = 4,
            chapterLabel = "Chapter 04",
            title = "Sorting & Divide/Conquer",
            subtitle = "Divide, sort subproblems, and merge in O(N log N)",
            description = "Understand comparison-based sorting barriers and discover why O(N log N) is the theoretical limit.",
            primarySubtopicId = "sort_bubble",
            primaryVisualizerSlug = "bubble-sort",
            totalXp = 650,
            iconType = "sort",
            lessons = listOf(
                AdventureLesson(
                    id = 401,
                    chapterId = 4,
                    title = "Bubble Sort & Adjacent Swaps",
                    duration = "10 min",
                    xp = "+100 XP",
                    xpReward = 100,
                    lessonType = "THEORY",
                    subtopicId = "sort_bubble",
                    visualizerSlug = "bubble-sort",
                    catalogueLessonId = "sort_bubble_l1"
                ),
                AdventureLesson(
                    id = 402,
                    chapterId = 4,
                    title = "Interactive Visualizer: Bubble Sort Lab",
                    duration = "12 min",
                    xp = "+120 XP",
                    xpReward = 120,
                    lessonType = "VISUALIZER",
                    subtopicId = "sort_bubble",
                    visualizerSlug = "bubble-sort",
                    catalogueLessonId = "sort_bubble_viz"
                ),
                AdventureLesson(
                    id = 403,
                    chapterId = 4,
                    title = "Merge Sort & Divide-and-Conquer Recurrence",
                    duration = "14 min",
                    xp = "+140 XP",
                    xpReward = 140,
                    lessonType = "THEORY",
                    subtopicId = "sort_merge",
                    visualizerSlug = "sort_merge",
                    catalogueLessonId = "sort_merge_l1"
                ),
                AdventureLesson(
                    id = 404,
                    chapterId = 4,
                    title = "Interactive Visualizer: Quick Sort Partitions",
                    duration = "16 min",
                    xp = "+140 XP",
                    xpReward = 140,
                    lessonType = "VISUALIZER",
                    subtopicId = "sort_quick",
                    visualizerSlug = "sort_quick",
                    catalogueLessonId = "sort_quick_viz"
                ),
                AdventureLesson(
                    id = 405,
                    chapterId = 4,
                    title = "Sorting Algorithms Mastery Challenge",
                    duration = "10 min",
                    xp = "+150 XP",
                    xpReward = 150,
                    lessonType = "QUIZ",
                    subtopicId = "sort_bubble",
                    visualizerSlug = "bubble-sort",
                    catalogueLessonId = "sort_bubble_l1",
                    quizLessonId = 1
                )
            )
        ),

        // Chapter 05: Linked Lists & Pointers
        AdventureChapter(
            id = 5,
            chapterNumber = 5,
            chapterLabel = "Chapter 05",
            title = "Linked Lists & Pointers",
            subtitle = "Nodes, references, cycle detection, and reversal",
            description = "Build dynamic non-contiguous collections, detect fast-and-slow runner cycles, and invert pointer directions.",
            primarySubtopicId = "ll_singly",
            primaryVisualizerSlug = "ll_singly",
            totalXp = 700,
            iconType = "link",
            lessons = listOf(
                AdventureLesson(
                    id = 501,
                    chapterId = 5,
                    title = "Singly Linked List Node References & Traversal",
                    duration = "10 min",
                    xp = "+120 XP",
                    xpReward = 120,
                    lessonType = "THEORY",
                    subtopicId = "ll_singly",
                    visualizerSlug = "ll_singly",
                    catalogueLessonId = "ll_singly_l1"
                ),
                AdventureLesson(
                    id = 502,
                    chapterId = 5,
                    title = "Interactive Visualizer: Singly Linked List Lab",
                    duration = "12 min",
                    xp = "+130 XP",
                    xpReward = 130,
                    lessonType = "VISUALIZER",
                    subtopicId = "ll_singly",
                    visualizerSlug = "ll_singly",
                    catalogueLessonId = "ll_singly_viz"
                ),
                AdventureLesson(
                    id = 503,
                    chapterId = 5,
                    title = "Node Insertion & Deletion Pointer Rewiring",
                    duration = "12 min",
                    xp = "+140 XP",
                    xpReward = 140,
                    lessonType = "THEORY",
                    subtopicId = "ll_insertion_deletion",
                    visualizerSlug = "ll_insertion_deletion",
                    catalogueLessonId = "ll_insertion_deletion_l1"
                ),
                AdventureLesson(
                    id = 504,
                    chapterId = 5,
                    title = "Interactive Visualizer: List Inversion & Reversal",
                    duration = "14 min",
                    xp = "+150 XP",
                    xpReward = 150,
                    lessonType = "VISUALIZER",
                    subtopicId = "ll_reversal",
                    visualizerSlug = "ll_reversal",
                    catalogueLessonId = "ll_reversal_viz"
                ),
                AdventureLesson(
                    id = 505,
                    chapterId = 5,
                    title = "Linked Lists Mastery Test & Challenge",
                    duration = "10 min",
                    xp = "+160 XP",
                    xpReward = 160,
                    lessonType = "QUIZ",
                    subtopicId = "ll_singly",
                    visualizerSlug = "ll_singly",
                    catalogueLessonId = "ll_singly_l1",
                    quizLessonId = 1
                )
            )
        ),

        // Chapter 06: Trees & Binary Trees
        AdventureChapter(
            id = 6,
            chapterNumber = 6,
            chapterLabel = "Chapter 06",
            title = "Trees & Binary Trees",
            subtitle = "Hierarchical data, traversals, and BST properties",
            description = "Climb hierarchical structures, master recursion, and inspect BST invariants and tree traversals.",
            primarySubtopicId = "tree_bst",
            primaryVisualizerSlug = "tree_bst",
            totalXp = 800,
            iconType = "tree",
            lessons = listOf(
                AdventureLesson(
                    id = 601,
                    chapterId = 6,
                    title = "Binary Tree Anatomy, Depth & Height",
                    duration = "10 min",
                    xp = "+140 XP",
                    xpReward = 140,
                    lessonType = "THEORY",
                    subtopicId = "tree_binary",
                    visualizerSlug = "tree_binary",
                    catalogueLessonId = "tree_binary_l1"
                ),
                AdventureLesson(
                    id = 602,
                    chapterId = 6,
                    title = "Interactive Visualizer: Binary Search Tree Lab",
                    duration = "15 min",
                    xp = "+160 XP",
                    xpReward = 160,
                    lessonType = "VISUALIZER",
                    subtopicId = "tree_bst",
                    visualizerSlug = "tree_bst",
                    catalogueLessonId = "tree_bst_viz"
                ),
                AdventureLesson(
                    id = 603,
                    chapterId = 6,
                    title = "Depth-First Tree Traversals (Inorder, Preorder, Postorder)",
                    duration = "12 min",
                    xp = "+150 XP",
                    xpReward = 150,
                    lessonType = "THEORY",
                    subtopicId = "tree_inorder",
                    visualizerSlug = "tree_inorder",
                    catalogueLessonId = "tree_inorder_l1"
                ),
                AdventureLesson(
                    id = 604,
                    chapterId = 6,
                    title = "Interactive Visualizer: Tree Traversal Scan",
                    duration = "14 min",
                    xp = "+160 XP",
                    xpReward = 160,
                    lessonType = "VISUALIZER",
                    subtopicId = "tree_inorder",
                    visualizerSlug = "tree_inorder",
                    catalogueLessonId = "tree_inorder_viz"
                ),
                AdventureLesson(
                    id = 605,
                    chapterId = 6,
                    title = "Binary Search Trees Mastery Challenge",
                    duration = "10 min",
                    xp = "+190 XP",
                    xpReward = 190,
                    lessonType = "QUIZ",
                    subtopicId = "tree_bst",
                    visualizerSlug = "tree_bst",
                    catalogueLessonId = "tree_bst_l1",
                    quizLessonId = 1
                )
            )
        ),

        // Chapter 07: Graphs & BFS / DFS
        AdventureChapter(
            id = 7,
            chapterNumber = 7,
            chapterLabel = "Chapter 07",
            title = "Graphs & BFS / DFS",
            subtitle = "Vertices, edges, queues, stacks, and pathfinding",
            description = "Model complex real-world relationships and traverse frontiers using Depth-First and Breadth-First Search.",
            primarySubtopicId = "graph_bfs",
            primaryVisualizerSlug = "graph_bfs",
            totalXp = 1000,
            iconType = "graph",
            lessons = listOf(
                AdventureLesson(
                    id = 701,
                    chapterId = 7,
                    title = "Graph Representations: Adjacency Lists & Matrices",
                    duration = "12 min",
                    xp = "+180 XP",
                    xpReward = 180,
                    lessonType = "THEORY",
                    subtopicId = "graph_representation",
                    visualizerSlug = "graph_representation",
                    catalogueLessonId = "graph_representation_l1"
                ),
                AdventureLesson(
                    id = 702,
                    chapterId = 7,
                    title = "Interactive Visualizer: Breadth-First Search (BFS)",
                    duration = "16 min",
                    xp = "+200 XP",
                    xpReward = 200,
                    lessonType = "VISUALIZER",
                    subtopicId = "graph_bfs",
                    visualizerSlug = "graph_bfs",
                    catalogueLessonId = "graph_bfs_viz"
                ),
                AdventureLesson(
                    id = 703,
                    chapterId = 7,
                    title = "Depth-First Search (DFS) & Cycle Detection",
                    duration = "14 min",
                    xp = "+190 XP",
                    xpReward = 190,
                    lessonType = "THEORY",
                    subtopicId = "graph_dfs",
                    visualizerSlug = "graph_dfs",
                    catalogueLessonId = "graph_dfs_l1"
                ),
                AdventureLesson(
                    id = 704,
                    chapterId = 7,
                    title = "Interactive Visualizer: Dijkstra's Shortest Path",
                    duration = "18 min",
                    xp = "+210 XP",
                    xpReward = 210,
                    lessonType = "VISUALIZER",
                    subtopicId = "graph_dijkstra",
                    visualizerSlug = "graph_dijkstra",
                    catalogueLessonId = "graph_dijkstra_viz"
                ),
                AdventureLesson(
                    id = 705,
                    chapterId = 7,
                    title = "Graphs & Network Traversal Mastery Test",
                    duration = "12 min",
                    xp = "+220 XP",
                    xpReward = 220,
                    lessonType = "QUIZ",
                    subtopicId = "graph_bfs",
                    visualizerSlug = "graph_bfs",
                    catalogueLessonId = "graph_bfs_l1",
                    quizLessonId = 1
                )
            )
        )
    )

    private val chaptersById: Map<Int, AdventureChapter> = chapters.associateBy { it.id }

    fun getAllChapters(): List<AdventureChapter> = chapters

    fun getChapterById(chapterId: Int): AdventureChapter? = chaptersById[chapterId]

    fun getLessonsForChapter(chapterId: Int): List<AdventureLesson> {
        return chaptersById[chapterId]?.lessons ?: emptyList()
    }

    fun getLessonById(chapterId: Int, lessonId: Int): AdventureLesson? {
        return getLessonsForChapter(chapterId).firstOrNull { it.id == lessonId }
    }

    fun getPrimaryVisualizerSlugForChapter(chapterId: Int): String {
        return chaptersById[chapterId]?.primaryVisualizerSlug ?: "array_traversal"
    }

    fun getPrimarySubtopicIdForChapter(chapterId: Int): String {
        return chaptersById[chapterId]?.primarySubtopicId ?: "array_traversal"
    }

    fun resolveSubtopicForLesson(chapterId: Int, lessonId: Int): String {
        val lesson = getLessonById(chapterId, lessonId)
        return lesson?.subtopicId ?: getPrimarySubtopicIdForChapter(chapterId)
    }

    fun resolveVisualizerSlugForLesson(chapterId: Int, lessonId: Int): String {
        val lesson = getLessonById(chapterId, lessonId)
        return lesson?.visualizerSlug ?: getPrimaryVisualizerSlugForChapter(chapterId)
    }

    fun resolveCatalogueLessonId(chapterId: Int, lessonId: Int): String {
        val lesson = getLessonById(chapterId, lessonId)
        return lesson?.catalogueLessonId ?: "${getPrimarySubtopicIdForChapter(chapterId)}_l1"
    }
}
