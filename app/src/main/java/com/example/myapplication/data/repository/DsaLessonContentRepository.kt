package com.example.myapplication.data.repository

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.DsaLessonOutline
import com.example.myapplication.data.model.catalogue.DsaSubtopic
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType
import com.example.myapplication.data.model.catalogue.TopicLessonProgress

/**
 * Centralized repository for structured DSA lesson content.
 *
 * Authored lessons are registered topic-by-topic using stable string IDs
 * matching DsaTopicCatalogueRepository. Topics without authored content yet
 * cleanly return null so the UI displays a dedicated Coming Soon screen.
 *
 * Ready for future PHP/MySQL backend syncing by populating from API responses
 * into the same DsaLessonContent model without changing UI code.
 */
object DsaLessonContentRepository {

    const val LESSON_INTRO_DS_01_ID = "prog_intro_ds_l1"

    private val implementedLessons: List<DsaLessonContent> = listOf(
        // ============================================================
        // Category: Programming Basics | Topic: Introduction to Data Structures
        // Lesson 01: Core Theory & Invariants
        // ============================================================
        DsaLessonContent(
            lessonId = LESSON_INTRO_DS_01_ID,
            subtopicId = "prog_intro_ds",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Introduction to Data Structures",
            subtitle = "How computers organize data in memory and why structure determines speed",
            estimatedMinutes = 8,
            xpReward = 40,
            backendLessonId = null, // Offline-first catalogue lesson; server XP sync activates when mapped in MySQL
            backendTopicId = null,
            sections = listOf(
                LessonSection(
                    id = "intro_what_is_ds",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Data Structure?",
                    body = "A data structure is a specialized way of organizing, storing, and managing data in a computer's memory so that it can be accessed, searched, and updated efficiently.\n\nWhen you write a small program with just 3 variables, storing data is easy. However, real applications handle thousands or millions of pieces of data — like student records, messages, or high scores. A data structure defines both how values are arranged in memory and what rules (invariants) govern how you interact with them.",
                    bulletPoints = listOf(
                        "Raw Data: Unorganized facts like a number (95), a name (\"Aarav\"), or a timestamp.",
                        "Data Structure: A deliberate layout (like an Array, Stack, or Hash Table) that stores data with predictable rules.",
                        "Abstract Data Type (ADT): The conceptual interface — what operations exist (e.g., push, pop, search) without worrying about low-level memory wiring."
                    ),
                    highlightNote = "Core Invariant: Every data structure makes a promise about how data stays organized — and algorithms rely on that promise to run fast."
                ),
                LessonSection(
                    id = "why_ds_needed",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Why Do We Need Data Structures?",
                    body = "Imagine trying to find one specific student's exam paper in a giant pile of 50,000 unorganized sheets scattered on the floor. You would have to check them one by one. Now imagine those same papers sorted alphabetically in labeled filing cabinets — you find the exact sheet in seconds.\n\nComputers face the exact same problem. Even though modern CPUs are fast, poor data organization causes apps to freeze, lag, or run out of memory as users grow.",
                    bulletPoints = listOf(
                        "Fast Access & Search: Locate specific records in milliseconds instead of scanning everything.",
                        "Efficient Updates: Insert new entries or delete old ones without rebuilding all stored data.",
                        "Memory Efficiency: Avoid wasting RAM by choosing compact representations suited to the task.",
                        "Scalability: Keep performance smooth whether an app has 100 users or 100 million users."
                    ),
                    highlightNote = "Every data structure optimizes four core operations differently: Access, Search, Insertion, and Deletion."
                ),
                LessonSection(
                    id = "memory_organization",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "How Data Is Organized & Stored in Memory",
                    body = "Your computer's RAM is essentially a giant numbered street of memory cells, where each cell has a unique address. Data structures organize values in RAM using two fundamental physical layouts — and two logical shapes.",
                    bulletPoints = listOf(
                        "Contiguous Storage (e.g., Arrays): Elements sit side-by-side in consecutive memory addresses. The CPU computes any element's exact location instantly using: address = base + (index × element_size).",
                        "Non-Contiguous / Linked Storage (e.g., Linked Lists, Trees, Graphs): Elements (nodes) can live anywhere in RAM. Each node stores both its value and a reference (pointer) to the next node's address.",
                        "Linear Structures: Data is arranged sequentially one after another — Arrays, Linked Lists, Stacks, and Queues.",
                        "Non-Linear Structures: Data branches into hierarchies or networks — Trees, Heaps, Graphs, and Hash Tables."
                    ),
                    highlightNote = "Contiguous memory gives instant index lookup (O(1)) and great CPU cache speed, while linked memory allows flexible growth without shifting elements."
                ),
                LessonSection(
                    id = "common_ds_and_uses",
                    type = LessonSectionType.USE_CASES,
                    title = "Common Data Structures & Where They Are Used",
                    body = "Almost every app on your phone and every backend server on the internet is powered by a small set of foundational data structures. Knowing where each one shines is the first step to thinking like a software engineer.",
                    bulletPoints = listOf(
                        "Arrays: Storing pixel grids in photos, game leaderboards, audio buffers, and fixed lookup tables.",
                        "Linked Lists: Music playlists (next/previous song), browser Back/Forward history, and OS task lists.",
                        "Stacks (Last-In, First-Out): Undo/Redo (Ctrl+Z) in editors, function call stacks in Kotlin/Java, and matching brackets in compilers.",
                        "Queues (First-In, First-Out): Printer spoolers, customer support ticket lines, and network packet buffers.",
                        "Hash Tables (HashMap / Dictionary): Instant user login lookup by email, database indexing, and spell checkers.",
                        "Trees: File system folders on your laptop, HTML DOM in browsers, and autocomplete search bars.",
                        "Graphs: Google Maps shortest-route navigation, social media friend networks, and flight connections."
                    )
                ),
                LessonSection(
                    id = "real_world_examples",
                    type = LessonSectionType.REAL_WORLD_EXAMPLES,
                    title = "Real-World Examples You Already Know",
                    body = "Data structures aren't abstract math inventions — they mirror systems humans invented centuries ago to organize physical objects efficiently.",
                    bulletPoints = listOf(
                        "Numbered Bookshelf (Array): Every slot is numbered 0, 1, 2… Grabbing book #42 takes one step, but squeezing a new book into slot #5 forces you to slide every book after it down.",
                        "Treasure Hunt Clues (Linked List): Clue #1 tells you where Clue #2 is hidden, which points to Clue #3. You can add a new stop by rewriting just one clue, without moving anything else.",
                        "Stack of Cafeteria Trays (Stack): You place a clean tray on top and students always take the top tray first (LIFO).",
                        "Movie Theater Ticket Line (Queue): The first person who joins the line is the first person to buy a ticket (FIFO).",
                        "College Locker Numbering (Hash Table): Instead of opening every locker to find yours, your student ID maps straight to your exact locker number."
                    )
                ),
                LessonSection(
                    id = "performance_impact",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "How Choosing a Data Structure Affects Performance",
                    body = "Suppose your college builds a portal with 1,000,000 student records and needs to look up a student by their Roll Number. The exact same task takes drastically different amounts of work depending solely on which data structure you choose:",
                    bulletPoints = listOf(
                        "Unsorted Array / Linked List: Must check records one by one — up to 1,000,000 comparisons in the worst case — O(N).",
                        "Sorted Array (with Binary Search): Cuts the remaining search space in half each step — at most 20 comparisons — O(log N).",
                        "Hash Table (HashMap by Roll Number): Computes a hash directly to the bucket — roughly 1 step on average — O(1)."
                    ),
                    highlightNote = "There is no single 'best' data structure. Hash Tables give O(1) lookup but don't maintain sorted order; Sorted Arrays support fast range queries and Binary Search, but inserting in the middle takes O(N) time."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Example 1: Linear List Search vs. HashMap Direct Lookup",
                    language = "KOTLIN",
                    code = """// Storing student grades in two different data structures
data class Student(val rollNo: Int, val name: String, val gpa: Double)

val studentList = listOf(
    Student(101, "Ananya", 9.1),
    Student(102, "Rohan", 8.6),
    Student(103, "Meera", 9.4)
)

// 1. Searching an Unsorted List -> O(N) linear scan
fun findInList(list: List<Student>, targetRoll: Int): Student? {
    for (student in list) {
        if (student.rollNo == targetRoll) return student
    }
    return null
}

// 2. Searching a HashMap -> O(1) average direct lookup
val studentMap = studentList.associateBy { it.rollNo }
val found = studentMap[103] // Direct jump to Meera!""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(
                            stepNumber = 1,
                            lineReference = "Lines 2–8",
                            explanation = "We define a Student record and store 3 students sequentially inside a standard List."
                        ),
                        CodeStepExplanation(
                            stepNumber = 2,
                            lineReference = "Lines 11–16",
                            explanation = "findInList() loops through every element from start to end. With 1,000,000 students, finding the last student requires checking all 1,000,000 entries — O(N) time."
                        ),
                        CodeStepExplanation(
                            stepNumber = 3,
                            lineReference = "Line 19",
                            explanation = "associateBy { it.rollNo } organizes the same students into a HashMap indexed by roll number."
                        ),
                        CodeStepExplanation(
                            stepNumber = 4,
                            lineReference = "Line 20",
                            explanation = "studentMap[103] hashes the key 103 and jumps straight to Meera's record in constant O(1) average time — no loop needed!"
                        )
                    )
                ),
                LessonCodeBlock(
                    title = "Example 2: Contiguous Array Indexing vs. Linked Node Chain",
                    language = "KOTLIN",
                    code = """// Contiguous memory: direct O(1) index access
val scores = intArrayOf(82, 91, 95, 78)
val thirdScore = scores[2] // Jumps directly to index 2 -> 95

// Non-contiguous memory: nodes connected by references
class Node(val score: Int, var next: Node? = null)

val head = Node(82, Node(91, Node(95, Node(78))))
var current: Node? = head
// Must follow links step-by-step to reach index 2
repeat(2) { current = current?.next }
val linkedThirdScore = current?.score // 95""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(
                            stepNumber = 1,
                            lineReference = "Lines 2–3",
                            explanation = "intArrayOf allocates 4 integers side-by-side in RAM. Accessing scores[2] uses simple offset math in 1 step — O(1)."
                        ),
                        CodeStepExplanation(
                            stepNumber = 2,
                            lineReference = "Lines 6–8",
                            explanation = "Each Node holds a score plus a 'next' pointer to the following node. Nodes can sit anywhere in memory."
                        ),
                        CodeStepExplanation(
                            stepNumber = 3,
                            lineReference = "Lines 9–12",
                            explanation = "To reach the 3rd score in a linked chain, we must start at head and hop through 'next' references one by one — O(N) access time."
                        )
                    )
                )
            ),
            keyTakeaways = listOf(
                "A data structure defines how data is organized in memory and which operations it performs efficiently.",
                "Contiguous structures (Arrays) excel at O(1) direct indexing; linked structures (Linked Lists, Trees) excel at flexible pointer-based updates.",
                "Linear structures (Arrays, Lists, Stacks, Queues) store items sequentially; non-linear structures (Trees, Graphs, Hash Tables) represent hierarchies, networks, or key-value mappings.",
                "Choosing the right data structure can shrink work from 1,000,000 steps O(N) down to 20 steps O(log N) or 1 step O(1).",
                "Always match your data structure to your program's most frequent operations (Access, Search, Insert, Delete) and invariants."
            )
        )
    )

    private val lessonsById: Map<String, DsaLessonContent> =
        implementedLessons.associateBy { it.lessonId }

    private val lessonsBySubtopic: Map<String, List<DsaLessonContent>> =
        implementedLessons.groupBy { it.subtopicId }
            .mapValues { (_, list) -> list.sortedBy { it.lessonNumber } }

    /**
     * Returns full structured lesson content if implemented, or null if coming soon.
     */
    fun getLessonContent(lessonId: String): DsaLessonContent? = lessonsById[lessonId]

    /**
     * Returns all authored structured lessons for a subtopic.
     */
    fun getImplementedLessonsForSubtopic(subtopicId: String): List<DsaLessonContent> =
        lessonsBySubtopic[subtopicId] ?: emptyList()

    /**
     * Returns true strictly when full structured educational content exists for this lesson.
     */
    fun hasImplementedContent(lessonId: String): Boolean = lessonsById.containsKey(lessonId)

    /**
     * Builds the ordered lesson outline for any subtopic in the 65-topic catalogue.
     * Uses authored DsaLessonContent when available and cleanly marks unauthored modules
     * with hasContent = false so the UI displays a dedicated Coming Soon screen.
     */
    fun getLessonOutlinesForSubtopic(subtopic: DsaSubtopic): List<DsaLessonOutline> {
        val outlines = mutableListOf<DsaLessonOutline>()
        val authoredTheoryLessons = getImplementedLessonsForSubtopic(subtopic.id)

        if (authoredTheoryLessons.isNotEmpty()) {
            authoredTheoryLessons.forEach { lesson ->
                outlines.add(
                    DsaLessonOutline(
                        lessonId = lesson.lessonId,
                        subtopicId = subtopic.id,
                        lessonNumber = lesson.lessonNumber,
                        title = lesson.title,
                        duration = "${lesson.estimatedMinutes} min",
                        xpReward = lesson.xpReward,
                        lessonType = "THEORY",
                        hasContent = true,
                        backendLessonId = lesson.backendLessonId
                    )
                )
            }
        } else {
            outlines.add(
                DsaLessonOutline(
                    lessonId = "${subtopic.id}_l1",
                    subtopicId = subtopic.id,
                    lessonNumber = 1,
                    title = "Core Theory & Invariants – ${subtopic.name}",
                    duration = "${(subtopic.estimatedMinutes * 0.4).toInt().coerceAtLeast(3)} min",
                    xpReward = subtopic.xpReward / 2,
                    lessonType = "THEORY",
                    hasContent = false
                )
            )
        }

        val nextNumberAfterTheory = outlines.size + 1

        // Module 2: Interactive Visualizer Lab
        val canViz = subtopic.hasVisualization && !subtopic.visualizerSlug.isNullOrEmpty()
        outlines.add(
            DsaLessonOutline(
                lessonId = "${subtopic.id}_viz",
                subtopicId = subtopic.id,
                lessonNumber = nextNumberAfterTheory,
                title = if (canViz) "Step Through Visualizer Lab" else "Interactive Visualizer Lab",
                duration = "${(subtopic.estimatedMinutes * 0.6).toInt().coerceAtLeast(5)} min",
                xpReward = subtopic.xpReward / 2,
                lessonType = "VISUALIZER",
                hasContent = canViz,
                visualizerSlug = subtopic.visualizerSlug
            )
        )

        // Module 3: Topic Mastery Quiz
        val canQuiz = subtopic.hasQuiz && subtopic.quizLessonId != null
        outlines.add(
            DsaLessonOutline(
                lessonId = "${subtopic.id}_quiz",
                subtopicId = subtopic.id,
                lessonNumber = nextNumberAfterTheory + 1,
                title = if (canQuiz) "Topic Mastery Quiz & Test" else "Topic Mastery Quiz",
                duration = "5 min",
                xpReward = 50,
                lessonType = "QUIZ",
                hasContent = canQuiz,
                quizLessonId = subtopic.quizLessonId
            )
        )

        return outlines
    }

    fun getLessonOutlineById(subtopic: DsaSubtopic, lessonId: String): DsaLessonOutline? {
        return getLessonOutlinesForSubtopic(subtopic).firstOrNull { it.lessonId == lessonId }
    }

    fun getPreviousLessonOutline(subtopic: DsaSubtopic, currentLessonNumber: Int): DsaLessonOutline? {
        return getLessonOutlinesForSubtopic(subtopic)
            .firstOrNull { it.lessonNumber == currentLessonNumber - 1 }
    }

    fun getNextLessonOutline(subtopic: DsaSubtopic, currentLessonNumber: Int): DsaLessonOutline? {
        return getLessonOutlinesForSubtopic(subtopic)
            .firstOrNull { it.lessonNumber == currentLessonNumber + 1 }
    }

    fun calculateTopicProgress(
        subtopic: DsaSubtopic,
        completedLessonIds: Set<String>
    ): TopicLessonProgress {
        val outlines = getLessonOutlinesForSubtopic(subtopic)
        val total = outlines.size
        val completed = outlines.count { completedLessonIds.contains(it.lessonId) }
        val percentage = if (total > 0) ((completed * 100) / total) else 0
        return TopicLessonProgress(
            completedCount = completed,
            totalCount = total,
            progressPercentage = percentage
        )
    }
}
