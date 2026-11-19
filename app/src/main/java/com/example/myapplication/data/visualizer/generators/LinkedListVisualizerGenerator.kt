package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.model.visualizer.VizListNode

object LinkedListVisualizerGenerator {

    // 1. Singly Linked List Traversal
    fun generateSinglyLinkedList(customInput: String?): List<DsaVisualizerStep> {
        val initialValues = listOf("10", "20", "30", "40", "50")
        val steps = mutableListOf<DsaVisualizerStep>()
        val total = initialValues.size + 2

        val baseNodes = initialValues.mapIndexed { idx, v ->
            VizListNode(
                id = "node_$idx",
                value = v,
                label = if (idx == 0) "head" else if (idx == initialValues.size - 1) "tail" else null,
                isHead = idx == 0,
                isTail = idx == initialValues.size - 1
            )
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = total,
                title = "Singly Linked List: Head at [10]",
                description = "Each node contains data and a 'next' pointer referencing the successor node. Terminated by null.",
                actionType = "INITIALIZE",
                variables = mapOf("head" to baseNodes[0].value, "length" to "${baseNodes.size}"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = baseNodes,
                pointerLabels = mapOf(0 to "head"),
                highlightedIndices = setOf(0)
            )
        )

        baseNodes.forEachIndexed { idx, node ->
            val updatedNodes = baseNodes.mapIndexed { i, n ->
                n.copy(isHighlighted = i == idx)
            }
            steps.add(
                DsaVisualizerStep(
                    stepIndex = idx + 1,
                    totalSteps = total,
                    title = "Visit Node [$idx]: Value = ${node.value}",
                    description = "Reading curr.data = ${node.value}. Follow pointer curr = curr.next.",
                    actionType = "TRAVERSE",
                    variables = mapOf("curr" to node.value, "next" to (baseNodes.getOrNull(idx + 1)?.value ?: "null")),
                    vizType = DsaVizType.LINKED_LIST,
                    listNodes = updatedNodes,
                    pointerLabels = mapOf(idx to "curr"),
                    highlightedIndices = setOf(idx)
                )
            )
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = total - 1,
                totalSteps = total,
                title = "Reached End of List (curr.next == null)",
                description = "Traversed all ${baseNodes.size} nodes sequentially in linear O(N) time.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "COMPLETED", "nodesVisited" to "${baseNodes.size}"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = baseNodes.map { it.copy(isHighlighted = true) },
                isCompleted = true,
                summaryResult = "Singly linked list traversal complete in O(N) time."
            )
        )

        return steps
    }

    // 2. Doubly Linked List
    fun generateDoublyLinkedList(customInput: String?): List<DsaVisualizerStep> {
        val values = listOf("A", "B", "C", "D")
        val steps = mutableListOf<DsaVisualizerStep>()
        val nodes = values.mapIndexed { idx, v ->
            VizListNode(
                id = "dnode_$idx",
                value = v,
                label = if (idx == 0) "head" else if (idx == values.size - 1) "tail" else null,
                isHead = idx == 0,
                isTail = idx == values.size - 1
            )
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Doubly Linked List: Bidirectional Pointers",
                description = "Every node contains two pointers: 'next' (forward link) and 'prev' (backward link).",
                actionType = "INITIALIZE",
                variables = mapOf("head" to nodes[0].value, "tail" to nodes.last().value),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes,
                isDoublyLinkedList = true,
                pointerLabels = mapOf(0 to "head", nodes.size - 1 to "tail"),
                highlightedIndices = setOf(0)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Forward Traversal: Head -> Tail",
                description = "Traversing forward: A -> B -> C -> D using node.next pointers.",
                actionType = "FORWARD",
                variables = mapOf("curr" to "B", "prev" to "A", "next" to "C"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes.mapIndexed { idx, n -> n.copy(isHighlighted = idx == 1) },
                isDoublyLinkedList = true,
                pointerLabels = mapOf(1 to "curr"),
                highlightedIndices = setOf(1)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Backward Traversal: Tail -> Head",
                description = "Traversing in reverse: D -> C -> B -> A using node.prev pointers without reversing list.",
                actionType = "BACKWARD",
                variables = mapOf("curr" to "C", "next" to "D", "prev" to "B"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes.mapIndexed { idx, n -> n.copy(isHighlighted = idx == 2) },
                isDoublyLinkedList = true,
                pointerLabels = mapOf(2 to "curr"),
                highlightedIndices = setOf(2)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 4,
                title = "Doubly Linked List Verified",
                description = "Bidirectional traversal enables O(1) node deletion when node reference is given.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SUCCESS"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes.map { it.copy(isHighlighted = true) },
                isDoublyLinkedList = true,
                isCompleted = true,
                summaryResult = "Doubly linked list allows bidirectional traversal."
            )
        )

        return steps
    }

    // 3. Circular Linked List
    fun generateCircularLinkedList(customInput: String?): List<DsaVisualizerStep> {
        val values = listOf("1", "2", "3", "4")
        val nodes = values.mapIndexed { idx, v ->
            VizListNode(
                id = "cnode_$idx",
                value = v,
                isHead = idx == 0,
                isTail = idx == values.size - 1
            )
        }
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Circular Linked List: Tail Points to Head",
                description = "The last node's 'next' pointer points back to Head instead of null, creating a continuous loop.",
                actionType = "INITIALIZE",
                variables = mapOf("tail.next" to "head", "head" to nodes[0].value),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes,
                isCircularLinkedList = true,
                pointerLabels = mapOf(0 to "head", nodes.size - 1 to "tail"),
                highlightedIndices = setOf(0, nodes.size - 1)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Loop Around: From Tail [4] -> Head [1]",
                description = "Traversing past tail wraps directly back to the head node without encountering null.",
                actionType = "WRAP",
                variables = mapOf("from" to nodes.last().value, "to" to nodes.first().value),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes.mapIndexed { i, n -> n.copy(isHighlighted = i == 0 || i == nodes.size - 1) },
                isCircularLinkedList = true,
                pointerLabels = mapOf(0 to "head", nodes.size - 1 to "tail"),
                highlightedIndices = setOf(0, nodes.size - 1)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Circular Linked List Complete",
                description = "Ideal for round-robin scheduling and circular media playlists.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SUCCESS"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = nodes.map { it.copy(isHighlighted = true) },
                isCircularLinkedList = true,
                isCompleted = true,
                summaryResult = "Circular linked list verified."
            )
        )

        return steps
    }

    // 4. Insertion and Deletion in Linked List
    fun generateInsertionDeletion(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val initialNodes = listOf(
            VizListNode("n0", "10", isHead = true),
            VizListNode("n1", "20"),
            VizListNode("n2", "30", isTail = true)
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Initial Chain: 10 -> 20 -> 30",
                description = "Insert new node 25 between 20 and 30.",
                actionType = "INITIALIZE",
                variables = mapOf("insert" to "25", "after" to "20"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = initialNodes,
                pointerLabels = mapOf(1 to "prevNode")
            )
        )

        // Insert step
        val insertedNodes = listOf(
            VizListNode("n0", "10", isHead = true),
            VizListNode("n1", "20"),
            VizListNode("n_new", "25", isNew = true, isHighlighted = true),
            VizListNode("n2", "30", isTail = true)
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Pointer Rewiring: Insert 25",
                description = "newNode.next = prev.next (30); prev.next = newNode (25). In-place O(1) pointer updates.",
                actionType = "INSERT",
                variables = mapOf("newNode.next" to "30", "prev.next" to "25"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = insertedNodes,
                pointerLabels = mapOf(2 to "newNode"),
                highlightedIndices = setOf(2)
            )
        )

        // Delete step
        val deletedNodes = listOf(
            VizListNode("n0", "10", isHead = true),
            VizListNode("n_new", "25", isHighlighted = true),
            VizListNode("n2", "30", isTail = true)
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Delete Node 20: Bypass Pointer",
                description = "prev.next = target.next. Bypasses 20 directly to 25. Node 20 is garbage-collected.",
                actionType = "DELETE",
                variables = mapOf("unlinked" to "20"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = deletedNodes,
                highlightedIndices = setOf(0, 1),
                isCompleted = true,
                summaryResult = "Node inserted and deleted with constant O(1) pointer manipulations."
            )
        )

        return steps
    }

    // 5. Linked List Reversal
    fun generateLinkedListReversal(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val initialNodes = listOf(
            VizListNode("n1", "1", isHead = true),
            VizListNode("n2", "2"),
            VizListNode("n3", "3"),
            VizListNode("n4", "4", isTail = true)
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "In-Place Reversal: Three Pointers (prev, curr, next)",
                description = "prev = null, curr = head (1), next = null. Flip curr.next pointer backwards iteratively.",
                actionType = "INITIALIZE",
                variables = mapOf("prev" to "null", "curr" to "1"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = initialNodes,
                pointerLabels = mapOf(0 to "curr"),
                highlightedIndices = setOf(0)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Flip First Pointer: 1.next = null",
                description = "next = curr.next; curr.next = prev; prev = curr; curr = next.",
                actionType = "REVERSE_POINTER",
                variables = mapOf("prev" to "1", "curr" to "2"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = initialNodes.mapIndexed { i, n -> n.copy(isHighlighted = i == 0) },
                pointerLabels = mapOf(0 to "prev", 1 to "curr"),
                highlightedIndices = setOf(0, 1)
            )
        )

        val reversedNodes = listOf(
            VizListNode("n4", "4", isHead = true, isHighlighted = true),
            VizListNode("n3", "3"),
            VizListNode("n2", "2"),
            VizListNode("n1", "1", isTail = true)
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Reversal Complete: New Head = 4",
                description = "All arrows reversed: 4 -> 3 -> 2 -> 1 -> null in O(N) time and O(1) memory.",
                actionType = "COMPLETE",
                variables = mapOf("newHead" to "4", "newTail" to "1"),
                vizType = DsaVizType.LINKED_LIST,
                listNodes = reversedNodes,
                pointerLabels = mapOf(0 to "newHead"),
                isCompleted = true,
                summaryResult = "Linked list reversed in O(N) time with O(1) space."
            )
        )

        return steps
    }
}
