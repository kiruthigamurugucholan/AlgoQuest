package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object StackLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Stack Operations & LIFO Principle
        DsaLessonContent(
            lessonId = "stack_operations_l1",
            subtopicId = "stack_operations",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Stack Operations & LIFO Principle",
            subtitle = "Restricted-access linear structures operating under Last-In, First-Out semantics",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) push, pop, peek",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Last-In, First-Out (LIFO) invariant.",
                "Master the foundational primitive operations: push(), pop(), peek(), isEmpty().",
                "Learn why all primary operations execute in strictly O(1) constant time.",
                "Connect stack semantics to physical tray dispensers and JVM call stacks."
            ),
            sections = listOf(
                LessonSection(
                    id = "stack_lifo_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Stack?",
                    body = "A Stack is a linear Abstract Data Type (ADT) that models a restricted-access collection adhering to the Last-In, First-Out (LIFO) principle.\n\nElements can only be inserted or extracted from a single end, designated as the 'top'. Imagine a spring-loaded stack of cafeteria trays: you place new trays on the top, and people always take the top tray first. You cannot extract from the middle or bottom without removing everything above it.",
                    bulletPoints = listOf(
                        "push(x): Adds element x to the top of the stack in O(1).",
                        "pop(): Removes and returns the top element in O(1).",
                        "peek(): Returns the top element without removing it in O(1).",
                        "isEmpty(): Checks whether the stack holds zero elements in O(1)."
                    )
                ),
                LessonSection(
                    id = "stack_invariants",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "LIFO Invariants & State Violations",
                    body = "A stack enforces two strict boundary states:\n\n1. Stack Underflow: Occurs when pop() or peek() is attempted on an empty stack (size == 0). Throws NoSuchElementException.\n2. Stack Overflow: Occurs in fixed-capacity stacks when push() is attempted on a full stack (size == capacity). In the JVM, infinite recursion creates java.lang.StackOverflowError when the call stack runs out of memory frames.",
                    bulletPoints = listOf(
                        "All primitive stack operations execute in strictly O(1) constant time."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Basic Stack Interface in Kotlin",
                    language = "KOTLIN",
                    code = """class ArrayStack<T>(private val capacity: Int = 100) {
    private val storage = arrayOfNulls<Any?>(capacity)
    private var topIndex = -1

    fun push(element: T) {
        check(topIndex < capacity - 1) { "Stack Overflow" }
        storage[++topIndex] = element
    }

    @Suppress("UNCHECKED_CAST")
    fun pop(): T {
        check(!isEmpty()) { "Stack Underflow" }
        val item = storage[topIndex] as T
        storage[topIndex--] = null // Prevent memory leak
        return item
    }

    @Suppress("UNCHECKED_CAST")
    fun peek(): T {
        check(!isEmpty()) { "Stack is empty" }
        return storage[topIndex] as T
    }

    fun isEmpty(): Boolean = topIndex == -1
    fun size(): Int = topIndex + 1
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 3", "topIndex initialized to -1 signals an empty stack."),
                        CodeStepExplanation(2, "Lines 5-8", "push pre-increments topIndex and writes element in O(1)."),
                        CodeStepExplanation(3, "Lines 11-17", "pop retrieves item, clears slot to prevent garbage collector leaks, and decrements topIndex.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Stacks enforce Last-In, First-Out (LIFO) ordering through a single accessible 'top' pointer.",
                "push(), pop(), and peek() operations all execute in guaranteed O(1) time.",
                "Stack Underflow occurs when popping an empty stack; Stack Overflow occurs when exceeding capacity.",
                "Forms the computational engine of function execution and language compilers."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_stk_ops_1",
                    question = "What condition defines Stack Underflow?",
                    options = listOf(
                        "Attempting to pop or peek an element from an empty stack",
                        "Attempting to push into a full stack",
                        "When all elements in the stack are negative numbers",
                        "When memory addresses are sorted in reverse"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Stack underflow occurs when an algorithm requests an item from an empty stack where no elements exist to remove."
                ),
                LessonPracticeQuestion(
                    id = "pq_stk_ops_2",
                    question = "If the values 10, 20, and 30 are pushed onto a stack in that sequence, in what order will they be popped?",
                    options = listOf("30, 20, 10", "10, 20, 30", "20, 10, 30", "30, 10, 20"),
                    correctOptionIndex = 0,
                    explanation = "According to LIFO (Last-In, First-Out), the most recently pushed element (30) is the first popped, followed by 20, then 10."
                )
            )
        ),

        // 2. Push, Pop & Peek Implementation
        DsaLessonContent(
            lessonId = "stack_push_pop_l1",
            subtopicId = "stack_push_pop",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Push, Pop & Peek Implementation",
            subtitle = "Array-backed vs. Linked-List-backed stack architectures and memory trade-offs",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) all operations",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Compare Array-backed stacks vs Linked-List-backed stacks.",
                "Understand the dynamic resizing mechanism in array stacks (amortized O(1)).",
                "Learn the zero-allocation linked list stack: prepending at head.",
                "Evaluate cache line hit rate vs heap allocation overhead."
            ),
            sections = listOf(
                LessonSection(
                    id = "stack_impl_comparison",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Array vs. Linked List Stack Backings",
                    body = "A stack can be implemented using either an Array or a Singly Linked List:\n\n1. Array-Backed Stack: Elements stored in contiguous RAM. Fast index operations and superb CPU cache performance. Resizing requires amortized doubling.\n\n2. Linked-List-Backed Stack: Each element is a Node(value, next). The stack's 'top' is simply the list's 'head'. Pushing creates a new node at head in O(1); popping unlinks the head node in O(1). Never overflows, but creates individual heap allocations for every push.",
                    bulletPoints = listOf(
                        "Array Stack: Superior cache locality, amortized O(1) pushes.",
                        "Linked List Stack: True O(1) worst-case pushes, but pointer memory overhead."
                    )
                ),
                LessonSection(
                    id = "stack_pointer_mechanics",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Pointer Tracking and Boundary Checks",
                    body = "In array-based stacks, an integer pointer 'topIndex' tracks the topmost element:\n- Push: Increment topIndex, store item at arr[topIndex].\n- Pop: Read item at arr[topIndex], clear reference, decrement topIndex.\n- Peek: Return arr[topIndex] without modifying topIndex.\n\nAlways guard against Stack Overflow when topIndex reaches capacity - 1, and Stack Underflow when topIndex is -1.",
                    bulletPoints = listOf(
                        "Array Top: Tracked via index integer topIndex.",
                        "Linked Top: Tracked via node reference pointer head."
                    ),
                    highlightNote = "Memory Leak Prevention: In managed languages like Kotlin/Java, always set array slots to null upon popping so objects can be garbage collected."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Linked-List-Backed Stack in Kotlin",
                    language = "KOTLIN",
                    code = """class LinkedStack<T> {
    private class Node<T>(val value: T, val next: Node<T>?)
    private var top: Node<T>? = null
    var size: Int = 0
        private set

    // True O(1) push: prepends at head
    fun push(value: T) {
        top = Node(value, top)
        size++
    }

    // True O(1) pop: advances head to next
    fun pop(): T {
        val currentTop = top ?: throw NoSuchElementException("Stack Underflow")
        top = currentTop.next
        size--
        return currentTop.value
    }

    fun peek(): T = top?.value ?: throw NoSuchElementException("Stack is empty")
    fun isEmpty(): Boolean = top == null
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-11", "push creates a node pointing to current top, then reassigns top in O(1)."),
                        CodeStepExplanation(2, "Lines 14-19", "pop captures top value and points top to top.next in O(1).")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Array-backed stacks provide superior cache performance and amortized O(1) runtime.",
                "Linked-list-backed stacks guarantee worst-case O(1) push without reallocation, but require extra pointer memory.",
                "In both implementations, push, pop, and peek are strictly O(1) operations."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_stk_pp_1",
                    question = "Why does a linked-list-backed stack never suffer from Stack Overflow?",
                    options = listOf(
                        "Nodes are dynamically allocated on the heap as long as system RAM is available",
                        "Because linked lists compress data automatically",
                        "Because linked lists store values in CPU cache",
                        "Because it reuses the same node repeatedly"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Unlike fixed-size arrays, a linked list stack has no predetermined capacity boundary; it allocates new nodes dynamically on the heap until the OS runs out of RAM."
                ),
                LessonPracticeQuestion(
                    id = "pq_stk_pp_2",
                    question = "What is the primary advantage of an array-backed stack over a linked-list-backed stack?",
                    options = listOf(
                        "Contiguous memory layout provides superior CPU cache locality and eliminates pointer overhead",
                        "It supports random access to any element in O(0) time",
                        "It never requires resizing",
                        "It can store heterogeneous data types simultaneously"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Arrays store items adjacent in RAM, avoiding individual heap object allocations and allowing CPU hardware to cache consecutive stack elements efficiently."
                )
            )
        ),

        // 3. Infix to Postfix Conversion
        DsaLessonContent(
            lessonId = "stack_infix_postfix_l1",
            subtopicId = "stack_infix_postfix",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Infix to Postfix Conversion",
            subtitle = "Operator precedence, associativity, and Dijkstra's Shunting-Yard algorithm",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N)",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand notation types: Infix (A + B), Postfix / RPN (A B +), Prefix (+ A B).",
                "Learn why computers evaluate Postfix expressions without parentheses or ambiguity.",
                "Master Edsger Dijkstra's Shunting-Yard Algorithm using an operator stack.",
                "Evaluate Postfix expressions in O(N) time using an operand stack."
            ),
            sections = listOf(
                LessonSection(
                    id = "notations_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Why Not Use Infix?",
                    body = "Humans write mathematical expressions in Infix notation: `A + B * C`. Resolving this requires knowing that multiplication has higher precedence than addition, or using parentheses `(A + B) * C`.\n\nComputers and calculators evaluate Postfix notation (Reverse Polish Notation, RPN): `A B C * +`.\nIn Postfix:\n- Operands appear in exact calculation sequence.\n- Operators appear immediately after their operands.\n- No parentheses or operator precedence rules are ever required during evaluation!",
                    bulletPoints = listOf(
                        "Infix: `3 + 4 * 2`",
                        "Postfix: `3 4 2 * +` (Evaluates: 4 * 2 = 8, then 3 + 8 = 11)."
                    )
                ),
                LessonSection(
                    id = "shunting_yard_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Dijkstra's Shunting-Yard Algorithm",
                    body = "Invented by Edsger Dijkstra, the Shunting-Yard algorithm converts Infix to Postfix using an operator stack:\n\n1. If token is an Operand: Append directly to output.\n2. If token is '(': Push onto operator stack.\n3. If token is ')': Pop operators from stack to output until matching '(' is found.\n4. If token is Operator op1: While stack top holds operator op2 with higher/equal precedence, pop op2 to output. Then push op1.\n5. End of input: Pop all remaining operators to output.",
                    bulletPoints = listOf(
                        "Time Complexity: O(N) single pass over input.",
                        "Space Complexity: O(N) operator stack."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Evaluating Postfix Expressions With a Stack",
                    language = "KOTLIN",
                    code = """fun evaluatePostfix(tokens: List<String>): Int {
    val stack = ArrayDeque<Int>() // Operand stack
    
    for (token in tokens) {
        when (token) {
            "+", "-", "*", "/" -> {
                val b = stack.removeLast() // Second operand
                val a = stack.removeLast() // First operand
                val result = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> a / b
                    else -> 0
                }
                stack.addLast(result)
            }
            else -> {
                stack.addLast(token.toInt()) // Operand pushed to stack
            }
        }
    }
    return stack.removeLast() // Final answer
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 6-18", "When an operator is encountered, pop the top two numbers, apply operation, and push result back."),
                        CodeStepExplanation(2, "Lines 19-21", "When a number is encountered, push it onto the operand stack."),
                        CodeStepExplanation(3, "Line 24", "Final remaining stack value is the fully evaluated mathematical result.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Postfix notation eliminates ambiguous operator precedence and parentheses.",
                "Dijkstra's Shunting-Yard algorithm converts Infix to Postfix in O(N) time.",
                "Postfix evaluation uses an operand stack: pop two, apply operator, push result.",
                "Powers compilers, interpreters, and scientific calculators."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_stk_inf_1",
                    question = "What is the equivalent Postfix expression for the Infix expression: 'A + B * C'?",
                    options = listOf("A B C * +", "+ A * B C", "A B + C *", "A B * C +"),
                    correctOptionIndex = 0,
                    explanation = "Multiplication has higher precedence than addition, so B * C becomes 'B C *'. Then adding A produces 'A B C * +'."
                ),
                LessonPracticeQuestion(
                    id = "pq_stk_inf_2",
                    question = "When evaluating a Postfix expression like '10 4 -', why is the first popped element treated as operand B and the second as operand A?",
                    options = listOf(
                        "Because the subtraction operator is non-commutative: a - b != b - a, and 4 was pushed after 10",
                        "Because stacks automatically invert signs",
                        "Because negative numbers are forbidden",
                        "To prevent stack underflow"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because a stack is LIFO, the second operand (4) was pushed last and is popped first. Subtraction is not commutative (10 - 4 = 6 vs 4 - 10 = -6), so a must be the first operand."
                )
            )
        ),

        // 4. Balanced Parentheses Matching
        DsaLessonContent(
            lessonId = "stack_balanced_parentheses_l1",
            subtopicId = "stack_balanced_parentheses",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Balanced Parentheses Matching",
            subtitle = "Bracket nesting verification, syntax validation, and early rejection invariants",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the Balanced Bracket invariant: every closer matches the most recent opener.",
                "Implement stack-based bracket validation handling '()', '[]', '{}'.",
                "Apply early-exit checks: odd string length and unmatched closing brackets.",
                "Verify final invariant: stack must be completely empty upon string completion."
            ),
            sections = listOf(
                LessonSection(
                    id = "parentheses_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Grammar of Matching Brackets",
                    body = "Code editors and compilers validate that code brackets are balanced:\n\n1. Every opening bracket must have a corresponding closing bracket of the same type.\n2. Brackets must close in reverse order of opening (e.g., `{[()]}` is valid, but `{[(])}` is invalid).\n\nBecause the most recently opened bracket must be the first one closed, the problem is a pure manifestation of LIFO stack semantics!",
                    bulletPoints = listOf(
                        "Odd Length Rule: A string with an odd number of brackets can NEVER be balanced (instant O(1) rejection)."
                    )
                ),
                LessonSection(
                    id = "parentheses_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Algorithm Rules",
                    body = "Iterate through each character:\n1. If character is an opener `(`, `[`, `{`: push onto stack.\n2. If character is a closer `)`, `]`, `}`:\n   - If stack is empty -> Unmatched closer! Return false.\n   - If stack top doesn't match closer type -> Bracket type mismatch! Return false.\n   - Otherwise, pop the matching opener from the stack.\n3. End of string: If stack is empty, return true. If stack still holds unclosed openers, return false!",
                    bulletPoints = listOf(
                        "Time: O(N) single pass.",
                        "Space: O(N) stack worst case."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Valid Parentheses in Kotlin",
                    language = "KOTLIN",
                    code = """fun isValidParentheses(s: String): Boolean {
    if (s.length % 2 != 0) return false // Odd length cannot be balanced!
    
    val stack = ArrayDeque<Char>()
    val matching = mapOf(')' to '(', ']' to '[', '}' to '{')
    
    for (ch in s) {
        if (ch in matching.values) {
            stack.addLast(ch) // Push opening bracket
        } else if (ch in matching.keys) {
            // Unmatched closer or mismatched bracket type
            if (stack.isEmpty() || stack.removeLast() != matching[ch]) {
                return false
            }
        }
    }
    return stack.isEmpty() // Must have no unclosed openers remaining
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 2", "O(1) early exit: strings with odd lengths can never have balanced pairs."),
                        CodeStepExplanation(2, "Lines 8-10", "Push opening brackets '(', '[', '{' onto stack."),
                        CodeStepExplanation(3, "Lines 11-16", "For closing brackets, pop stack and confirm opener matches required pair."),
                        CodeStepExplanation(4, "Line 18", "Confirm stack is empty: lingering openers mean unclosed brackets.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Closing brackets must pair with the most recently opened bracket (LIFO).",
                "Odd-length strings can be rejected immediately in O(1) time.",
                "Stack underflow during a closing bracket means an extra unmatched closer.",
                "Non-empty stack at the end means an unclosed opening bracket."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_stk_par_1",
                    question = "Why does a non-empty stack at the end of the bracket validation algorithm mean the string is INVALID?",
                    options = listOf(
                        "It indicates at least one opening bracket was never closed",
                        "Because valid stacks must self-destruct",
                        "Because odd numbers cannot be stored in stacks",
                        "Because all closing brackets were duplicated"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If the stack still holds items after reading the entire string, it means an opening bracket like '(' or '{' was opened but never had a matching closing bracket."
                ),
                LessonPracticeQuestion(
                    id = "pq_stk_par_2",
                    question = "What is the time and auxiliary space complexity of the bracket matching algorithm for a string of length N?",
                    options = listOf("O(N) Time, O(N) Space", "O(N^2) Time, O(1) Space", "O(log N) Time, O(N) Space", "O(1) Time, O(1) Space"),
                    correctOptionIndex = 0,
                    explanation = "The algorithm inspects each of the N characters once in O(N) time. In the worst case (e.g. '(((((('), all N characters are pushed onto the stack, using O(N) space."
                )
            )
        ),

        // 5. Real-World Applications of Stacks
        DsaLessonContent(
            lessonId = "stack_applications_l1",
            subtopicId = "stack_applications",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Real-World Applications of Stacks",
            subtitle = "Call stack execution, monotonic stacks, undo/redo buffers, and syntax trees",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Understand the operating system Call Stack and activation records.",
                "Implement Undo/Redo buffers using dual stacks.",
                "Master the Monotonic Stack pattern for Next Greater Element in O(N) time.",
                "Identify where web browsers and compilers rely on stack invariants."
            ),
            sections = listOf(
                LessonSection(
                    id = "stack_apps_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Where Stacks Power Modern Computing",
                    body = "Stacks are not just abstract data structures; they are built directly into hardware CPU instruction sets:\n\n1. Program Call Stack: When function A() calls function B(), the CPU pushes A's instruction pointer and local variables onto the call stack. When B() returns, the stack pops back to A.\n2. Undo/Redo (Ctrl+Z / Ctrl+Y): Text editors maintain an Undo Stack and Redo Stack.\n3. Browser History: Forward/Back button navigation.\n4. HTML DOM Parsing: Verifying opening `<p>` and closing `</p>` tags.",
                    bulletPoints = listOf(
                        "CPU Stack Pointer (ESP/RSP): Hardware register tracking top of stack."
                    )
                ),
                LessonSection(
                    id = "monotonic_stack_intro",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Monotonic Stack Pattern",
                    body = "A Monotonic Stack maintains elements in strictly increasing or decreasing order.\n\nProblem: Given an array, find the Next Greater Element for every index. A naive nested loop takes O(N^2) time.\n\nWith a monotonic decreasing stack:\n- Iterate through elements.\n- While stack is not empty and current element > arr[stack.top()], pop top and record current element as its Next Greater Element!\n- Push current element onto stack.\n\nEvery element is pushed once and popped at most once: strictly O(N) linear time!",
                    bulletPoints = listOf(
                        "Solves Next Greater Element, Daily Temperatures, and Largest Rectangle in Histogram in O(N)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Next Greater Element Using Monotonic Stack",
                    language = "KOTLIN",
                    code = """fun nextGreaterElement(nums: IntArray): IntArray {
    val result = IntArray(nums.size) { -1 }
    val stack = ArrayDeque<Int>() // Stores indices in decreasing order
    
    for (i in nums.indices) {
        // While current element is greater than element at stack top
        while (stack.isNotEmpty() && nums[i] > nums[stack.last()]) {
            val poppedIndex = stack.removeLast()
            result[poppedIndex] = nums[i] // nums[i] is next greater!
        }
        stack.addLast(i) // Push current index
    }
    return result
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 2", "Initialize result array with -1 sentinel indicating no greater element found."),
                        CodeStepExplanation(2, "Lines 7-10", "Resolve all pending smaller elements on stack when a larger element arrives."),
                        CodeStepExplanation(3, "Line 11", "Push current index onto stack to find its future greater element.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "CPU call stacks store function activation records using hardware stack pointers.",
                "Dual stacks power Undo/Redo buffers in modern word processors and IDEs.",
                "Monotonic stacks resolve range queries like Next Greater Element in optimal O(N) time.",
                "HTML, XML, and JSON parsers rely on stacks to validate hierarchical syntax trees."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_stk_app_1",
                    question = "Why does the Monotonic Stack algorithm for Next Greater Element run in O(N) instead of O(N^2)?",
                    options = listOf(
                        "Each element is pushed onto the stack exactly once and popped at most once across the entire algorithm",
                        "Because the array is sorted before processing",
                        "Because it skips negative numbers",
                        "Because it uses binary search inside the loop"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Even though there is a while loop inside a for loop, no element can be popped more than once. Total push and pop operations cannot exceed 2N, guaranteeing O(N) amortized runtime."
                ),
                LessonPracticeQuestion(
                    id = "pq_stk_app_2",
                    question = "How do text editors implement Undo (Ctrl+Z) and Redo (Ctrl+Y) functionality?",
                    options = listOf(
                        "Using two stacks: an Undo stack for recorded actions and a Redo stack for undone actions",
                        "Using a binary heap with random eviction",
                        "By saving entire file snapshots to disk on every keystroke",
                        "Using a circular queue of fixed size 1"
                    ),
                    correctOptionIndex = 0,
                    explanation = "When an action occurs, it is pushed onto the Undo stack. Pressing Undo pops it from Undo and pushes it to Redo. Pressing Redo pops from Redo and pushes back to Undo."
                )
            )
        )
    )
}
