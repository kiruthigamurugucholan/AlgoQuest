package com.example.myapplication.data.model.visualizer

/**
 * Supported visualization paradigm types across the 65 DSA catalogue topics.
 */
enum class DsaVizType {
    ARRAY,              // Linear array elements with pointers (L, R, mid, j, etc.)
    SEARCH,             // Searching with target, boundaries, mid, comparisons
    SORT,               // Sorting with comparisons, swaps, sorted regions
    STRING,             // String manipulation, character traversal, palindrome, anagram
    LINKED_LIST,        // Node chains (values, next/prev pointers, head, curr, tail)
    STACK,              // Vertical stack (bottom to top, top pointer, push/pop/peek)
    QUEUE,              // FIFO queue / circular ring buffer (front, rear, wrap)
    TREE,               // Tree node hierarchy (root, left, right, traversals, BST)
    GRAPH,              // Graph network (vertices, edges, visited set, queue/stack)
    RECURSION,          // Call stack frames (depth, function call, parameters, returns)
    DP_TABLE,           // 1D / 2D table grid (row, col, active cell, state equation)
    GREEDY,             // Candidate pool (items with weight/value, chosen vs rejected)
    FLOWCHART           // Execution state machine, variables table, asymptotic curves
}

/**
 * Linked list node representation for visualization.
 */
data class VizListNode(
    val id: String,
    val value: String,
    val label: String? = null,
    val isHighlighted: Boolean = false,
    val isHead: Boolean = false,
    val isTail: Boolean = false,
    val isDeleted: Boolean = false,
    val isNew: Boolean = false
)

/**
 * Tree node representation for visualization.
 */
data class VizTreeNode(
    val id: String,
    val value: String,
    val leftChildId: String? = null,
    val rightChildId: String? = null,
    val depth: Int = 0,
    val xFraction: Float = 0.5f,
    val yFraction: Float = 0.5f,
    val isHighlighted: Boolean = false,
    val isVisited: Boolean = false,
    val annotation: String? = null
)

/**
 * Graph vertex for visual canvas layout.
 */
data class VizGraphVertex(
    val id: String,
    val label: String,
    val xFraction: Float,
    val yFraction: Float,
    val isHighlighted: Boolean = false,
    val isVisited: Boolean = false,
    val distance: String? = null
)

/**
 * Graph edge connecting two vertices.
 */
data class VizGraphEdge(
    val fromId: String,
    val toId: String,
    val weight: Int? = null,
    val isHighlighted: Boolean = false,
    val isMst: Boolean = false,
    val isDirected: Boolean = false
)

/**
 * Recursion call stack frame.
 */
data class VizCallFrame(
    val frameId: String,
    val functionName: String,
    val arguments: Map<String, String>,
    val returnValue: String? = null,
    val isReturning: Boolean = false,
    val depth: Int = 0
)

/**
 * Greedy decision candidate item.
 */
data class VizGreedyItem(
    val id: String,
    val label: String,
    val weight: String,
    val value: String,
    val ratio: String? = null,
    val status: String // "AVAILABLE", "EVALUATING", "SELECTED", "REJECTED"
)

/**
 * Complete, self-contained state representing one atomic step in an algorithm's execution.
 */
data class DsaVisualizerStep(
    val stepIndex: Int,
    val totalSteps: Int,
    val title: String,
    val description: String,
    val actionType: String = "PROCESS", // "COMPARE", "SWAP", "PUSH", "POP", "VISIT", "RECURSE", "UPDATE", "FOUND", "COMPLETE"
    val variables: Map<String, String> = emptyMap(),
    val vizType: DsaVizType = DsaVizType.ARRAY,

    // Array / Search / Sort / String payload
    val arrayElements: List<Int> = emptyList(),
    val stringCharacters: List<Char> = emptyList(),
    val pointerLabels: Map<Int, String> = emptyMap(),
    val highlightedIndices: Set<Int> = emptySet(),
    val sortedIndices: Set<Int> = emptySet(),
    val eliminatedIndices: Set<Int> = emptySet(),
    val swappedIndices: Pair<Int, Int>? = null,
    val targetValue: String? = null,
    val isFound: Boolean = false,

    // Linked List payload
    val listNodes: List<VizListNode> = emptyList(),
    val isDoublyLinkedList: Boolean = false,
    val isCircularLinkedList: Boolean = false,

    // Stack payload
    val stackItems: List<String> = emptyList(),
    val stackTopIndex: Int = -1,
    val stackCapacity: Int = 8,

    // Queue payload
    val queueItems: List<String> = emptyList(),
    val queueFrontIndex: Int = -1,
    val queueRearIndex: Int = -1,
    val isCircularQueue: Boolean = false,
    val queueCapacity: Int = 8,

    // Tree payload
    val treeNodes: List<VizTreeNode> = emptyList(),
    val treeTraversalOrder: List<String> = emptyList(),
    val activeTreeNodeId: String? = null,

    // Graph payload
    val graphVertices: List<VizGraphVertex> = emptyList(),
    val graphEdges: List<VizGraphEdge> = emptyList(),
    val activeVertexId: String? = null,
    val visitedVertexIds: Set<String> = emptySet(),
    val auxiliaryQueueOrStack: List<String> = emptyList(),

    // Recursion payload
    val callStack: List<VizCallFrame> = emptyList(),
    val maxRecursionDepth: Int = 5,
    val baseCaseReached: Boolean = false,

    // DP Table payload
    val dpHeadersX: List<String> = emptyList(),
    val dpHeadersY: List<String> = emptyList(),
    val dpGrid: List<List<String>> = emptyList(),
    val activeDpCell: Pair<Int, Int>? = null,
    val dependentDpCells: List<Pair<Int, Int>> = emptyList(),
    val dpFormula: String? = null,

    // Greedy payload
    val greedyItems: List<VizGreedyItem> = emptyList(),
    val greedyCapacityUsed: String? = null,
    val greedyTotalValue: String? = null,

    // Flowchart / Complexity payload
    val flowchartSteps: List<String> = emptyList(),
    val activeFlowchartStep: Int = -1,
    val codeLineNumber: Int = -1,
    val complexityCurveName: String? = null,

    // Completion
    val isCompleted: Boolean = false,
    val summaryResult: String? = null
)

/**
 * Definition of an interactive visualizer scenario for a DSA catalogue topic.
 */
data class DsaVisualizerScenario(
    val subtopicId: String,
    val title: String,
    val categoryName: String,
    val vizType: DsaVizType,
    val defaultInput: String,
    val supportsCustomInput: Boolean = true,
    val inputHelpText: String = "Comma-separated numbers or target",
    val timeComplexity: String = "O(N)",
    val spaceComplexity: String = "O(1)",
    val generateSteps: (customInput: String?) -> List<DsaVisualizerStep>
)
