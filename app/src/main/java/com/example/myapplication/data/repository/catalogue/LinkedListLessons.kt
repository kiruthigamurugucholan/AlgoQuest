package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object LinkedListLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Singly Linked List
        DsaLessonContent(
            lessonId = "ll_singly_l1",
            subtopicId = "ll_singly",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Singly Linked List",
            subtitle = "Non-contiguous dynamic node chains linked by forward memory references",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) prepend, O(N) access/search",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Node architecture: Data value and 'next' pointer reference.",
                "Compare non-contiguous dynamic allocation against contiguous array storage.",
                "Observe O(1) prepend (insert at head) vs O(N) arbitrary index traversal.",
                "Learn why linked lists do not support instant random access (arr[i])."
            ),
            sections = listOf(
                LessonSection(
                    id = "singly_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Singly Linked List?",
                    body = "A Singly Linked List is a linear data structure where elements (called nodes) are not stored in contiguous memory locations. Instead, each node contains two components:\n1. Data: The actual value stored.\n2. Next: A reference (pointer) to the memory address of the next node in sequence.\n\nThe list begins at a reference pointer called 'head' and terminates when a node's next pointer points to 'null'.",
                    bulletPoints = listOf(
                        "Dynamic Size: Allocates nodes on heap on-demand; no initial capacity needed.",
                        "No Memory Shifting: Inserting at head takes O(1) pointer updates with zero shifting.",
                        "No Random Access: Reaching index k requires hopping through k pointers from head = O(N)."
                    )
                ),
                LessonSection(
                    id = "singly_vs_array",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "Arrays vs. Singly Linked Lists",
                    body = "Key engineering trade-offs between arrays and linked lists:\n\n- Access Time: Arrays = O(1); Linked Lists = O(N).\n- Insert at Head: Arrays = O(N) right shifts; Linked Lists = O(1).\n- Memory Overhead: Arrays store only raw data; Linked Lists store data PLUS a pointer (4 to 8 bytes per node) and suffer from reduced CPU cache locality.",
                    bulletPoints = listOf(
                        "Prepend (Insert at head): O(1)",
                        "Append (with tail pointer): O(1)",
                        "Search / Access: O(N)"
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Singly Linked List Implementation in Kotlin",
                    language = "KOTLIN",
                    code = """class ListNode<T>(val value: T, var next: ListNode<T>? = null)

class SinglyLinkedList<T> {
    var head: ListNode<T>? = null
    var size: Int = 0

    // O(1) Prepend: Insert new element at head
    fun prepend(value: T) {
        val newNode = ListNode(value, head)
        head = newNode
        size++
    }

    // O(N) Traversal: Search for an element
    fun contains(target: T): Boolean {
        var current = head
        while (current != null) {
            if (current.value == target) return true
            current = current.next
        }
        return false
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 1", "ListNode holds a generic payload and a nullable next reference."),
                        CodeStepExplanation(2, "Lines 8-12", "Prepend creates a new node pointing to the current head, then updates head in O(1) time."),
                        CodeStepExplanation(3, "Lines 15-22", "Traversal walks pointer-by-pointer until finding target or hitting null.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Singly linked lists store elements as independent heap nodes connected by forward pointers.",
                "Inserting or removing at the head is strictly O(1) constant time.",
                "Accessing an element by index requires O(N) linear pointer traversal.",
                "Extra pointer storage adds memory overhead compared to primitive arrays."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_ll_sng_1",
                    question = "Why does accessing element i in a Singly Linked List take O(N) time instead of O(1)?",
                    options = listOf(
                        "Nodes are scattered non-contiguously in memory, so the CPU must follow 'next' pointers sequentially from the head",
                        "Because linked lists are stored on disk instead of RAM",
                        "Because every node must be decrypted",
                        "Because Java converts linked lists to stacks"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because nodes can live anywhere in heap memory without predictable offsets, reaching index i requires following the chain of pointers i times starting at the head node."
                ),
                LessonPracticeQuestion(
                    id = "pq_ll_sng_2",
                    question = "What is the time complexity of inserting a new node at the very beginning (head) of a Singly Linked List?",
                    options = listOf("O(1) Constant", "O(N) Linear", "O(log N)", "O(N^2)"),
                    correctOptionIndex = 0,
                    explanation = "Prepend sets the new node's next pointer to head, and points head to the new node — requiring only two pointer assignments (O(1))."
                )
            )
        ),

        // 2. Doubly Linked List
        DsaLessonContent(
            lessonId = "ll_doubly_l1",
            subtopicId = "ll_doubly",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Doubly Linked List",
            subtitle = "Bidirectional traversal with forward and backward pointer links",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) node deletion, O(N) access",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Doubly Linked Node: Data, 'next', and 'prev' pointers.",
                "Learn bidirectional traversal: traversing forward from head or backward from tail.",
                "Execute O(1) deletion of an arbitrary node given only its direct reference.",
                "Analyze memory overhead: 2 pointers per node."
            ),
            sections = listOf(
                LessonSection(
                    id = "doubly_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Bidirectional Navigation",
                    body = "A Doubly Linked List (DLL) extends the singly linked list by equipping each node with TWO pointers:\n1. next: Points to the following node.\n2. prev: Points to the preceding node.\n\nThis enables bidirectional traversal and allows deleting a node in O(1) time without having to traverse from head to find the predecessor!",
                    bulletPoints = listOf(
                        "Head Invariant: head.prev == null.",
                        "Tail Invariant: tail.next == null.",
                        "Node Deletion: node.prev.next = node.next and node.next.prev = node.prev in O(1)!"
                    )
                ),
                LessonSection(
                    id = "doubly_use_cases",
                    type = LessonSectionType.USE_CASES,
                    title = "Where Doubly Linked Lists Power Systems",
                    body = "Because DLLs provide instant O(1) removal and insertion when given a node pointer, they form the backbone of advanced data structures:\n\n- LRU Cache (Least Recently Used): Combines a HashMap with a Doubly Linked List to achieve O(1) get and O(1) put with recency eviction.\n- Browser Navigation: Back and Forward history buttons.\n- Music Player Playlists: Next Track and Previous Track navigation.",
                    bulletPoints = listOf(
                        "Time: O(1) delete when node pointer is known; O(N) search.",
                        "Space: Higher overhead (2 pointers per node)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Doubly Linked List Node & O(1) Deletion",
                    language = "KOTLIN",
                    code = """class DoublyNode<T>(
    val value: T,
    var prev: DoublyNode<T>? = null,
    var next: DoublyNode<T>? = null
)

class DoublyLinkedList<T> {
    var head: DoublyNode<T>? = null
    var tail: DoublyNode<T>? = null

    // O(1) deletion of an existing node (no search required!)
    fun removeNode(node: DoublyNode<T>) {
        if (node.prev != null) {
            node.prev?.next = node.next
        } else {
            head = node.next // Removing head
        }

        if (node.next != null) {
            node.next?.prev = node.prev
        } else {
            tail = node.prev // Removing tail
        }
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 1-5", "DoublyNode holds value alongside both prev and next reference pointers."),
                        CodeStepExplanation(2, "Lines 12-23", "Deletes target node in O(1) time by rewiring neighboring pointers around it.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Doubly linked lists provide bidirectional traversal via 'next' and 'prev' pointers.",
                "Given a direct node reference, deletion executes in O(1) constant time.",
                "Forms the foundational data structure behind LRU Cache and browser history.",
                "Requires extra memory for two pointers per node."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_ll_dbl_1",
                    question = "Why can a node be deleted in O(1) time in a Doubly Linked List but takes O(N) in a Singly Linked List?",
                    options = listOf(
                        "The Doubly Linked Node already holds a direct reference to its predecessor (prev), so no search traversal is needed",
                        "Because doubly linked lists are stored in contiguous memory",
                        "Because doubly linked lists use hash codes",
                        "Because memory is deallocated automatically"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In a singly linked list, unlinking a node requires locating its predecessor node, which takes O(N) traversal. A doubly linked node already references its predecessor via 'prev', enabling instant O(1) pointer unlinking."
                ),
                LessonPracticeQuestion(
                    id = "pq_ll_dbl_2",
                    question = "Which system architecture heavily relies on a Doubly Linked List paired with a HashMap?",
                    options = listOf("LRU Cache (Least Recently Used Cache)", "Binary Search Tree", "QuickSort", "Counting Sort"),
                    correctOptionIndex = 0,
                    explanation = "An LRU Cache combines a HashMap (for O(1) key lookup) with a Doubly Linked List (for O(1) moving/evicting nodes based on access recency)."
                )
            )
        ),

        // 3. Circular Linked List
        DsaLessonContent(
            lessonId = "ll_circular_l1",
            subtopicId = "ll_circular",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Circular Linked List",
            subtitle = "Continuous looping chains where the tail node reconnects to the head",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) insert at head/tail, O(N) traversal",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Circular Invariant: tail.next == head (no null pointers).",
                "Implement do-while traversal to prevent infinite loop traps.",
                "Learn the Tail-Pointer trick for O(1) insertion at both head AND tail.",
                "Identify real-world cyclic applications (Round-Robin CPU scheduling, multiplayer games)."
            ),
            sections = listOf(
                LessonSection(
                    id = "circular_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Continuous Ring Structure",
                    body = "In a Circular Linked List, the last node does not point to null. Instead, the final node's next pointer points directly back to the first node (head), forming an unbroken loop.\n\nBecause there is no null terminator, traversing a circular linked list requires tracking the starting node to terminate the loop once a full cycle is completed.",
                    bulletPoints = listOf(
                        "No Null Termination: Any node can serve as a starting point for traversal.",
                        "Cycle Detection: Requires do-while or comparison with starting node.",
                        "Circular Singly or Doubly: Can be implemented with 1 or 2 pointers per node."
                    )
                ),
                LessonSection(
                    id = "tail_pointer_trick",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Tail-Pointer Optimization",
                    body = "If you maintain a reference to the 'tail' node:\n- The head is simply: tail.next!\n- Prepending an element (insert at head): insert after tail in O(1).\n- Appending an element (insert at tail): insert after tail and advance tail = new_node in O(1)!\n\nA single tail pointer grants instantaneous O(1) access to both ends of the list.",
                    bulletPoints = listOf(
                        "Round-Robin Scheduling: OS gives equal CPU time slices to active processes in a circular queue.",
                        "Multiplayer Turn Management: Board games cycling turns clockwise between players."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Circular Linked List With Tail Pointer",
                    language = "KOTLIN",
                    code = """class CircularNode<T>(val value: T, var next: CircularNode<T>? = null)

class CircularLinkedList<T> {
    private var tail: CircularNode<T>? = null

    // O(1) Append using single tail pointer
    fun append(value: T) {
        val newNode = CircularNode(value)
        if (tail == null) {
            newNode.next = newNode // Points to itself
            tail = newNode
        } else {
            newNode.next = tail?.next // Point to head
            tail?.next = newNode      // Old tail points to new node
            tail = newNode            // Advance tail
        }
    }

    // Safe traversal using do-while
    fun printAll() {
        if (tail == null) return
        var current = tail?.next // Start at head
        do {
            print("${'$'}{current?.value} -> ")
            current = current?.next
        } while (current != tail?.next) // Stop after 1 full cycle
        println("(back to head)")
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-18", "Single tail pointer enables O(1) insertion at both ends of the circular ring."),
                        CodeStepExplanation(2, "Lines 21-29", "Traverse using do-while loop to execute at least once and terminate upon returning to head.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "In circular linked lists, the last node links back to the first node; no node points to null.",
                "A single 'tail' pointer provides O(1) insertion at both head and tail.",
                "Must be traversed with do-while loops or stop checks to avoid infinite loops.",
                "Naturally models cyclic processes like CPU round-robin scheduling and turn-based games."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_ll_cir_1",
                    question = "Why does maintaining a 'tail' pointer in a Circular Linked List give O(1) access to the head node as well?",
                    options = listOf(
                        "Because by circular definition, tail.next points directly to the head node",
                        "Because tail and head share the same memory address",
                        "Because the JVM caches the head pointer in registers",
                        "Because all nodes in a circular list have index 0"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In a circular list, the last node links back to the start. Therefore, tail.next is always the head node, giving instant O(1) access to both endpoints."
                ),
                LessonPracticeQuestion(
                    id = "pq_ll_cir_2",
                    question = "What happens if a standard 'while (current != null)' traversal loop is executed on a Circular Linked List?",
                    options = listOf(
                        "An infinite loop occurs because no node in the list ever has a null next pointer",
                        "A NullPointerException is thrown immediately",
                        "The list terminates at index 0",
                        "The CPU stops executing"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because circular lists never terminate with null, testing for '!= null' will run endlessly around the ring until the process hangs or runs out of execution quota."
                )
            )
        ),

        // 4. Insertion & Deletion in Linked Lists
        DsaLessonContent(
            lessonId = "ll_insertion_deletion_l1",
            subtopicId = "ll_insertion_deletion",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Insertion & Deletion in Linked Lists",
            subtitle = "Pointer manipulation patterns, sentinel dummy nodes, and edge-case handling",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(1) at known position, O(N) by value",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Master pointer updates for inserting at head, middle, and tail.",
                "Understand pointer updates for deleting nodes by value or reference.",
                "Learn the Sentinel (Dummy Head) technique to eliminate null-check edge cases.",
                "Avoid common bugs: memory leaks, lost pointers, and NullPointerExceptions."
            ),
            sections = listOf(
                LessonSection(
                    id = "pointer_manipulation_rules",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Golden Rule of Pointer Updates",
                    body = "When inserting a node X between node A and node B (where A.next == B):\n\n1. ALWAYS connect the new node first: X.next = A.next (points X to B).\n2. THEN update predecessor: A.next = X (points A to X).\n\nIf you execute step 2 first (A.next = X), you overwrite the only reference to node B and the rest of the entire list is permanently lost in memory!",
                    bulletPoints = listOf(
                        "Rule: Never overwrite an existing pointer until you have safely stored or pointed to its destination."
                    )
                ),
                LessonSection(
                    id = "dummy_head_technique",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Sentinel / Dummy Head Technique",
                    body = "Deleting or inserting at the head of a linked list usually requires ugly special-case code (e.g., if (head.value == target) head = head.next).\n\nBy creating a temporary 'Dummy Head' node where dummy.next = head:\n- The actual head is now just a regular node with a predecessor (dummy)!\n- The exact same deletion loop handles deleting the head, middle nodes, or tail uniformly.\n- Finally, return dummy.next as the new list head.",
                    bulletPoints = listOf(
                        "Eliminates 90% of null-pointer edge cases in linked list problems."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Delete Node by Value Using Dummy Head",
                    language = "KOTLIN",
                    code = """class Node(val value: Int, var next: Node? = null)

fun deleteNodeByValue(head: Node?, target: Int): Node? {
    // Sentinel Dummy Node pointing to actual head
    val dummy = Node(0, head)
    var prev: Node? = dummy
    var curr: Node? = head
    
    while (curr != null) {
        if (curr.value == target) {
            prev?.next = curr.next // Unlink curr in O(1)
            break
        }
        prev = curr
        curr = curr.next
    }
    
    return dummy.next // Cleanly returns new head even if original head was deleted!
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 5", "Create dummy node whose next pointer references the original head."),
                        CodeStepExplanation(2, "Lines 10-13", "When target match is found, prev.next = curr.next bypasses the matched node."),
                        CodeStepExplanation(3, "Line 19", "Returning dummy.next guarantees correct head reference without separate if-statements.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Always point the new node's next pointer before updating predecessor pointers to avoid lost data.",
                "Using a sentinel dummy head eliminates special-casing for head insertions and deletions.",
                "Unlinking a node in garbage-collected languages (Kotlin) automatically frees unreachable nodes.",
                "Handling empty lists and single-element lists are mandatory edge cases."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_ll_insdel_1",
                    question = "What happens if you execute 'A.next = X' before setting 'X.next = A.next' when inserting X after A?",
                    options = listOf(
                        "You overwrite the reference to the rest of the list, permanently losing all subsequent nodes",
                        "The node is inserted in reverse order",
                        "A NullPointerException is thrown by the compiler",
                        "The list converts to an array"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If you assign A.next = X first, the reference to the original subsequent node is destroyed. X now has no reference to the rest of the list, severing the chain."
                ),
                LessonPracticeQuestion(
                    id = "pq_ll_insdel_2",
                    question = "What is the primary architectural purpose of a Dummy / Sentinel head node in linked list algorithms?",
                    options = listOf(
                        "It standardizes operations so inserting or deleting the head uses identical logic as middle nodes",
                        "It doubles the memory capacity of the list",
                        "It sorts the nodes automatically",
                        "It allows random indexing in O(1) time"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A dummy head gives the actual head node a predecessor, eliminating conditional special cases when inserting or deleting at index 0."
                )
            )
        ),

        // 5. Reversing a Linked List
        DsaLessonContent(
            lessonId = "ll_reversal_l1",
            subtopicId = "ll_reversal",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Reversing a Linked List",
            subtitle = "Three-pointer in-place link inversion in O(N) time and O(1) space",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) iterative, O(N) recursive",
            learningObjectives = listOf(
                "Master the classic Three-Pointer technique: prev, curr, and next.",
                "Implement in-place pointer reversal with O(1) auxiliary space.",
                "Understand the recursive reversal approach and its call-stack implications.",
                "Verify invariants: after processing curr, curr.next points backward to prev."
            ),
            sections = listOf(
                LessonSection(
                    id = "reversal_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Inverting Pointer Direction",
                    body = "Reversing a singly linked list means flipping every node's next pointer so that the original head becomes the tail (pointing to null), and the original tail becomes the new head.\n\nBecause you only have forward links, you must carefully track three adjacent pointers in lockstep:\n1. prev: The node that should become current's new destination (starts at null).\n2. curr: The node currently being inverted.\n3. next: A temporary bookmark saving curr.next before it is overwritten.",
                    bulletPoints = listOf(
                        "Iterative Time: O(N) single pass.",
                        "Iterative Space: Strictly O(1) auxiliary space.",
                        "Recursive Space: O(N) call stack."
                    )
                ),
                LessonSection(
                    id = "three_pointer_steps",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The 4-Step Inversion Loop",
                    body = "In every iteration of the loop:\n\n1. next = curr.next  (Save next node address)\n2. curr.next = prev  (Reverse current node's pointer!)\n3. prev = curr       (Advance prev forward)\n4. curr = next       (Advance curr forward)\n\nWhen curr reaches null, the entire list is reversed and prev is pointing directly to the new head!",
                    bulletPoints = listOf(
                        "Never lose the reference to next before inverting curr.next."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Iterative O(1) Space Linked List Reversal in Kotlin",
                    language = "KOTLIN",
                    code = """class Node(val value: Int, var next: Node? = null)

fun reverseList(head: Node?): Node? {
    var prev: Node? = null
    var curr: Node? = head
    
    while (curr != null) {
        val nextTemp = curr.next // 1. Bookmark next node
        curr.next = prev         // 2. Invert pointer backwards
        prev = curr              // 3. Step prev forward
        curr = nextTemp          // 4. Step curr forward
    }
    
    return prev // prev is now the new head!
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 4-5", "Initialize prev to null (which becomes tail's terminator) and curr to head."),
                        CodeStepExplanation(2, "Line 8", "Save curr.next into nextTemp before breaking the forward reference."),
                        CodeStepExplanation(3, "Line 9", "Point curr.next backward to prev."),
                        CodeStepExplanation(4, "Lines 10-11", "Advance both prev and curr one step forward along the chain.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Reversing a singly linked list in-place requires three pointers: prev, curr, and next.",
                "Always bookmark curr.next before reassigning it backward to prev.",
                "Operates in optimal O(N) time and strictly O(1) auxiliary space.",
                "When curr reaches null, prev is returned as the new head."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_ll_rev_1",
                    question = "Why must 'curr.next' be saved in a temporary variable 'nextTemp' before executing 'curr.next = prev'?",
                    options = listOf(
                        "Because setting curr.next = prev overwrites the only reference to the remainder of the unreversed list",
                        "Because Kotlin variables cannot be reassigned without temporary copies",
                        "To prevent stack overflow",
                        "Because prev is a read-only val"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If you assign curr.next = prev first, you overwrite the pointer to the next node in line, severing your connection to the rest of the list."
                ),
                LessonPracticeQuestion(
                    id = "pq_ll_rev_2",
                    question = "What does the 'prev' pointer point to when the iterative reversal while-loop terminates (curr == null)?",
                    options = listOf(
                        "The new head of the reversed linked list (formerly the tail)",
                        "Null",
                        "The original head node",
                        "The median node"
                    ),
                    correctOptionIndex = 0,
                    explanation = "When curr reaches null, prev is resting on the last valid node of the original list, which is now the new head of the reversed list."
                )
            )
        )
    )
}
