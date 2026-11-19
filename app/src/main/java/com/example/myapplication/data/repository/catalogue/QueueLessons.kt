package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object QueueLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Queue & FIFO Principle
        DsaLessonContent(
            lessonId = "queue_simple_l1",
            subtopicId = "queue_simple",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Queue & FIFO Principle",
            subtitle = "First-In, First-Out sequencing for sequential processing and buffer management",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) enqueue, dequeue, peek",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the First-In, First-Out (FIFO) access invariant.",
                "Master core operations: enqueue() at rear, dequeue() at front, peek(), isEmpty().",
                "Recognize why all primary operations execute in O(1) time.",
                "Identify real-world asynchronous queue systems (message brokers, print queues, web servers)."
            ),
            sections = listOf(
                LessonSection(
                    id = "queue_fifo_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Queue?",
                    body = "A Queue is a linear Abstract Data Type (ADT) that models a waiting line operating under the First-In, First-Out (FIFO) principle.\n\nElements are inserted exclusively at the back (rear/tail) and extracted exclusively from the front (head). Imagine people waiting in line at a cinema ticket counter: the first person to arrive is the first person served, and newcomers join the back of the line.",
                    bulletPoints = listOf(
                        "enqueue(x): Inserts element x at the rear in O(1).",
                        "dequeue(): Removes and returns the front element in O(1).",
                        "peek(): Inspects the front element without removing it in O(1).",
                        "isEmpty(): Checks whether the queue contains zero elements in O(1)."
                    )
                ),
                LessonSection(
                    id = "queue_real_world",
                    type = LessonSectionType.REAL_WORLD_EXAMPLES,
                    title = "Asynchronous Computing & Buffering",
                    body = "Queues are the foundational structure connecting asynchronous decoupled computer systems:\n\n- Print Spooler: Print jobs sent to a shared printer wait in FIFO order.\n- Message Brokers (Kafka, RabbitMQ, SQS): Backend microservices communicate asynchronously via distributed queues.\n- Breadth-First Search (BFS): Graph exploration visits vertices level by level using a FIFO queue.\n- Web Server Request Buffers: Incoming HTTP requests queue up when worker threads are busy.",
                    bulletPoints = listOf(
                        "Buffer: Absorbs sudden traffic spikes without crashing services."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Linked-List-Backed FIFO Queue in Kotlin",
                    language = "KOTLIN",
                    code = """class LinkedQueue<T> {
    private class Node<T>(val value: T, var next: Node<T>? = null)
    private var front: Node<T>? = null
    private var rear: Node<T>? = null
    var size: Int = 0
        private set

    // O(1) Enqueue: Add to rear
    fun enqueue(value: T) {
        val newNode = Node(value)
        if (rear == null) {
            front = newNode
            rear = newNode
        } else {
            rear?.next = newNode
            rear = newNode
        }
        size++
    }

    // O(1) Dequeue: Remove from front
    fun dequeue(): T {
        val frontNode = front ?: throw NoSuchElementException("Queue Underflow")
        front = frontNode.next
        if (front == null) rear = null // Queue became empty
        size--
        return frontNode.value
    }

    fun peek(): T = front?.value ?: throw NoSuchElementException("Queue is empty")
    fun isEmpty(): Boolean = front == null
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-18", "enqueue appends to the rear pointer in O(1) time without traversing."),
                        CodeStepExplanation(2, "Lines 21-27", "dequeue removes from front pointer in O(1) time and clears rear when empty.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Queues enforce First-In, First-Out (FIFO) semantics: earliest arrivals are served first.",
                "enqueue(), dequeue(), and peek() all execute in guaranteed O(1) constant time.",
                "A linked list with front and rear pointers delivers true O(1) queue operations.",
                "Essential for Breadth-First Search (BFS), message brokers, and OS process scheduling."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_que_smp_1",
                    question = "Which end of a FIFO queue receives newly inserted elements?",
                    options = listOf("The rear (tail)", "The front (head)", "The middle", "The index with lowest priority"),
                    correctOptionIndex = 0,
                    explanation = "Under FIFO rules, new elements are enqueued at the rear (tail), while removals (dequeue) occur at the front (head)."
                ),
                LessonPracticeQuestion(
                    id = "pq_que_smp_2",
                    question = "Which graph traversal algorithm fundamentally relies on a FIFO queue?",
                    options = listOf(
                        "Breadth-First Search (BFS)",
                        "Depth-First Search (DFS)",
                        "Dijkstra's Algorithm",
                        "Kruskal's Algorithm"
                    ),
                    correctOptionIndex = 0,
                    explanation = "BFS explores vertices level-by-level in concentric rings, requiring a FIFO queue to process immediate neighbors before advancing deeper."
                )
            )
        ),

        // 2. Circular Queue Implementation
        DsaLessonContent(
            lessonId = "queue_circular_l1",
            subtopicId = "queue_circular",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Circular Queue Implementation",
            subtitle = "Ring buffers and modulo arithmetic eliminating memory drift in fixed arrays",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) all operations",
            spaceComplexity = "O(N) fixed capacity",
            learningObjectives = listOf(
                "Understand why naive linear array queues suffer from 'False Queue Full' memory drift.",
                "Apply modulo arithmetic: index = (index + 1) % capacity to wrap pointers around.",
                "Distinguish between Queue Empty (size == 0) and Queue Full (size == capacity).",
                "Learn ring buffer applications in audio streaming and Linux kernel device drivers."
            ),
            sections = listOf(
                LessonSection(
                    id = "circular_queue_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Memory Drift Problem in Linear Arrays",
                    body = "Suppose we implement a queue using a fixed array of size 5:\n1. Enqueue 5 items: rear reaches index 4. The array is full.\n2. Dequeue 3 items: front advances to index 3. Slots 0, 1, and 2 are now vacant!\n3. Try to enqueue another item: rear is at index 4, so a naive check reports 'Queue Full' even though more than half the array is empty!\n\nThis phenomenon is called Memory Drift or False Queue Full. Shifting elements left takes O(N) time, defeating the O(1) queue promise.",
                    bulletPoints = listOf(
                        "Solution: Connect the end of the array back to the beginning using modulo arithmetic!"
                    )
                ),
                LessonSection(
                    id = "ring_buffer_math",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Modulo Pointer Wrapping",
                    body = "A Circular Queue (Ring Buffer) treats the array as a closed circle:\n\n- Next Rear: `rear = (rear + 1) % capacity`\n- Next Front: `front = (front + 1) % capacity`\n\nWhen rear reaches the last index (capacity - 1), (capacity - 1 + 1) % capacity = 0, wrapping smoothly back to index 0!\nWe maintain a `count` variable to distinguish whether front == rear represents completely empty or completely full.",
                    bulletPoints = listOf(
                        "Time: O(1) guaranteed for both enqueue and dequeue.",
                        "Zero Shifting: Elements are never moved in memory."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Circular Queue (Ring Buffer) in Kotlin",
                    language = "KOTLIN",
                    code = """class CircularQueue(val capacity: Int) {
    private val data = IntArray(capacity)
    private var front = 0
    private var rear = 0
    var size = 0
        private set

    fun enqueue(value: Int): Boolean {
        if (isFull()) return false // Queue overflow
        data[rear] = value
        rear = (rear + 1) % capacity // Wrap around!
        size++
        return true
    }

    fun dequeue(): Int {
        if (isEmpty()) throw NoSuchElementException("Queue Underflow")
        val value = data[front]
        front = (front + 1) % capacity // Wrap around!
        size--
        return value
    }

    fun peek(): Int = if (isEmpty()) -1 else data[front]
    fun isEmpty(): Boolean = size == 0
    fun isFull(): Boolean = size == capacity
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-14", "enqueue writes to rear and advances pointer with (rear + 1) % capacity."),
                        CodeStepExplanation(2, "Lines 16-22", "dequeue captures front and advances pointer with (front + 1) % capacity."),
                        CodeStepExplanation(3, "Lines 25-26", "count variable unambiguously resolves empty vs full state.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Circular queues wrap array pointers using modulo arithmetic: (i + 1) % capacity.",
                "Eliminates false queue full and avoids expensive O(N) array memory shifts.",
                "Both enqueue and dequeue operate in guaranteed O(1) constant time.",
                "Widely used as ring buffers in OS audio streaming, video rendering, and network sockets."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_que_cir_1",
                    question = "If capacity is 5 and rear is at index 4, what is the next rear index in a Circular Queue?",
                    options = listOf("0", "5", "1", "4"),
                    correctOptionIndex = 0,
                    explanation = "Applying the wrapping formula: (4 + 1) % 5 = 5 % 5 = 0, so rear wraps back to the beginning of the array."
                ),
                LessonPracticeQuestion(
                    id = "pq_que_cir_2",
                    question = "Why is a Circular Queue preferred over shifting elements in a standard array queue?",
                    options = listOf(
                        "Circular queues achieve O(1) dequeue time, while shifting array elements left takes O(N) time",
                        "Circular queues use half as much memory",
                        "Circular queues automatically sort elements",
                        "Circular queues can only hold prime numbers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Advancing the front pointer with modulo takes 1 operation (O(1)), whereas shifting all remaining elements left takes N operations (O(N))."
                )
            )
        ),

        // 3. Priority Queue & Heap Basics
        DsaLessonContent(
            lessonId = "queue_priority_l1",
            subtopicId = "queue_priority",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Priority Queue & Heap Basics",
            subtitle = "Serving elements by highest priority rather than arrival sequence",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(log N) insert/extract, O(1) peek",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Priority Queue invariant: highest (or lowest) priority is served first.",
                "Compare implementation backings: Unsorted Array vs Sorted Array vs Binary Heap.",
                "Learn why Binary Heaps achieve optimal O(log N) insert and O(log N) extract-min.",
                "Identify foundational algorithms powered by Priority Queues (Dijkstra, Prim's, Huffman)."
            ),
            sections = listOf(
                LessonSection(
                    id = "pq_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Priority Queue?",
                    body = "In a standard queue, elements are served strictly in order of arrival (FIFO). In a Priority Queue, every element has an associated priority value, and elements with higher priority are served BEFORE elements with lower priority.\n\nImagine a hospital Emergency Room: patients are not treated in arrival sequence; a patient with a critical injury is treated ahead of someone with a mild headache.",
                    bulletPoints = listOf(
                        "Min-Priority Queue: Smallest value has highest priority (served first).",
                        "Max-Priority Queue: Largest value has highest priority (served first)."
                    )
                ),
                LessonSection(
                    id = "pq_heap_tradeoffs",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Why Binary Heaps Are the Optimal Backing",
                    body = "Evaluating data structure backings for Priority Queues:\n\n- Unsorted Array: Insert = O(1), Extract-Min = O(N) (must scan whole array).\n- Sorted Array: Insert = O(N) (must shift items), Extract-Min = O(1).\n- Binary Heap (Optimal): Insert = O(log N), Extract-Min = O(log N), Peek-Min = O(1)!\n\nA Binary Heap balances the cost of insertion and extraction symmetrically, providing the ideal structure for heavy traffic.",
                    bulletPoints = listOf(
                        "Dijkstra's Shortest Path: Always extracts the unvisited vertex with smallest tentative distance.",
                        "Huffman Coding: Merges two smallest frequency trees repeatedly to generate optimal prefix codes."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Using PriorityQueue in Kotlin / Android",
                    language = "KOTLIN",
                    code = """import java.util.PriorityQueue

data class Patient(val name: String, val severity: Int) : Comparable<Patient> {
    // Higher severity number = higher priority (comes first)
    override fun compareTo(other: Patient): Int = other.severity.compareTo(this.severity)
}

fun emergencyRoomSimulation() {
    val pq = PriorityQueue<Patient>()
    
    // Enqueue patients with different priorities in O(log N)
    pq.offer(Patient("John (Mild Cough)", severity = 2))
    pq.offer(Patient("Sarah (Severe Trauma)", severity = 10))
    pq.offer(Patient("Mike (Broken Arm)", severity = 6))
    
    // Always extracts highest priority patient in O(log N)
    while (pq.isNotEmpty()) {
        val nextPatient = pq.poll()
        println("Treating: ${'$'}{nextPatient.name}")
    }
    // Output: Sarah (10), Mike (6), John (2)
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 3-6", "Define Patient with custom Comparable interface prioritizing higher severity."),
                        CodeStepExplanation(2, "Lines 12-14", "offer() inserts elements into the underlying binary heap in O(log N) time."),
                        CodeStepExplanation(3, "Lines 17-20", "poll() extracts the highest priority element in O(log N) time.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Priority queues serve elements by relative priority ranking rather than insertion order.",
                "Binary heaps implement priority queues with O(log N) insert and extract, and O(1) peek.",
                "Essential for Dijkstra's shortest path, Prim's minimum spanning tree, and A* pathfinding.",
                "Java/Kotlin's standard `java.util.PriorityQueue` is backed by a min-heap array."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_que_pri_1",
                    question = "What is the time complexity of extracting the minimum element from a Min-Heap backed Priority Queue with N elements?",
                    options = listOf("O(log N)", "O(1)", "O(N)", "O(N log N)"),
                    correctOptionIndex = 0,
                    explanation = "Extracting the root takes O(1), but restoring the heap property (sifting down the replaced root) takes time proportional to tree height: O(log N)."
                ),
                LessonPracticeQuestion(
                    id = "pq_que_pri_2",
                    question = "Why is a Binary Heap preferred over a Sorted Array for implementing a Priority Queue?",
                    options = listOf(
                        "Inserting into a sorted array takes O(N) time due to element shifting, whereas a heap inserts in O(log N)",
                        "A sorted array cannot store negative priorities",
                        "Binary heaps require no memory",
                        "A sorted array takes O(N^2) to find the minimum"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In a sorted array, inserting a new element requires shifting existing elements, costing O(N). A binary heap sifts up along the tree height, costing only O(log N)."
                )
            )
        ),

        // 4. Double-Ended Queue (Deque)
        DsaLessonContent(
            lessonId = "queue_deque_l1",
            subtopicId = "queue_deque",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Double-Ended Queue (Deque)",
            subtitle = "Symmetric double-ended operations and the Monotonic Deque sliding window maximum",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) all end operations",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Double-Ended Queue (Deque) interface: insert and delete at BOTH ends in O(1).",
                "Learn how Deque unifies both Stack and Queue capabilities in a single structure.",
                "Master the Monotonic Deque technique for Sliding Window Maximum in O(N) time.",
                "Observe work-stealing thread pools (e.g., Java ForkJoinPool)."
            ),
            sections = listOf(
                LessonSection(
                    id = "deque_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Deque?",
                    body = "A Deque (pronounced 'deck', short for Double-Ended Queue) is a generalized sequential container that permits insertion and removal at BOTH the front and the back in O(1) constant time.\n\nBecause it supports operations at both ends, a Deque can seamlessly act as either a LIFO Stack or a FIFO Queue, making it the most versatile linear collection in software engineering.",
                    bulletPoints = listOf(
                        "addFirst(x) / removeFirst(): O(1) front operations.",
                        "addLast(x) / removeLast(): O(1) back operations.",
                        "peekFirst() / peekLast(): O(1) inspection."
                    )
                ),
                LessonSection(
                    id = "sliding_window_max_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Monotonic Deque Pattern",
                    body = "Problem: Given an array and window size K, find the maximum element in every sliding window. A naive scan takes O(N * K).\n\nA Monotonic Decreasing Deque stores indices of candidates in descending order:\n1. Before inserting index i, remove all indices from the back of the deque whose values are <= arr[i] (they can never be the maximum again!).\n2. Insert index i at the back.\n3. Remove indices from the front that have fallen out of the current window (index <= i - K).\n4. The front of the deque is ALWAYS the maximum of the current window!\n\nEvery index is added once and removed at most once: strictly O(N) total runtime!",
                    bulletPoints = listOf(
                        "Reduces Sliding Window Maximum from O(N * K) to O(N)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Sliding Window Maximum Using Monotonic Deque",
                    language = "KOTLIN",
                    code = """fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
    if (nums.isEmpty() || k <= 0) return intArrayOf()
    val result = IntArray(nums.size - k + 1)
    val deque = ArrayDeque<Int>() // Stores indices
    
    for (i in nums.indices) {
        // 1. Remove indices out of current window [i - k + 1 .. i]
        if (deque.isNotEmpty() && deque.first() <= i - k) {
            deque.removeFirst()
        }
        
        // 2. Remove smaller elements from back (monotonic decreasing)
        while (deque.isNotEmpty() && nums[deque.last()] <= nums[i]) {
            deque.removeLast()
        }
        
        deque.addLast(i)
        
        // 3. Deque front is always the window maximum
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.first()]
        }
    }
    return result
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-10", "Evict expired window indices from front in O(1)."),
                        CodeStepExplanation(2, "Lines 13-15", "Evict smaller candidates from back to maintain strictly decreasing values."),
                        CodeStepExplanation(3, "Lines 20-22", "Front of deque contains index of current maximum element.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "A Deque supports O(1) insertion, deletion, and inspection at both front and back.",
                "Unifies both Stack (LIFO) and Queue (FIFO) semantics in a single structure.",
                "Monotonic deques solve the sliding window maximum problem in optimal O(N) linear time.",
                "Underpins work-stealing task schedulers in multi-threaded CPU runtimes."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_que_deq_1",
                    question = "What is the time complexity of the Monotonic Deque algorithm for finding the Maximum in all sliding windows of size K?",
                    options = listOf("O(N) Linear Time", "O(N * K)", "O(N log K)", "O(K^2)"),
                    correctOptionIndex = 0,
                    explanation = "Even though elements are popped inside a while loop, each index is pushed onto the deque once and popped at most once across the entire array, bounding total operations to 2N = O(N)."
                ),
                LessonPracticeQuestion(
                    id = "pq_que_deq_2",
                    question = "Which operations can a Deque perform in strictly O(1) constant time?",
                    options = listOf(
                        "Insert and remove at both the front and the back",
                        "Search for an arbitrary unsorted value",
                        "Sort all elements automatically",
                        "Binary search on unsorted inputs"
                    ),
                    correctOptionIndex = 0,
                    explanation = "By architectural definition, a Double-Ended Queue executes addFirst, addLast, removeFirst, and removeLast all in O(1) time."
                )
            )
        )
    )
}
