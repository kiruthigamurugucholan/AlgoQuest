package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.model.visualizer.VizGraphEdge
import com.example.myapplication.data.model.visualizer.VizGraphVertex

object GraphVisualizerGenerator {

    private fun getSampleVertices(): List<VizGraphVertex> {
        return listOf(
            VizGraphVertex("A", "A", 0.2f, 0.3f),
            VizGraphVertex("B", "B", 0.5f, 0.15f),
            VizGraphVertex("C", "C", 0.8f, 0.3f),
            VizGraphVertex("D", "D", 0.35f, 0.75f),
            VizGraphVertex("E", "E", 0.65f, 0.75f)
        )
    }

    private fun getSampleEdges(): List<VizGraphEdge> {
        return listOf(
            VizGraphEdge("A", "B", weight = 4),
            VizGraphEdge("A", "D", weight = 2),
            VizGraphEdge("B", "C", weight = 3),
            VizGraphEdge("B", "D", weight = 1),
            VizGraphEdge("B", "E", weight = 5),
            VizGraphEdge("C", "E", weight = 2),
            VizGraphEdge("D", "E", weight = 7)
        )
    }

    // 1. Graph Representation (Adjacency Matrix & List)
    fun generateGraphRepresentation(customInput: String?): List<DsaVisualizerStep> {
        val vertices = getSampleVertices()
        val edges = getSampleEdges()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Graph Representation: Vertices V=5, Edges E=7",
                description = "Graphs represent non-linear networks. Modeled via Adjacency Matrix (dense) or Adjacency List (sparse).",
                actionType = "INITIALIZE",
                variables = mapOf("vertices" to "5", "edges" to "7", "representation" to "ADJACENCY_LIST"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices,
                graphEdges = edges
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Adjacency List Inspection: Vertex A Neighbors",
                description = "AdjList[A] -> [B, D]. Vertex A connects to B and D.",
                actionType = "INSPECT",
                variables = mapOf("vertex" to "A", "degree" to "2", "neighbors" to "[B, D]"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id == "A") it.copy(isHighlighted = true) else it },
                graphEdges = edges.map { if (it.fromId == "A" || it.toId == "A") it.copy(isHighlighted = true) else it },
                activeVertexId = "A"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Adjacency List Structure Complete",
                description = "Space complexity O(V + E) for sparse networks vs O(V^2) for adjacency matrix.",
                actionType = "COMPLETE",
                variables = mapOf("space" to "O(V + E)", "status" to "VERIFIED"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { it.copy(isVisited = true) },
                graphEdges = edges,
                isCompleted = true,
                summaryResult = "Adjacency list uses O(V + E) space."
            )
        )

        return steps
    }

    // 2. Breadth First Search (BFS)
    fun generateBfs(customInput: String?): List<DsaVisualizerStep> {
        val vertices = getSampleVertices()
        val edges = getSampleEdges()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "BFS Exploration: Start at Vertex A",
                description = "BFS explores neighbors level-by-level using a FIFO queue. Enqueue source A.",
                actionType = "INITIALIZE",
                variables = mapOf("queue" to "[A]", "visited" to "[A]"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id == "A") it.copy(isHighlighted = true, isVisited = true) else it },
                graphEdges = edges,
                activeVertexId = "A",
                visitedVertexIds = setOf("A"),
                auxiliaryQueueOrStack = listOf("A")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Visit Neighbors of A: Enqueue B and D",
                description = "Dequeued A. Visited adjacent unvisited neighbors B and D. Queue: [B, D].",
                actionType = "VISIT_NEIGHBORS",
                variables = mapOf("queue" to "[B, D]", "visited" to "[A, B, D]"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id in listOf("A", "B", "D")) it.copy(isVisited = true) else it },
                graphEdges = edges.map { if ((it.fromId == "A" && (it.toId == "B" || it.toId == "D"))) it.copy(isHighlighted = true) else it },
                activeVertexId = "A",
                visitedVertexIds = setOf("A", "B", "D"),
                auxiliaryQueueOrStack = listOf("B", "D")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Visit from B: Enqueue C and E",
                description = "Dequeued B. Added C and E to queue. Queue: [D, C, E].",
                actionType = "ENQUEUE",
                variables = mapOf("queue" to "[D, C, E]", "visited" to "[A, B, D, C, E]"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { it.copy(isVisited = true) },
                graphEdges = edges,
                activeVertexId = "B",
                visitedVertexIds = setOf("A", "B", "C", "D", "E"),
                auxiliaryQueueOrStack = listOf("D", "C", "E")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "BFS Traversal Complete: Order = A -> B -> D -> C -> E",
                description = "All reachable vertices visited in O(V + E) time.",
                actionType = "COMPLETE",
                variables = mapOf("bfsOrder" to "A, B, D, C, E"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { it.copy(isVisited = true) },
                graphEdges = edges,
                visitedVertexIds = setOf("A", "B", "C", "D", "E"),
                auxiliaryQueueOrStack = emptyList(),
                isCompleted = true,
                summaryResult = "BFS order: A -> B -> D -> C -> E in O(V + E) time."
            )
        )

        return steps
    }

    // 3. Depth First Search (DFS)
    fun generateDfs(customInput: String?): List<DsaVisualizerStep> {
        val vertices = getSampleVertices()
        val edges = getSampleEdges()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "DFS Exploration: Deep Path Search with Backtracking",
                description = "DFS dives along an unvisited path as far as possible before backtracking. Start at A.",
                actionType = "INITIALIZE",
                variables = mapOf("callStack" to "[A]", "visited" to "[A]"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id == "A") it.copy(isHighlighted = true, isVisited = true) else it },
                graphEdges = edges,
                activeVertexId = "A",
                visitedVertexIds = setOf("A"),
                auxiliaryQueueOrStack = listOf("A")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Dive Deep: A -> B -> C -> E",
                description = "Traversing deep edge chain: A -> B, then B -> C, then C -> E. Stack: [A, B, C, E].",
                actionType = "DIVE",
                variables = mapOf("callStack" to "[A, B, C, E]", "current" to "E"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id in listOf("A", "B", "C", "E")) it.copy(isVisited = true) else it },
                graphEdges = edges.map { if (it.fromId in listOf("A", "B", "C") && it.toId in listOf("B", "C", "E")) it.copy(isHighlighted = true) else it },
                activeVertexId = "E",
                visitedVertexIds = setOf("A", "B", "C", "E"),
                auxiliaryQueueOrStack = listOf("A", "B", "C", "E")
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Backtrack to B and Explore Branch D",
                description = "Backtrack when dead-end is reached. Visit remaining unvisited vertex D. DFS order: A -> B -> C -> E -> D.",
                actionType = "COMPLETE",
                variables = mapOf("dfsOrder" to "A, B, C, E, D"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { it.copy(isVisited = true) },
                graphEdges = edges,
                visitedVertexIds = setOf("A", "B", "C", "D", "E"),
                auxiliaryQueueOrStack = emptyList(),
                isCompleted = true,
                summaryResult = "DFS traversal completed in O(V + E) time."
            )
        )

        return steps
    }

    // 4. Shortest Path (Unweighted BFS)
    fun generateShortestPath(customInput: String?): List<DsaVisualizerStep> {
        val vertices = getSampleVertices()
        val edges = getSampleEdges()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Shortest Path in Unweighted Graph: Source A -> Destination E",
                description = "In unweighted graphs, standard BFS guarantees the shortest path (minimum edge count).",
                actionType = "INITIALIZE",
                variables = mapOf("source" to "A", "target" to "E", "dist[A]" to "0"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id == "A") it.copy(distance = "0", isHighlighted = true) else it },
                graphEdges = edges,
                activeVertexId = "A"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Hop 1: Distances to Neighbors B and D = 1",
                description = "dist[B] = 1, dist[D] = 1. Both reached in 1 step.",
                actionType = "HOP",
                variables = mapOf("dist[B]" to "1", "dist[D]" to "1"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map {
                    when (it.id) {
                        "A" -> it.copy(distance = "0", isVisited = true)
                        "B", "D" -> it.copy(distance = "1", isVisited = true)
                        else -> it
                    }
                },
                graphEdges = edges,
                activeVertexId = "B"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Hop 2: Reach Destination E via B (dist = 2)",
                description = "Path A -> B -> E (2 edges). Shortest path verified.",
                actionType = "COMPLETE",
                variables = mapOf("shortestPath" to "A -> B -> E", "length" to "2 edges"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map {
                    when (it.id) {
                        "A" -> it.copy(distance = "0", isVisited = true)
                        "B" -> it.copy(distance = "1", isVisited = true)
                        "E" -> it.copy(distance = "2", isHighlighted = true, isVisited = true)
                        else -> it.copy(distance = "2")
                    }
                },
                graphEdges = edges.map { if ((it.fromId == "A" && it.toId == "B") || (it.fromId == "B" && it.toId == "E")) it.copy(isHighlighted = true) else it },
                activeVertexId = "E",
                isCompleted = true,
                summaryResult = "Shortest unweighted path from A to E is 2 edges (A -> B -> E)."
            )
        )

        return steps
    }

    // 5. Dijkstra's Algorithm
    fun generateDijkstra(customInput: String?): List<DsaVisualizerStep> {
        val vertices = getSampleVertices()
        val edges = getSampleEdges()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Dijkstra's Algorithm: Shortest Paths on Weighted Graph",
                description = "Greedy relaxation: Maintain priority queue of vertices by tentative distance. dist[A] = 0, all others = ∞.",
                actionType = "INITIALIZE",
                variables = mapOf("dist[A]" to "0", "dist[B..E]" to "∞"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { if (it.id == "A") it.copy(distance = "0", isHighlighted = true) else it.copy(distance = "∞") },
                graphEdges = edges,
                activeVertexId = "A"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Relax Edges from A: dist[D] = 2, dist[B] = 4",
                description = "A -> D (weight 2): dist[D] updated to 2. A -> B (weight 4): dist[B] updated to 4.",
                actionType = "RELAX",
                variables = mapOf("dist[D]" to "2", "dist[B]" to "4"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map {
                    when (it.id) {
                        "A" -> it.copy(distance = "0", isVisited = true)
                        "D" -> it.copy(distance = "2", isHighlighted = true)
                        "B" -> it.copy(distance = "4")
                        else -> it.copy(distance = "∞")
                    }
                },
                graphEdges = edges,
                activeVertexId = "D"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Relax via D & B: dist[B] reduced to 3 via D (2+1=3)",
                description = "Edge D -> B (weight 1) provides shorter route: 2 + 1 = 3 < 4. Relaxation improves dist[B]!",
                actionType = "RELAX",
                variables = mapOf("dist[B]" to "3 (via D)", "dist[E]" to "8"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map {
                    when (it.id) {
                        "A" -> it.copy(distance = "0", isVisited = true)
                        "D" -> it.copy(distance = "2", isVisited = true)
                        "B" -> it.copy(distance = "3", isVisited = true)
                        "C" -> it.copy(distance = "6")
                        "E" -> it.copy(distance = "8")
                        else -> it
                    }
                },
                graphEdges = edges,
                activeVertexId = "B",
                isCompleted = true,
                summaryResult = "Dijkstra's computed all shortest path costs in O((V + E) log V) time."
            )
        )

        return steps
    }

    // 6. Minimum Spanning Tree (MST - Kruskal / Prim)
    fun generateMst(customInput: String?): List<DsaVisualizerStep> {
        val vertices = getSampleVertices()
        val edges = getSampleEdges()
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Minimum Spanning Tree (MST): Kruskal's Greedy Algorithm",
                description = "Sort all edges by ascending weight. Greedily add edges that do not create cycles until V - 1 = 4 edges selected.",
                actionType = "INITIALIZE",
                variables = mapOf("targetEdges" to "V - 1 = 4", "mstWeight" to "0"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices,
                graphEdges = edges
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Pick Smallest Edges: (B-D: 1), (A-D: 2), (C-E: 2)",
                description = "Added lightest available edges without creating cycles. Total weight = 1 + 2 + 2 = 5.",
                actionType = "PICK_EDGES",
                variables = mapOf("edgesInMst" to "3", "weight" to "5"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices,
                graphEdges = edges.map {
                    val isPart = (it.fromId == "B" && it.toId == "D") ||
                            (it.fromId == "A" && it.toId == "D") ||
                            (it.fromId == "C" && it.toId == "E")
                    it.copy(isHighlighted = isPart, isMst = isPart)
                }
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Connect Remaining Vertex: Add (B-C: 3). MST Complete!",
                description = "Final edge connects all 5 vertices. Total MST weight = 1 + 2 + 2 + 3 = 8.",
                actionType = "COMPLETE",
                variables = mapOf("totalMstWeight" to "8", "edgesCount" to "4"),
                vizType = DsaVizType.GRAPH,
                graphVertices = vertices.map { it.copy(isVisited = true) },
                graphEdges = edges.map {
                    val isPart = (it.fromId == "B" && it.toId == "D") ||
                            (it.fromId == "A" && it.toId == "D") ||
                            (it.fromId == "C" && it.toId == "E") ||
                            (it.fromId == "B" && it.toId == "C")
                    it.copy(isMst = isPart, isHighlighted = isPart)
                },
                isCompleted = true,
                summaryResult = "MST spans all 5 vertices with minimum total weight of 8."
            )
        )

        return steps
    }
}
