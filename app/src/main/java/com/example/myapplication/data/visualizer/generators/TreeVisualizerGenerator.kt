package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.model.visualizer.VizTreeNode

object TreeVisualizerGenerator {

    // Standard base tree for traversals
    //         4
    //       /   \
    //      2     6
    //     / \   / \
    //    1   3 5   7
    private fun getSampleBstNodes(): List<VizTreeNode> {
        return listOf(
            VizTreeNode("n4", "4", leftChildId = "n2", rightChildId = "n6", depth = 0, xFraction = 0.5f, yFraction = 0.15f),
            VizTreeNode("n2", "2", leftChildId = "n1", rightChildId = "n3", depth = 1, xFraction = 0.25f, yFraction = 0.45f),
            VizTreeNode("n6", "6", leftChildId = "n5", rightChildId = "n7", depth = 1, xFraction = 0.75f, yFraction = 0.45f),
            VizTreeNode("n1", "1", depth = 2, xFraction = 0.12f, yFraction = 0.8f),
            VizTreeNode("n3", "3", depth = 2, xFraction = 0.38f, yFraction = 0.8f),
            VizTreeNode("n5", "5", depth = 2, xFraction = 0.62f, yFraction = 0.8f),
            VizTreeNode("n7", "7", depth = 2, xFraction = 0.88f, yFraction = 0.8f)
        )
    }

    // 1. Binary Trees Fundamentals
    fun generateBinaryTree(customInput: String?): List<DsaVisualizerStep> {
        val nodes = getSampleBstNodes()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Binary Tree Anatomy: Root, Edges, Leaves & Height",
                description = "Each node has at most two children (left and right). Root is [4] at depth 0.",
                actionType = "INITIALIZE",
                variables = mapOf("root" to "4", "height" to "2", "totalNodes" to "7"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes,
                activeTreeNodeId = "n4"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Internal Branching: Nodes [2] and [6]",
                description = "Node [4] branches into left child [2] and right child [6] at depth 1.",
                actionType = "BRANCH",
                variables = mapOf("leftChild" to "2", "rightChild" to "6"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id == "n2" || it.id == "n6") it.copy(isHighlighted = true) else it },
                activeTreeNodeId = "n2"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Leaf Nodes: [1], [3], [5], [7] (Zero Children)",
                description = "Nodes at depth 2 have both left == null and right == null. Tree is Perfect and Balanced.",
                actionType = "LEAF",
                variables = mapOf("leafCount" to "4", "maxNodesAtLevel2" to "2^2 = 4"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.depth == 2) it.copy(isHighlighted = true) else it },
                isCompleted = true,
                summaryResult = "Binary tree verified: 7 nodes, height 2, O(log N) depth."
            )
        )

        return steps
    }

    // 2. Binary Search Trees (BST Insert & Search)
    fun generateBst(customInput: String?): List<DsaVisualizerStep> {
        val nodes = getSampleBstNodes()
        val steps = mutableListOf<DsaVisualizerStep>()
        val searchKey = 5

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "BST Invariant: Left < Root < Right. Search Key = $searchKey",
                description = "All left descendants < node < all right descendants. Eliminates half of tree at each comparison.",
                actionType = "INITIALIZE",
                variables = mapOf("searchKey" to "$searchKey", "root" to "4"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes,
                activeTreeNodeId = "n4"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Compare Key (5) > Root (4) -> Move Right to [6]",
                description = "5 > 4: by BST invariant, key cannot be in left subtree. Discard left half.",
                actionType = "SEARCH_STEP",
                variables = mapOf("curr" to "4", "target" to "5", "direction" to "RIGHT"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id == "n6") it.copy(isHighlighted = true) else it },
                activeTreeNodeId = "n6"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Compare Key (5) < Node (6) -> Move Left to [5] -> MATCH!",
                description = "5 < 6: move left to node [5]. Found key in O(h) = O(log N) comparisons!",
                actionType = "FOUND",
                variables = mapOf("foundKey" to "5", "depth" to "2"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id == "n5") it.copy(isHighlighted = true, isVisited = true) else it },
                activeTreeNodeId = "n5",
                isFound = true,
                isCompleted = true,
                summaryResult = "BST search located key 5 in O(log N) logarithmic steps."
            )
        )

        return steps
    }

    // 3. Tree Traversals Overview (Level Order BFS)
    fun generateTreeTraversal(customInput: String?): List<DsaVisualizerStep> {
        val nodes = getSampleBstNodes()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Level-Order Traversal (BFS) using FIFO Queue",
                description = "Visit all nodes of depth d before proceeding to depth d + 1. Enqueue root [4].",
                actionType = "INITIALIZE",
                variables = mapOf("queue" to "[4]", "traversal" to "[]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes,
                activeTreeNodeId = "n4"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Level 0 Visited: [4] -> Enqueue Children [2, 6]",
                description = "Dequeued 4. Enqueued 2 and 6. Visited sequence: [4].",
                actionType = "VISIT",
                variables = mapOf("queue" to "[2, 6]", "traversal" to "[4]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id == "n4") it.copy(isVisited = true) else it },
                treeTraversalOrder = listOf("4")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Level 1 Visited: [2, 6] -> Enqueue Leaves [1, 3, 5, 7]",
                description = "Dequeued 2 and 6. Enqueued children. Traversal: [4, 2, 6].",
                actionType = "VISIT",
                variables = mapOf("queue" to "[1, 3, 5, 7]", "traversal" to "[4, 2, 6]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.depth <= 1) it.copy(isVisited = true) else it },
                treeTraversalOrder = listOf("4", "2", "6")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 4,
                title = "Level-Order BFS Complete: [4, 2, 6, 1, 3, 5, 7]",
                description = "All depth tiers traversed horizontally in O(N) time with O(W) queue space.",
                actionType = "COMPLETE",
                variables = mapOf("traversal" to "[4, 2, 6, 1, 3, 5, 7]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { it.copy(isVisited = true) },
                treeTraversalOrder = listOf("4", "2", "6", "1", "3", "5", "7"),
                isCompleted = true,
                summaryResult = "BFS level-order traversal: [4, 2, 6, 1, 3, 5, 7]."
            )
        )

        return steps
    }

    // 4. Inorder Traversal (LNR: Left -> Node -> Right)
    fun generateInorderTraversal(customInput: String?): List<DsaVisualizerStep> {
        val nodes = getSampleBstNodes()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Inorder Traversal (LNR): Left -> Node -> Right",
                description = "Traverse left subtree, visit node, traverse right subtree. On a BST, produces sorted values!",
                actionType = "INITIALIZE",
                variables = mapOf("rule" to "Left -> Node -> Right"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Traverse Left Subtree: Visited [1, 2, 3]",
                description = "Visited left leaf [1], parent [2], right sibling [3].",
                actionType = "VISIT",
                variables = mapOf("inorder" to "[1, 2, 3]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id in listOf("n1", "n2", "n3")) it.copy(isVisited = true) else it },
                treeTraversalOrder = listOf("1", "2", "3")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Visit Root [4] then Right Subtree: [5, 6, 7]",
                description = "Visit Root 4. Traverse right subtree in LNR: 5 -> 6 -> 7.",
                actionType = "VISIT",
                variables = mapOf("inorder" to "[1, 2, 3, 4, 5, 6, 7]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { it.copy(isVisited = true) },
                treeTraversalOrder = listOf("1", "2", "3", "4", "5", "6", "7"),
                isCompleted = true,
                summaryResult = "Inorder traversal outputs strictly sorted sequence: 1, 2, 3, 4, 5, 6, 7."
            )
        )

        return steps
    }

    // 5. Preorder Traversal (NLR: Node -> Left -> Right)
    fun generatePreorderTraversal(customInput: String?): List<DsaVisualizerStep> {
        val nodes = getSampleBstNodes()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Preorder Traversal (NLR): Node -> Left -> Right",
                description = "Process parent node first before visiting children. Root [4] is always the first node visited.",
                actionType = "INITIALIZE",
                variables = mapOf("rule" to "Node -> Left -> Right"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes,
                activeTreeNodeId = "n4"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Visit Root [4], then Recurse Left Subtree: [2, 1, 3]",
                description = "Parent [4] recorded first, then parent [2] recorded before leaves [1] and [3].",
                actionType = "VISIT",
                variables = mapOf("preorder" to "[4, 2, 1, 3]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id in listOf("n4", "n2", "n1", "n3")) it.copy(isVisited = true) else it },
                treeTraversalOrder = listOf("4", "2", "1", "3")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Preorder Complete: [4, 2, 1, 3, 6, 5, 7]",
                description = "Used for cloning trees and serializing hierarchy top-down.",
                actionType = "COMPLETE",
                variables = mapOf("preorder" to "[4, 2, 1, 3, 6, 5, 7]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { it.copy(isVisited = true) },
                treeTraversalOrder = listOf("4", "2", "1", "3", "6", "5", "7"),
                isCompleted = true,
                summaryResult = "Preorder traversal: [4, 2, 1, 3, 6, 5, 7]."
            )
        )

        return steps
    }

    // 6. Postorder Traversal (LRN: Left -> Right -> Node)
    fun generatePostorderTraversal(customInput: String?): List<DsaVisualizerStep> {
        val nodes = getSampleBstNodes()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Postorder Traversal (LRN): Left -> Right -> Node",
                description = "Bottom-up traversal: children are processed before parent. Root [4] is always visited last.",
                actionType = "INITIALIZE",
                variables = mapOf("rule" to "Left -> Right -> Node"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Bottom-Up Subtree Leaves: [1, 3] -> Parent [2]",
                description = "Evaluates leaves [1, 3] first, then parent [2].",
                actionType = "VISIT",
                variables = mapOf("postorder" to "[1, 3, 2]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { if (it.id in listOf("n1", "n3", "n2")) it.copy(isVisited = true) else it },
                treeTraversalOrder = listOf("1", "3", "2")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Postorder Complete: [1, 3, 2, 5, 7, 6, 4]",
                description = "Root [4] is the final node visited. Essential for tree deletion and bottom-up metrics.",
                actionType = "COMPLETE",
                variables = mapOf("postorder" to "[1, 3, 2, 5, 7, 6, 4]"),
                vizType = DsaVizType.TREE,
                treeNodes = nodes.map { it.copy(isVisited = true) },
                treeTraversalOrder = listOf("1", "3", "2", "5", "7", "6", "4"),
                isCompleted = true,
                summaryResult = "Postorder traversal: [1, 3, 2, 5, 7, 6, 4]."
            )
        )

        return steps
    }

    // 7. AVL Trees (Self-Balancing via Rotations)
    fun generateAvlTree(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        val unbalancedNodes = listOf(
            VizTreeNode("n30", "30", leftChildId = "n20", depth = 0, xFraction = 0.6f, yFraction = 0.2f, isHighlighted = true, annotation = "BF = +2"),
            VizTreeNode("n20", "20", leftChildId = "n10", depth = 1, xFraction = 0.4f, yFraction = 0.5f, annotation = "BF = +1"),
            VizTreeNode("n10", "10", depth = 2, xFraction = 0.2f, yFraction = 0.8f, annotation = "BF = 0")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 3,
                title = "AVL Imbalance Detected: Left-Left (LL) Case",
                description = "Node [30] has Balance Factor BF = height(left) - height(right) = +2. Tree is left-heavy.",
                actionType = "IMBALANCE",
                variables = mapOf("imbalanceAt" to "30", "BF" to "+2", "rotationNeeded" to "RIGHT_ROTATION"),
                vizType = DsaVizType.TREE,
                treeNodes = unbalancedNodes,
                activeTreeNodeId = "n30"
            )
        )

        val balancedNodes = listOf(
            VizTreeNode("n20", "20", leftChildId = "n10", rightChildId = "n30", depth = 0, xFraction = 0.5f, yFraction = 0.2f, isHighlighted = true, annotation = "BF = 0"),
            VizTreeNode("n10", "10", depth = 1, xFraction = 0.25f, yFraction = 0.6f, annotation = "BF = 0"),
            VizTreeNode("n30", "30", depth = 1, xFraction = 0.75f, yFraction = 0.6f, annotation = "BF = 0")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 3,
                title = "Right Rotation Executed: Node [20] Becomes Root",
                description = "Single Right Rotation around 30 elevates 20 to root. Node 30 becomes right child of 20.",
                actionType = "ROTATE",
                variables = mapOf("newRoot" to "20", "newBF" to "0", "status" to "BALANCED"),
                vizType = DsaVizType.TREE,
                treeNodes = balancedNodes,
                activeTreeNodeId = "n20",
                isCompleted = true,
                summaryResult = "AVL right rotation restored strict O(log N) balance factor."
            )
        )

        return steps
    }
}
