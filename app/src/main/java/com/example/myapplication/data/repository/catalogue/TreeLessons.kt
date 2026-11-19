package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object TreeLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Binary Tree Fundamentals
        DsaLessonContent(
            lessonId = "tree_binary_l1",
            subtopicId = "tree_binary",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Binary Tree Fundamentals",
            subtitle = "Hierarchical node branching where each node has at most two children",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N) traversal",
            spaceComplexity = "O(h) stack",
            learningObjectives = listOf(
                "Understand the hierarchical anatomy: Root, Leaf, Edge, Height, and Depth.",
                "Distinguish between Full, Complete, and Perfect Binary Trees.",
                "Calculate tree invariants: maximum nodes at level L = 2^L; maximum nodes for height h = 2^(h+1) - 1.",
                "Represent binary trees in memory using TreeNode objects."
            ),
            sections = listOf(
                LessonSection(
                    id = "tree_anatomy",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Binary Tree?",
                    body = "A Binary Tree is a non-linear hierarchical data structure in which each node has at most two children, typically referred to as the 'left child' and the 'right child'.\n\nUnlike arrays and linked lists which represent linear sequential data, trees naturally represent hierarchies like folder file systems, HTML DOM trees, organizational charts, and decision logic.",
                    bulletPoints = listOf(
                        "Root: Topmost node without any parent.",
                        "Leaf Node: Node with zero children (left == null && right == null).",
                        "Edge: Link connecting parent to child.",
                        "Height (h): Number of edges on the longest downward path from root to leaf."
                    )
                ),
                LessonSection(
                    id = "tree_classifications",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Tree Classifications",
                    body = "Trees are classified by structural completeness:\n\n1. Full Binary Tree: Every node has either 0 or 2 children (no node has only 1 child).\n2. Complete Binary Tree: All levels are completely filled except possibly the last level, and all nodes in the last level are packed as far left as possible (essential for Binary Heaps!).\n3. Perfect Binary Tree: All interior nodes have 2 children and all leaf nodes are at the exact same depth.",
                    bulletPoints = listOf(
                        "Maximum nodes at level L: 2^L",
                        "Total nodes in perfect tree of height h: 2^(h + 1) - 1"
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "TreeNode Definition & Height Calculation",
                    language = "KOTLIN",
                    code = """class TreeNode<T>(
    val value: T,
    var left: TreeNode<T>? = null,
    var right: TreeNode<T>? = null
)

// Calculate tree height in O(N) time and O(h) space
fun <T> treeHeight(root: TreeNode<T>?): Int {
    if (root == null) return -1 // Height of empty tree is -1 (or 0 if counting nodes)
    val leftH = treeHeight(root.left)
    val rightH = treeHeight(root.right)
    return maxOf(leftH, rightH) + 1
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 1-5", "TreeNode holds a value and two nullable child references (left and right)."),
                        CodeStepExplanation(2, "Lines 8-13", "Recursive post-order height calculation: compute left and right child heights, then take max + 1.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Binary trees constrain each node to at most two children (left and right).",
                "Full trees have 0 or 2 children per node; complete trees fill left-to-right.",
                "Height of a balanced tree is O(log N); height of a degenerate skewed tree is O(N).",
                "Forms the structural foundation for BSTs, Heaps, AVL trees, and Tries."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_bin_1",
                    question = "What is the maximum number of nodes possible at level L (where root is level 0) in a binary tree?",
                    options = listOf("2^L", "2L", "L^2", "log2(L)"),
                    correctOptionIndex = 0,
                    explanation = "Because each node can branch into at most 2 children, the number of potential nodes doubles at each depth level: Level 0 = 1, Level 1 = 2, Level 2 = 4, Level L = 2^L."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_bin_2",
                    question = "What defines a 'Complete Binary Tree'?",
                    options = listOf(
                        "All levels are completely filled except possibly the last level, where nodes are positioned as far left as possible",
                        "Every node has exactly 2 children",
                        "All leaves have identical values",
                        "The tree has zero edges"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A complete binary tree fills all levels completely except possibly the lowest, which must be packed contiguously from left to right without gaps."
                )
            )
        ),

        // 2. Binary Search Tree (BST)
        DsaLessonContent(
            lessonId = "tree_bst_l1",
            subtopicId = "tree_bst",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Binary Search Tree (BST)",
            subtitle = "The fundamental BST invariant: left < node < right for logarithmic searching",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(h) = O(log N) balanced, O(N) worst",
            spaceComplexity = "O(h)",
            learningObjectives = listOf(
                "Master the foundational BST Invariant: all left descendants < node < all right descendants.",
                "Implement search(), insert(), and delete() operations in O(h) time.",
                "Understand In-Order Successor / Predecessor for two-child node deletions.",
                "Observe Degenerate (Skewed) BSTs and understand why self-balancing trees are needed."
            ),
            sections = listOf(
                LessonSection(
                    id = "bst_invariant",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Binary Search Tree Invariant",
                    body = "A Binary Search Tree (BST) is a binary tree where every node satisfies a strict mathematical ordering invariant:\n\n1. All nodes in the LEFT subtree have values STRICTLY LESS THAN the node's value.\n2. All nodes in the RIGHT subtree have values STRICTLY GREATER THAN the node's value.\n3. Both left and right subtrees must themselves also be valid Binary Search Trees.\n\nThis ordering property allows discarding half of the remaining tree with every comparison, mirroring Binary Search!",
                    bulletPoints = listOf(
                        "Balanced Tree: Height h = O(log N) -> Operations take O(log N) time.",
                        "Skewed Tree: Height h = O(N) -> Operations degrade to O(N) linear time."
                    )
                ),
                LessonSection(
                    id = "bst_deletion_cases",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Three Cases of BST Node Deletion",
                    body = "Deleting node X in a BST involves three structural possibilities:\n\n- Case 1 (Leaf Node): Node has 0 children -> Simply unlink it.\n- Case 2 (Single Child): Node has 1 child -> Bypass node by connecting its parent directly to its child.\n- Case 3 (Two Children): Node has 2 children -> Find node's In-Order Successor (smallest node in the right subtree), copy its value into X, and recursively delete the successor node from the right subtree!",
                    bulletPoints = listOf(
                        "In-Order Successor is guaranteed to have at most 1 child, making its removal trivial."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "BST Search & Insertion in Kotlin",
                    language = "KOTLIN",
                    code = """class BstNode(val value: Int, var left: BstNode? = null, var right: BstNode? = null)

// O(h) Search: halving search space at each comparison
fun searchBst(root: BstNode?, target: Int): BstNode? {
    if (root == null || root.value == target) return root
    return if (target < root.value) {
        searchBst(root.left, target)
    } else {
        searchBst(root.right, target)
    }
}

// O(h) Insert: preserves BST invariant
fun insertBst(root: BstNode?, value: Int): BstNode {
    if (root == null) return BstNode(value)
    if (value < root.value) {
        root.left = insertBst(root.left, value)
    } else if (value > root.value) {
        root.right = insertBst(root.right, value)
    }
    return root
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 4-11", "searchBst compares target to root; branches left if smaller and right if larger."),
                        CodeStepExplanation(2, "Lines 14-22", "insertBst recurses down to an empty leaf slot and anchors the new node while returning updated subtree.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "BST invariant: every left descendant < node < every right descendant.",
                "Search, insert, and delete take time proportional to tree height O(h).",
                "When balanced, height is O(log N); if inserted in sorted order, height degrades to O(N).",
                "Deleting a node with two children replaces it with its in-order successor."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_bst_1",
                    question = "What is the worst-case time complexity of searching in an unbalanced (skewed) Binary Search Tree with N elements?",
                    options = listOf("O(N) Linear Time", "O(log N)", "O(1)", "O(N log N)"),
                    correctOptionIndex = 0,
                    explanation = "If elements are inserted in already-sorted order (e.g. 1, 2, 3, 4, 5), each node branches only to the right, forming a degenerate linked list of height N with O(N) search time."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_bst_2",
                    question = "Where is the In-Order Successor of a node X located in a Binary Search Tree?",
                    options = listOf(
                        "The leftmost node of X's right subtree (the smallest value greater than X)",
                        "The root of the tree",
                        "The rightmost node of X's left subtree",
                        "X's immediate parent"
                    ),
                    correctOptionIndex = 0,
                    explanation = "The in-order successor is the next largest value in sorted sequence, which is found by moving one step right from X, then walking left as far as possible."
                )
            )
        ),

        // 3. Tree Traversals Overview
        DsaLessonContent(
            lessonId = "tree_traversal_l1",
            subtopicId = "tree_traversal",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Tree Traversals Overview",
            subtitle = "Depth-First Search (DFS) vs. Breadth-First Search (BFS) level-order exploration",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(W) BFS queue, O(h) DFS stack",
            learningObjectives = listOf(
                "Differentiate between Depth-First Search (DFS) and Breadth-First Search (BFS).",
                "Implement Level-Order Traversal using a FIFO Queue.",
                "Compare memory consumption: BFS tracks tree width W vs DFS tracks tree height h.",
                "Understand where each traversal is optimal."
            ),
            sections = listOf(
                LessonSection(
                    id = "traversals_overview",
                    type = LessonSectionType.INTRODUCTION,
                    title = "DFS vs. BFS Strategies",
                    body = "Tree traversal is the process of visiting every node in a tree exactly once. There are two overarching strategies:\n\n1. Depth-First Search (DFS): Explores as deep as possible along each branch before backtracking. Subdivided into Inorder, Preorder, and Postorder. Uses a Stack (call stack).\n\n2. Breadth-First Search (BFS) / Level-Order: Explores the tree level by level, visiting all nodes at depth d before proceeding to depth d + 1. Uses a FIFO Queue.",
                    bulletPoints = listOf(
                        "DFS Space: O(h) where h is tree height.",
                        "BFS Space: O(W) where W is maximum tree width (up to N / 2 nodes at leaf level!)."
                    )
                ),
                LessonSection(
                    id = "traversal_tradeoffs",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Choosing Between DFS and BFS",
                    body = "The choice between DFS and BFS depends on the problem structure:\n\n- Use BFS when finding the shortest path or closest node to the root (in an unweighted tree/graph), or when processing nodes level by level.\n- Use DFS when exploring all paths, checking connectivity, evaluating expressions, or when memory is constrained on wide, shallow trees (DFS uses O(h) memory vs BFS O(W)).",
                    bulletPoints = listOf(
                        "Deep, narrow trees: BFS consumes O(W) which is small; DFS call stack depth O(h) can be large.",
                        "Wide, shallow trees: DFS consumes O(h) which is small; BFS queue O(W) can hold up to N/2 nodes."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Level-Order Traversal (BFS) Using a Queue",
                    language = "KOTLIN",
                    code = """class TreeNode(val value: Int, var left: TreeNode? = null, var right: TreeNode? = null)

fun levelOrderTraversal(root: TreeNode?): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    if (root == null) return result
    
    val queue = ArrayDeque<TreeNode>()
    queue.addLast(root)
    
    while (queue.isNotEmpty()) {
        val levelSize = queue.size
        val currentLevel = mutableListOf<Int>()
        
        repeat(levelSize) {
            val node = queue.removeFirst()
            currentLevel.add(node.value)
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }
        result.add(currentLevel)
    }
    return result
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 7-8", "Initialize queue with root node."),
                        CodeStepExplanation(2, "Line 11", "Snapshot current queue size to process all nodes of current level in batch."),
                        CodeStepExplanation(3, "Lines 16-18", "Pop each node and enqueue its left and right children for the next level.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "DFS explores vertically down branches; BFS explores horizontally across levels.",
                "Level-order traversal requires a FIFO queue.",
                "All tree traversals visit every node once: O(N) time complexity.",
                "DFS memory is bounded by tree height O(h); BFS memory is bounded by tree width O(W)."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_trv_1",
                    question = "Which data structure is required to implement Breadth-First Search (Level-Order) tree traversal?",
                    options = listOf("FIFO Queue", "LIFO Stack", "Binary Heap", "Hash Table"),
                    correctOptionIndex = 0,
                    explanation = "BFS visits nodes in strict arrival sequence across depth levels, requiring a FIFO Queue to enqueue child nodes and process them in order."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_trv_2",
                    question = "In a completely balanced binary tree with N nodes, what is the maximum number of nodes stored in the BFS queue at the leaf level?",
                    options = listOf("Approximately N / 2 nodes", "log2(N) nodes", "N^2 nodes", "1 node"),
                    correctOptionIndex = 0,
                    explanation = "In a balanced binary tree, the final leaf level contains roughly half of all nodes in the entire tree (N / 2), which represents the maximum width W stored in the queue."
                )
            )
        ),

        // 4. Inorder Traversal (LNR)
        DsaLessonContent(
            lessonId = "tree_inorder_l1",
            subtopicId = "tree_inorder",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Inorder Traversal (LNR)",
            subtitle = "Left-Node-Right recursion yielding strictly sorted values on Binary Search Trees",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(h) stack",
            learningObjectives = listOf(
                "Master the Left-Node-Right (LNR) recursive visitation sequence.",
                "Prove why Inorder traversal on a BST produces strictly ascending sorted order.",
                "Implement iterative Inorder traversal using an explicit Stack.",
                "Use Inorder to validate BST invariants (isSorted verification)."
            ),
            sections = listOf(
                LessonSection(
                    id = "inorder_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The LNR Sequence",
                    body = "Inorder Traversal visits nodes in Left-Node-Right order:\n1. Recursively traverse the entire LEFT subtree.\n2. Process the current NODE (visit/print/record).\n3. Recursively traverse the entire RIGHT subtree.\n\nBecause the left subtree holds all values smaller than the current node, and the right subtree holds all values larger, performing an Inorder traversal on a Binary Search Tree produces all elements in strictly ASCENDING SORTED ORDER!",
                    bulletPoints = listOf(
                        "LNR: Left -> Node -> Right.",
                        "BST Invariant: Inorder traversal of any BST yields a sorted sequence."
                    )
                ),
                LessonSection(
                    id = "inorder_applications",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "BST Validation & Inorder Successor",
                    body = "Because Inorder traversal produces values in strictly non-decreasing sequence on a BST, it provides a clean O(N) verification algorithm:\n\nAs you traverse in LNR order, maintain a variable prevVal. If the current node value is <= prevVal, the tree violates the BST invariant.\n\nFurthermore, finding the Inorder Successor of any node with a right subtree simply requires traversing one step right and then following left pointers to the leaf.",
                    bulletPoints = listOf(
                        "BST Invariant Check: curr.value > prev.value at every step.",
                        "Kth Smallest Element: Perform inorder traversal and stop at the kth visited node."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Recursive & Iterative Inorder Traversal in Kotlin",
                    language = "KOTLIN",
                    code = """class TreeNode(val value: Int, var left: TreeNode? = null, var right: TreeNode? = null)

// 1. Recursive Inorder Traversal
fun inorderRecursive(root: TreeNode?, result: MutableList<Int> = mutableListOf()): List<Int> {
    if (root == null) return result
    inorderRecursive(root.left, result)  // L
    result.add(root.value)              // N
    inorderRecursive(root.right, result) // R
    return result
}

// 2. Iterative Inorder Traversal with explicit Stack
fun inorderIterative(root: TreeNode?): List<Int> {
    val result = mutableListOf<Int>()
    val stack = ArrayDeque<TreeNode>()
    var curr = root
    
    while (curr != null || stack.isNotEmpty()) {
        while (curr != null) {
            stack.addLast(curr) // Go left as far as possible
            curr = curr.left
        }
        curr = stack.removeLast()
        result.add(curr.value) // Process node
        curr = curr.right      // Switch to right subtree
    }
    return result
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 5-11", "Recursive LNR: visit left child, record value, visit right child."),
                        CodeStepExplanation(2, "Lines 18-26", "Iterative version pushes nodes while walking left, pops top node, records it, and moves right.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Inorder traversal visits in Left -> Node -> Right (LNR) order.",
                "Running Inorder traversal on a BST outputs values in ascending sorted order.",
                "Time complexity is O(N) as each node is visited once.",
                "Auxiliary space is bounded by tree height O(h) for the call stack."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_in_1",
                    question = "What is the result of performing an Inorder traversal on a valid Binary Search Tree?",
                    options = listOf(
                        "Elements are produced in strictly ascending sorted order",
                        "Elements are produced in reverse sorted order",
                        "Root is always the very first element visited",
                        "Leaves are visited before internal nodes"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because a BST guarantees Left < Node < Right, visiting Left, then Node, then Right mathematically guarantees all values appear in ascending numerical order."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_in_2",
                    question = "In the iterative Inorder traversal algorithm, what is the purpose of the inner while loop?",
                    options = listOf(
                        "To push all left descendants onto the stack until reaching the leftmost node",
                        "To sort the right subtree",
                        "To delete leaf nodes",
                        "To compute tree depth"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Inorder must process the smallest available node first; the inner loop pushes nodes onto the stack while traversing left until hitting null, so the top of the stack holds the leftmost node."
                )
            )
        ),

        // 5. Preorder Traversal (NLR)
        DsaLessonContent(
            lessonId = "tree_preorder_l1",
            subtopicId = "tree_preorder",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Preorder Traversal (NLR)",
            subtitle = "Node-Left-Right sequence for tree cloning, prefix expressions, and serialization",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(h) stack",
            learningObjectives = listOf(
                "Master the Node-Left-Right (NLR) visitation sequence.",
                "Use Preorder traversal to serialize trees into flat strings or lists.",
                "Clone/duplicate trees using top-down Preorder copying.",
                "Evaluate Prefix expressions (Polish notation)."
            ),
            sections = listOf(
                LessonSection(
                    id = "preorder_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The NLR Sequence",
                    body = "Preorder Traversal visits nodes in Node-Left-Right order:\n1. Process the current NODE first.\n2. Recursively traverse the LEFT subtree.\n3. Recursively traverse the RIGHT subtree.\n\nBecause the parent node is processed before any of its children, Preorder is inherently top-down. The root of the tree is ALWAYS the first element in a Preorder traversal!",
                    bulletPoints = listOf(
                        "NLR: Node -> Left -> Right.",
                        "First Element: The very first element in any preorder traversal is guaranteed to be the tree root."
                    )
                ),
                LessonSection(
                    id = "preorder_use_cases",
                    type = LessonSectionType.USE_CASES,
                    title = "Applications of Preorder Traversal",
                    body = "- Tree Serialization: Storing a tree structure to disk or JSON and deserializing it later.\n- Tree Duplication / Cloning: Making an exact copy of a tree requires creating the root first before attaching cloned children.\n- Prefix Expression Trees: Representing mathematical operators before operands (`+ A B`).",
                    bulletPoints = listOf(
                        "Time: O(N) visits every node once.",
                        "Space: O(h) call stack depth."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Preorder Traversal in Kotlin",
                    language = "KOTLIN",
                    code = """class TreeNode(val value: Int, var left: TreeNode? = null, var right: TreeNode? = null)

fun preorderTraversal(root: TreeNode?, result: MutableList<Int> = mutableListOf()): List<Int> {
    if (root == null) return result
    result.add(root.value)                // N: Process current node first
    preorderTraversal(root.left, result)  // L: Traverse left subtree
    preorderTraversal(root.right, result) // R: Traverse right subtree
    return result
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 5", "Record current node's value first before traversing children."),
                        CodeStepExplanation(2, "Lines 6-7", "Recurse into left child followed by right child.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Preorder visits in Node -> Left -> Right (NLR) sequence.",
                "The root of the tree is always the first element visited.",
                "Essential for tree cloning, JSON/file serialization, and prefix expression evaluation.",
                "Operates in O(N) time with O(h) stack memory."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_pre_1",
                    question = "Where is the root node located in a Preorder traversal output?",
                    options = listOf(
                        "At index 0 (the very first element)",
                        "At the exact middle index",
                        "At the last index",
                        "It depends on whether the tree is balanced"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because Preorder processes the Node before traversing Left and Right subtrees (NLR), the root of the tree is always evaluated and output first."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_pre_2",
                    question = "Why is Preorder traversal preferred when cloning or duplicating a binary tree?",
                    options = listOf(
                        "You must create the parent node before you can attach left and right child nodes to it",
                        "Because it sorts the nodes automatically",
                        "Because it consumes O(1) memory",
                        "Because duplicate nodes can only be copied from leaves"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A child node cannot be attached to a parent that does not yet exist; Preorder creates the parent node first (top-down), then recursively constructs and connects the children."
                )
            )
        ),

        // 6. Postorder Traversal (LRN)
        DsaLessonContent(
            lessonId = "tree_postorder_l1",
            subtopicId = "tree_postorder",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Postorder Traversal (LRN)",
            subtitle = "Left-Right-Node bottom-up processing for tree deletion, subtree metrics, and Postfix evaluation",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(h) stack",
            learningObjectives = listOf(
                "Master the Left-Right-Node (LRN) visitation sequence.",
                "Understand why bottom-up tree operations (calculating subtree size, height) require Postorder.",
                "Safely delete/deallocate trees from leaves up to root.",
                "Evaluate Postfix mathematical syntax trees."
            ),
            sections = listOf(
                LessonSection(
                    id = "postorder_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The LRN Sequence",
                    body = "Postorder Traversal visits nodes in Left-Right-Node order:\n1. Recursively traverse the LEFT subtree.\n2. Recursively traverse the RIGHT subtree.\n3. Process the current NODE last.\n\nBecause both children are fully processed before the parent is touched, Postorder is inherently bottom-up. The root of the tree is ALWAYS the final element visited!",
                    bulletPoints = listOf(
                        "LRN: Left -> Right -> Node.",
                        "Bottom-Up: Information bubbles up from leaf nodes to root."
                    )
                ),
                LessonSection(
                    id = "postorder_use_cases",
                    type = LessonSectionType.USE_CASES,
                    title = "Why Bottom-Up Processing Requires Postorder",
                    body = "- Deleting Trees: In C/C++, you must deallocate left and right children before freeing the parent node to prevent dangling pointers.\n- Directory Size Calculation: To find the total disk space of a folder, you must sum the sizes of all sub-folders and files first before outputting the folder's total size.\n- Lowest Common Ancestor (LCA): Subtree results must be gathered from both branches before root decides LCA.",
                    bulletPoints = listOf(
                        "Time: O(N) visits each node once.",
                        "Space: O(h) recursion stack."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Postorder Traversal in Kotlin",
                    language = "KOTLIN",
                    code = """class TreeNode(val value: Int, var left: TreeNode? = null, var right: TreeNode? = null)

fun postorderTraversal(root: TreeNode?, result: MutableList<Int> = mutableListOf()): List<Int> {
    if (root == null) return result
    postorderTraversal(root.left, result)  // L: Traverse left subtree
    postorderTraversal(root.right, result) // R: Traverse right subtree
    result.add(root.value)                 // N: Process current node last
    return result
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 5-6", "Recurse into left and right subtrees completely before inspecting current node."),
                        CodeStepExplanation(2, "Line 7", "Process current node only after both children are processed (bottom-up).")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Postorder visits in Left -> Right -> Node (LRN) order.",
                "The root of the tree is always the final element processed.",
                "Mandatory for bottom-up computations (subtree size, directory disk space, tree height).",
                "Enables safe leaf-to-root memory deallocation."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_post_1",
                    question = "Why is Postorder traversal used when computing the total disk space of a folder directory tree?",
                    options = listOf(
                        "You must calculate the sizes of all sub-files and sub-folders before you can know the parent folder's total size",
                        "Because postorder runs in O(1) time",
                        "Because folders are sorted alphabetically",
                        "Because postorder skips hidden files"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Calculating total directory size is a bottom-up problem: you cannot report a directory's size until all of its sub-directories and files have been completely summed."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_post_2",
                    question = "Where is the root node located in a Postorder traversal sequence?",
                    options = listOf(
                        "At the very last index of the output",
                        "At index 0",
                        "At the midpoint",
                        "It is omitted entirely"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because Postorder traverses both Left and Right subtrees before processing the parent Node (LRN), the tree root is the last element recorded."
                )
            )
        ),

        // 7. AVL Tree & Self-Balancing Trees
        DsaLessonContent(
            lessonId = "tree_avl_l1",
            subtopicId = "tree_avl",
            lessonNumber = 1,
            title = "Core Theory & Invariants – AVL Tree & Self-Balancing Trees",
            subtitle = "Strict height-balanced invariants and the 4 tree rotation cases (LL, RR, LR, RL)",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(log N) guaranteed in all cases",
            spaceComplexity = "O(log N) stack",
            learningObjectives = listOf(
                "Understand the Balance Factor invariant: balance = height(left) - height(right) in {-1, 0, 1}.",
                "Master the four rotation cases: Left-Left (LL), Right-Right (RR), Left-Right (LR), Right-Left (RL).",
                "Learn why tree rotations execute in strictly O(1) pointer updates.",
                "Compare AVL Trees (strictly balanced, faster lookups) against Red-Black Trees (fewer rotations, faster inserts)."
            ),
            sections = listOf(
                LessonSection(
                    id = "avl_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Why Standard BSTs Fail at Scale",
                    body = "Standard Binary Search Trees have a major flaw: if data is inserted in sorted order (1, 2, 3, 4, 5), the tree degrades into a straight linked list of height N. Search time collapses from O(log N) to O(N)!\n\nInvented in 1962 by Georgy Adelson-Velsky and Evgenii Landis, the AVL Tree was the world's first self-balancing BST. It strictly enforces that the height difference between left and right subtrees of ANY node never exceeds 1.",
                    bulletPoints = listOf(
                        "Balance Factor (BF): `height(left) - height(right)`",
                        "AVL Invariant: For every node, `BF in {-1, 0, 1}`."
                    )
                ),
                LessonSection(
                    id = "avl_rotations",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The 4 Rotation Cases",
                    body = "When an insertion causes a node's Balance Factor to become +2 or -2, the tree rebalances using O(1) pointer rotations:\n\n1. Left-Left (LL) Heavy: Node BF = +2, left child BF >= 0 -> Right Rotation.\n2. Right-Right (RR) Heavy: Node BF = -2, right child BF <= 0 -> Left Rotation.\n3. Left-Right (LR) Heavy: Node BF = +2, left child BF = -1 -> Left Rotate left child, then Right Rotate node.\n4. Right-Left (RL) Heavy: Node BF = -2, right child BF = +1 -> Right Rotate right child, then Left Rotate node.",
                    bulletPoints = listOf(
                        "Rotations preserve BST ordering while reducing subtree height by 1 in O(1) time."
                    ),
                    highlightNote = "AVL trees guarantee strictly O(log N) search, insertion, and deletion under all conditions."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "AVL Tree Left and Right Rotations in Kotlin",
                    language = "KOTLIN",
                    code = """class AvlNode(
    val value: Int,
    var left: AvlNode? = null,
    var right: AvlNode? = null,
    var height: Int = 1
)

// O(1) Right Rotation (fixes LL imbalance)
fun rightRotate(y: AvlNode): AvlNode {
    val x = y.left!!
    val t2 = x.right
    
    // Perform rotation
    x.right = y
    y.left = t2
    
    // Update heights
    y.height = maxOf(height(y.left), height(y.right)) + 1
    x.height = maxOf(height(x.left), height(x.right)) + 1
    
    return x // New root of rotated subtree
}

// O(1) Left Rotation (fixes RR imbalance)
fun leftRotate(x: AvlNode): AvlNode {
    val y = x.right!!
    val t2 = y.left
    
    // Perform rotation
    y.left = x
    x.right = t2
    
    // Update heights
    x.height = maxOf(height(x.left), height(x.right)) + 1
    y.height = maxOf(height(y.left), height(y.right)) + 1
    
    return y // New root of rotated subtree
}

private fun height(node: AvlNode?): Int = node?.height ?: 0""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-20", "Right rotation pivots child x above y and reconnects middle subtree t2 to y.left in O(1)."),
                        CodeStepExplanation(2, "Lines 23-35", "Left rotation pivots child y above x and reconnects middle subtree t2 to x.right in O(1)."),
                        CodeStepExplanation(3, "Lines 17-18", "Recalculate heights for rotated nodes from their new children.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "AVL trees maintain strict balance by ensuring |height(left) - height(right)| <= 1 at every node.",
                "Guarantees O(log N) search, insert, and delete operations in worst case.",
                "Rebalancing uses 4 rotation cases (LL, RR, LR, RL) that execute in O(1) pointer updates.",
                "AVL trees are strictly more balanced than Red-Black trees, making lookups faster."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_tree_avl_1",
                    question = "What is the permissible range for a node's Balance Factor in a valid AVL Tree?",
                    options = listOf("-1, 0, or +1", "-2, -1, 0, +1, or +2", "Strictly 0", "Any positive integer"),
                    correctOptionIndex = 0,
                    explanation = "An AVL tree requires that the height difference between left and right subtrees never exceeds 1, meaning Balance Factor must be strictly within {-1, 0, 1}."
                ),
                LessonPracticeQuestion(
                    id = "pq_tree_avl_2",
                    question = "Which rotation sequence resolves a Left-Right (LR) imbalance where a node has BF = +2 and its left child has BF = -1?",
                    options = listOf(
                        "Left rotate the left child, then Right rotate the parent node",
                        "Single right rotation on the parent",
                        "Single left rotation on the parent",
                        "Double right rotation"
                    ),
                    correctOptionIndex = 0,
                    explanation = "An LR imbalance is a zig-zag shape. It is resolved by first left-rotating the left child to straighten it into an LL line, followed by a right rotation on the parent node."
                )
            )
        )
    )
}
