package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object GraphLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Graph Representation (Matrix vs Adjacency List)
        DsaLessonContent(
            lessonId = "graph_representation_l1",
            subtopicId = "graph_representation",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Graph Representation",
            subtitle = "Vertices, Edges, Adjacency Matrix vs. Adjacency List memory trade-offs",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "Matrix: O(1) query, O(V^2) space; List: O(deg(V)) query, O(V+E) space",
            spaceComplexity = "Matrix O(V^2), List O(V + E)",
            learningObjectives = listOf(
                "Define graph terminology: Vertices (V), Edges (E), Directed/Undirected, Weighted/Unweighted.",
                "Compare Adjacency Matrix (V x V table) vs Adjacency List (array of linked lists/arrays).",
                "Recognize Dense graphs (E ≈ V^2) vs Sparse graphs (E << V^2).",
                "Select optimal representation based on space efficiency and query patterns."
            ),
            sections = listOf(
                LessonSection(
                    id = "graph_anatomy",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Graph?",
                    body = "A Graph G = (V, E) is a non-linear data structure consisting of a set of vertices (or nodes) V connected by a set of edges E.\n\nGraphs are the most expressive data structure in computer science, modeling complex networks such as Google Maps road systems, social media friend connections, internet router topologies, and recommendation engines.",
                    bulletPoints = listOf(
                        "Directed Graph (Digraph): Edges have direction (A -> B != B -> A).",
                        "Undirected Graph: Edges are bidirectional (A <-> B).",
                        "Weighted Graph: Edges carry a cost, length, or capacity."
                    )
                ),
                LessonSection(
                    id = "matrix_vs_list",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "Adjacency Matrix vs. Adjacency List",
                    body = "1. Adjacency Matrix: A 2D boolean or integer array `matrix[V][V]` where `matrix[u][v]` indicates whether edge (u, v) exists.\n- Edge query `hasEdge(u, v)`: O(1) instantaneous.\n- Space: O(V^2) regardless of edge count (wastes massive RAM on sparse real-world graphs!)\n\n2. Adjacency List: An array or map where `adj[u]` lists only the actual neighbors of vertex u.\n- Space: O(V + E) proportional to actual edges.\n- Iterate neighbors: O(degree(u)) fast.\n\nSince real-world graphs (like road networks or social networks) are overwhelmingly SPARSE, Adjacency Lists are the industry standard.",
                    bulletPoints = listOf(
                        "Sparse Graphs (E << V^2): Adjacency List is far superior.",
                        "Dense Graphs (E ≈ V^2): Adjacency Matrix provides O(1) edge checks."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Adjacency List Graph in Kotlin",
                    language = "KOTLIN",
                    code = """class Graph(val vertices: Int) {
    // Adjacency list: array of integer lists for neighbors
    private val adjList = Array(vertices) { mutableListOf<Int>() }

    fun addEdge(u: Int, v: Int, bidirectional: Boolean = true) {
        adjList[u].add(v)
        if (bidirectional) {
            adjList[v].add(u)
        }
    }

    fun getNeighbors(u: Int): List<Int> = adjList[u]
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 3", "Allocate array of size V where each slot holds a dynamic list of neighboring vertex IDs."),
                        CodeStepExplanation(2, "Lines 5-10", "addEdge appends destination to neighbor list in O(1) time; adds reverse if undirected.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "A graph comprises a set of vertices V connected by edges E.",
                "Adjacency matrix consumes O(V^2) space but checks edge existence in O(1).",
                "Adjacency list consumes O(V + E) space, saving massive memory on sparse graphs.",
                "Real-world graphs are almost always sparse, making adjacency lists the standard choice."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_grp_rep_1",
                    question = "Why is an Adjacency List preferred over an Adjacency Matrix for representing a road network with 1,000,000 cities and 3,000,000 roads?",
                    options = listOf(
                        "An adjacency matrix would require 1,000,000^2 = 1 trillion integers (several terabytes), while an adjacency list takes O(V + E) which fits in a few megabytes",
                        "Because road networks cannot have weighted edges",
                        "Because matrices cannot represent bidirectional roads",
                        "Because graph algorithms only work on lists"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A matrix requires V^2 entries regardless of how few edges exist. For V = 10^6, V^2 is 10^12 slots (impractical), whereas an adjacency list only stores the 3*10^6 actual edges, consuming O(V + E) memory."
                ),
                LessonPracticeQuestion(
                    id = "pq_grp_rep_2",
                    question = "What is the time complexity of checking whether an edge exists between vertex u and vertex v in an Adjacency Matrix?",
                    options = listOf("O(1) Constant Time", "O(V) Linear Time", "O(E)", "O(log V)"),
                    correctOptionIndex = 0,
                    explanation = "In an adjacency matrix, checking an edge is an immediate 2D array index lookup: matrix[u][v] in O(1) time."
                )
            )
        ),

        // 2. Breadth-First Search (BFS)
        DsaLessonContent(
            lessonId = "graph_bfs_l1",
            subtopicId = "graph_bfs",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Breadth-First Search (BFS)",
            subtitle = "Concentric level-order expansion using a queue to find shortest unweighted paths",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V)",
            learningObjectives = listOf(
                "Master the BFS level-by-level exploration mechanics using a FIFO Queue.",
                "Understand why a 'visited' set is mandatory to prevent infinite loops in cyclic graphs.",
                "Prove why BFS guarantees the SHORTEST PATH in unweighted graphs.",
                "Analyze time complexity O(V + E) and space complexity O(V)."
            ),
            sections = listOf(
                LessonSection(
                    id = "bfs_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Concentric Ripple Effect",
                    body = "Breadth-First Search (BFS) explores a graph like ripples expanding outward from a stone dropped in a pond.\n\nStarting at a source vertex, BFS visits all immediate neighbors at distance 1, then all neighbors at distance 2, then distance 3, and so on. It never advances to distance d + 1 until all vertices at distance d are completely exhausted.",
                    bulletPoints = listOf(
                        "FIFO Queue: Ensures vertices are processed in strict distance order.",
                        "Visited Set: Marks nodes as soon as they are enqueued to prevent processing the same vertex twice."
                    )
                ),
                LessonSection(
                    id = "bfs_shortest_path_proof",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Shortest Path Guarantee in Unweighted Graphs",
                    body = "Because BFS explores vertices in strictly increasing order of edge distance:\n- The first time BFS encounters target vertex T, it is mathematically GUARANTEED to have reached T via the minimum number of edge hops!\n- No shorter path can exist because any hypothetical shorter path would have visited T at an earlier level.",
                    bulletPoints = listOf(
                        "Time: O(V + E) — every vertex is enqueued once, every edge examined twice (undirected).",
                        "Space: O(V) for queue and visited set."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Graph BFS Traversal in Kotlin",
                    language = "KOTLIN",
                    code = """fun bfs(startNode: Int, adjList: Map<Int, List<Int>>): List<Int> {
    val visited = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()
    val traversalOrder = mutableListOf<Int>()
    
    // Initialize starting vertex
    visited.add(startNode)
    queue.addLast(startNode)
    
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        traversalOrder.add(current)
        
        // Explore all adjacent neighbors
        for (neighbor in adjList[current] ?: emptyList()) {
            if (neighbor !in visited) {
                visited.add(neighbor) // Mark visited IMMEDIATELY on enqueue!
                queue.addLast(neighbor)
            }
        }
    }
    return traversalOrder
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 7-8", "Mark start vertex visited immediately and enqueue it."),
                        CodeStepExplanation(2, "Lines 11-12", "Dequeue front node and record in traversal order."),
                        CodeStepExplanation(3, "Lines 15-19", "Iterate over neighbors; enqueue unvisited neighbors and mark visited instantly to prevent duplicates.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "BFS explores graphs in concentric rings of increasing distance using a FIFO Queue.",
                "Guarantees the shortest path in unweighted graphs.",
                "Always mark nodes visited immediately when adding to the queue, not when dequeuing.",
                "Time complexity is O(V + E); space complexity is O(V)."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_grp_bfs_1",
                    question = "Why must a vertex be marked as 'visited' at the moment it is ENQUEUED rather than when it is DEQUEUED?",
                    options = listOf(
                        "To prevent adjacent nodes from enqueuing the same vertex multiple times, which avoids duplicate work and memory explosion",
                        "Because the queue cannot store unvisited vertices",
                        "Because dequeuing takes O(N) time",
                        "To reverse the search order"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If you wait until dequeue to mark visited, multiple already-enqueued neighbors might see the node as unvisited and push duplicate copies of it into the queue, wasting O(V) memory."
                ),
                LessonPracticeQuestion(
                    id = "pq_grp_bfs_2",
                    question = "What is the time complexity of Breadth-First Search on a graph with V vertices and E edges using an Adjacency List?",
                    options = listOf("O(V + E)", "O(V * E)", "O(V^2)", "O(log V)"),
                    correctOptionIndex = 0,
                    explanation = "Every vertex is enqueued and dequeued at most once (O(V)), and every edge is traversed once per endpoint (O(E)), summing to O(V + E)."
                )
            )
        ),

        // 3. Depth-First Search (DFS)
        DsaLessonContent(
            lessonId = "graph_dfs_l1",
            subtopicId = "graph_dfs",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Depth-First Search (DFS)",
            subtitle = "Deep recursive branch exploration, backtracking, and cycle detection",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V) call stack",
            learningObjectives = listOf(
                "Master recursive and stack-based Depth-First Search mechanics.",
                "Learn Cycle Detection in Directed Graphs using 3-color states (White, Gray, Black).",
                "Apply DFS for Connected Components and Topological Sorting.",
                "Understand the Backtracking pattern."
            ),
            sections = listOf(
                LessonSection(
                    id = "dfs_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Plunging Down Branches",
                    body = "Depth-First Search (DFS) explores a graph by diving as deep as possible along each branch before backtracking.\n\nImagine navigating a physical maze: you follow a single path until you hit a dead end, backtrack to the most recent fork, and explore the next branch. It uses the CPU Call Stack (or an explicit LIFO Stack) to remember the return path.",
                    bulletPoints = listOf(
                        "Call Stack: Recursion naturally maintains the backtracking history.",
                        "Visited Tracking: Prevents infinite looping in cyclic graphs."
                    )
                ),
                LessonSection(
                    id = "cycle_detection_3color",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Cycle Detection & Three-Color States",
                    body = "To detect cycles in a directed graph, a simple boolean `visited` flag is insufficient. We use three vertex states:\n\n1. WHITE (Unvisited): Vertex has not been discovered yet.\n2. GRAY (Visiting / In Stack): Vertex is currently on the active recursion call stack. Its descendants are still being explored.\n3. BLACK (Visited / Done): Vertex and all its descendants are fully explored.\n\nCycle Invariant: If a DFS traversal encounters a GRAY node, a Back-Edge exists pointing back to an active ancestor — a CYCLE IS DETECTED!",
                    bulletPoints = listOf(
                        "Encountering Gray Node = Cycle detected!",
                        "Encountering Black Node = Safe (already explored branch)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Recursive DFS & Cycle Detection in Kotlin",
                    language = "KOTLIN",
                    code = """enum class NodeState { WHITE, GRAY, BLACK }

fun hasCycleDirected(vertices: Int, adjList: Map<Int, List<Int>>): Boolean {
    val state = Array(vertices) { NodeState.WHITE }

    fun dfs(u: Int): Boolean {
        state[u] = NodeState.GRAY // Mark actively visiting on call stack
        
        for (v in adjList[u] ?: emptyList()) {
            if (state[v] == NodeState.GRAY) return true // Cycle found!
            if (state[v] == NodeState.WHITE && dfs(v)) return true
        }
        
        state[u] = NodeState.BLACK // Mark completely finished
        return false
    }

    for (i in 0 until vertices) {
        if (state[i] == NodeState.WHITE && dfs(i)) return true
    }
    return false
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 7", "Mark current node GRAY when entering its recursion frame."),
                        CodeStepExplanation(2, "Lines 10-11", "If an adjacent neighbor is already GRAY, we found a back-edge creating a cycle!"),
                        CodeStepExplanation(3, "Line 14", "Mark node BLACK when popping out of its call frame.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "DFS dives deep along branches before backtracking using a Call Stack or explicit LIFO Stack.",
                "Time complexity is O(V + E); space complexity is O(V) stack depth.",
                "Cycle detection in directed graphs uses three states: White, Gray (in-stack), and Black.",
                "Encountering an active Gray node proves the existence of a directed cycle."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_grp_dfs_1",
                    question = "In the three-color cycle detection algorithm for directed graphs, what does encountering a GRAY neighbor indicate?",
                    options = listOf(
                        "A directed cycle exists because the neighbor is an ancestor currently active on the Call Stack",
                        "The graph is disconnected",
                        "The node is a leaf node",
                        "The edge has negative weight"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A GRAY node represents a vertex whose recursive call is currently pending on the call stack. Finding an edge leading back to it forms a closed loop (cycle)."
                ),
                LessonPracticeQuestion(
                    id = "pq_grp_dfs_2",
                    question = "Why is DFS preferred over BFS when solving maze puzzles or checking topological sorting?",
                    options = listOf(
                        "DFS dives to the end of candidate paths directly and produces natural finishing times for topological sorting",
                        "DFS uses O(1) memory in all graphs",
                        "DFS guarantees shortest paths in weighted graphs",
                        "DFS avoids all cycles automatically"
                    ),
                    correctOptionIndex = 0,
                    explanation = "DFS explores complete paths from start to finish naturally and records exit times, which form the exact reverse order required for topological sorting."
                )
            )
        ),

        // 4. Shortest Path in Unweighted Graphs
        DsaLessonContent(
            lessonId = "graph_shortest_path_l1",
            subtopicId = "graph_shortest_path",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Shortest Path in Unweighted Graphs",
            subtitle = "Parent tracking arrays, distance arrays, and reconstructing optimal paths",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(V + E)",
            spaceComplexity = "O(V)",
            learningObjectives = listOf(
                "Understand why BFS naturally computes the shortest path in unweighted graphs.",
                "Maintain Distance Array: dist[v] = dist[u] + 1.",
                "Maintain Parent Array: parent[v] = u to reconstruct the actual route.",
                "Reconstruct path backwards from destination to source."
            ),
            sections = listOf(
                LessonSection(
                    id = "unweighted_sp_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Unweighted Shortest Path Principle",
                    body = "In an unweighted graph (or a graph where every edge has identical unit cost = 1), the shortest path between two vertices is simply the path containing the FEWEST EDGES.\n\nWhile Dijkstra's algorithm is necessary for weighted graphs, BFS is simpler, faster (O(V + E) vs O((V + E) log V)), and guarantees the optimal path.",
                    bulletPoints = listOf(
                        "dist[start] = 0; all other distances initialized to infinity (or -1).",
                        "For every unvisited neighbor v of u: dist[v] = dist[u] + 1."
                    )
                ),
                LessonSection(
                    id = "path_reconstruction",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Path Reconstruction via Parent Map",
                    body = "To return the actual sequence of vertices (e.g. `[0, 3, 7, 9]`):\n1. When node u discovers unvisited node v, record `parent[v] = u`.\n2. Once destination target T is reached, halt BFS immediately.\n3. Trace backwards from T using parent pointers: `curr = parent[curr]` until reaching source S.\n4. Reverse the collected list to output the correct forward path!",
                    bulletPoints = listOf(
                        "Backtracking via parent map takes time proportional to path length <= V."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "BFS Shortest Path & Reconstruction in Kotlin",
                    language = "KOTLIN",
                    code = """fun shortestPathBfs(
    start: Int,
    target: Int,
    vertices: Int,
    adjList: Map<Int, List<Int>>
): List<Int> {
    val dist = IntArray(vertices) { -1 }
    val parent = IntArray(vertices) { -1 }
    val queue = ArrayDeque<Int>()
    
    dist[start] = 0
    queue.addLast(start)
    
    while (queue.isNotEmpty()) {
        val curr = queue.removeFirst()
        if (curr == target) break // Target reached, early exit!
        
        for (neighbor in adjList[curr] ?: emptyList()) {
            if (dist[neighbor] == -1) { // Unvisited
                dist[neighbor] = dist[curr] + 1
                parent[neighbor] = curr // Record predecessor
                queue.addLast(neighbor)
            }
        }
    }
    
    if (dist[target] == -1) return emptyList() // No path exists
    
    // Reconstruct path backwards from target to start
    val path = mutableListOf<Int>()
    var curr: Int = target
    while (curr != -1) {
        path.add(curr)
        curr = parent[curr]
    }
    path.reverse() // Reverse to forward sequence
    return path
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 17-23", "Standard BFS updates distance by +1 and remembers parent vertex."),
                        CodeStepExplanation(2, "Line 15", "Early exit terminates search as soon as target is dequeued."),
                        CodeStepExplanation(3, "Lines 29-35", "Trace parent pointers backward from target to start and reverse.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "BFS computes the shortest path in unweighted graphs in O(V + E) time.",
                "Tracking parent references enables backwards path reconstruction in O(path length).",
                "Early exit when destination is dequeued saves unnecessary exploration.",
                "More efficient than Dijkstra for unweighted networks (social degrees of separation, mazes)."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_grp_sp_1",
                    question = "Why is BFS preferred over Dijkstra's algorithm for finding the shortest path in an unweighted graph?",
                    options = listOf(
                        "BFS runs in O(V + E) without priority queue heap overhead, while Dijkstra runs in O((V + E) log V)",
                        "BFS works on negative edges while Dijkstra does not",
                        "Dijkstra cannot find shortest paths in trees",
                        "BFS requires zero memory"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In an unweighted graph, all edge weights are identical (1). BFS processes nodes in natural order using a simple O(1) queue, avoiding the O(log V) heap operations of Dijkstra."
                ),
                LessonPracticeQuestion(
                    id = "pq_grp_sp_2",
                    question = "How is the actual sequence of vertices in a shortest path reconstructed after BFS finishes?",
                    options = listOf(
                        "By following recorded parent pointers backwards from target to start, then reversing the list",
                        "By running DFS from target",
                        "By sorting the visited array",
                        "By multiplying vertex numbers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Each node records the predecessor that discovered it in the parent array. Starting at the destination and repeatedly following parent references retraces the exact shortest route back to the source."
                )
            )
        ),

        // 5. Dijkstra's Shortest Path Algorithm
        DsaLessonContent(
            lessonId = "graph_dijkstra_l1",
            subtopicId = "graph_dijkstra",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Dijkstra's Shortest Path Algorithm",
            subtitle = "Greedy edge relaxation with Min-Priority Queues for non-negative weighted graphs",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O((V + E) log V)",
            spaceComplexity = "O(V)",
            learningObjectives = listOf(
                "Understand the Greedy Choice Property of Dijkstra's algorithm.",
                "Master Edge Relaxation: if dist[u] + weight < dist[v], update dist[v].",
                "Learn why Dijkstra's algorithm FAILS on graphs with negative edge weights.",
                "Implement Dijkstra using a Min-Heap / PriorityQueue in O((V + E) log V) time."
            ),
            sections = listOf(
                LessonSection(
                    id = "dijkstra_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Shortest Paths with Edge Weights",
                    body = "Invented by Edsger Dijkstra in 1956, Dijkstra's algorithm finds the shortest path from a single source vertex to all other vertices in a weighted graph with NON-NEGATIVE edge weights.\n\nWhile BFS counts the number of edges, Dijkstra minimizes the cumulative sum of edge weights (e.g., road distance, network latency, flight prices).",
                    bulletPoints = listOf(
                        "Greedy Strategy: At each step, extract the unvisited vertex with the smallest tentative distance.",
                        "Relaxation: Check if reaching neighbor v through current node u is shorter than previously known."
                    )
                ),
                LessonSection(
                    id = "dijkstra_relaxation_math",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Edge Relaxation & The Negative Weight Trap",
                    body = "Edge Relaxation formula:\n`if (dist[u] + weight(u, v) < dist[v]) { dist[v] = dist[u] + weight(u, v) }`\n\nWhy Negative Weights Break Dijkstra:\nDijkstra marks a vertex permanently visited once extracted from the priority queue, assuming no future path can ever be shorter. If a negative edge exists later, this greedy assumption is violated, producing incorrect results!\n(For negative edges, use the Bellman-Ford algorithm).",
                    bulletPoints = listOf(
                        "Time: O((V + E) log V) using a Min-Heap.",
                        "Space: O(V) distance array and priority queue."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Dijkstra's Algorithm in Kotlin",
                    language = "KOTLIN",
                    code = """import java.util.PriorityQueue

data class Edge(val to: Int, val weight: Int)
data class NodeDist(val node: Int, val dist: Int) : Comparable<NodeDist> {
    override fun compareTo(other: NodeDist) = this.dist.compareTo(other.dist) // Min-Heap
}

fun dijkstra(start: Int, vertices: Int, adj: Map<Int, List<Edge>>): IntArray {
    val dist = IntArray(vertices) { Int.MAX_VALUE }
    val pq = PriorityQueue<NodeDist>()
    
    dist[start] = 0
    pq.offer(NodeDist(start, 0))
    
    while (pq.isNotEmpty()) {
        val (u, d) = pq.poll()
        if (d > dist[u]) continue // Skip stale entries
        
        for (edge in adj[u] ?: emptyList()) {
            val newDist = dist[u] + edge.weight
            if (newDist < dist[edge.to]) { // Relaxation!
                dist[edge.to] = newDist
                pq.offer(NodeDist(edge.to, newDist))
            }
        }
    }
    return dist
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 4-6", "NodeDist comparable ensures PriorityQueue behaves as a Min-Heap based on distance."),
                        CodeStepExplanation(2, "Line 17", "Skip stale queue entries if a shorter route to u was already processed."),
                        CodeStepExplanation(3, "Lines 20-24", "Relaxation step: update neighbor distance and enqueue new shorter path estimate.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Dijkstra's algorithm finds single-source shortest paths in non-negative weighted graphs.",
                "Uses a Min-Priority Queue to greedily select the closest unvisited vertex.",
                "Edge relaxation updates distances whenever a shorter path through u is discovered.",
                "Fails on graphs with negative edge weights (requires Bellman-Ford)."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_grp_djk_1",
                    question = "Why does Dijkstra's algorithm fail when a graph contains negative edge weights?",
                    options = listOf(
                        "Its greedy invariant assumes that once a node is finalized, its distance can never decrease, which negative edges violate",
                        "Because priority queues cannot hold negative numbers",
                        "Because negative weights cause integer overflow",
                        "Because negative weights convert graphs into trees"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Dijkstra's greedy assumption is that paths only grow longer as more edges are added. A negative edge can retroactively make a previously finalized path shorter, breaking the algorithm's correctness."
                ),
                LessonPracticeQuestion(
                    id = "pq_grp_djk_2",
                    question = "What is the time complexity of Dijkstra's algorithm implemented with a Min-Heap on a graph with V vertices and E edges?",
                    options = listOf("O((V + E) log V)", "O(V^3)", "O(E * V)", "O(V + E)"),
                    correctOptionIndex = 0,
                    explanation = "Each vertex is extracted from the heap once (V log V), and each edge relaxation pushes an update into the heap at most once (E log V), totaling O((V + E) log V)."
                )
            )
        ),

        // 6. Minimum Spanning Tree (Prim's & Kruskal's)
        DsaLessonContent(
            lessonId = "graph_mst_l1",
            subtopicId = "graph_mst",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Minimum Spanning Tree (MST)",
            subtitle = "Kruskal's Disjoint Set Union vs. Prim's Priority Queue greedy edge selection",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "Kruskal: O(E log E); Prim: O((V + E) log V)",
            spaceComplexity = "O(V + E)",
            learningObjectives = listOf(
                "Define a Spanning Tree: a subgraph connecting all V vertices with exactly V - 1 edges and no cycles.",
                "Master Kruskal's Algorithm: Sort edges by weight and pick greedily using Disjoint Set Union (DSU).",
                "Master Prim's Algorithm: Grow a tree from a start vertex using a Min-Priority Queue.",
                "Identify real-world applications: cable TV networks, electrical grids, telecommunications wiring."
            ),
            sections = listOf(
                LessonSection(
                    id = "mst_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Minimum Spanning Tree?",
                    body = "Given a connected, undirected, weighted graph, a Spanning Tree is a subgraph that connects all V vertices using exactly V - 1 edges without forming any cycles.\n\nA Minimum Spanning Tree (MST) is the spanning tree whose total sum of edge weights is the MINIMUM POSSIBLE among all valid spanning trees.\n\nReal-World Analogy: Laying underground fiber-optic cables to connect 100 cities with the minimum total kilometers of cable.",
                    bulletPoints = listOf(
                        "Edges in MST: Exactly V - 1 edges.",
                        "Cycles: Zero cycles allowed."
                    )
                ),
                LessonSection(
                    id = "kruskal_vs_prim",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Kruskal's vs. Prim's Algorithms",
                    body = "Two famous greedy algorithms compute the MST:\n\n1. Kruskal's Algorithm (Edge-centric):\n- Sort ALL edges in ascending order of weight: O(E log E).\n- Iterate through sorted edges: if adding the edge does not create a cycle (verified in O(α(V)) time using Union-Find / DSU), add it to the MST!\n- Terminate when V - 1 edges are added.\n\n2. Prim's Algorithm (Vertex-centric):\n- Start at an arbitrary root vertex.\n- Repeatedly add the cheapest edge connecting a vertex in the tree to an unvisited vertex outside the tree using a Min-Priority Queue.",
                    bulletPoints = listOf(
                        "Kruskal's: Better for Sparse graphs (easy to sort fewer edges).",
                        "Prim's: Better for Dense graphs."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Kruskal's Algorithm Using Disjoint Set (DSU) in Kotlin",
                    language = "KOTLIN",
                    code = """data class GraphEdge(val src: Int, val dest: Int, val weight: Int) : Comparable<GraphEdge> {
    override fun compareTo(other: GraphEdge) = this.weight.compareTo(other.weight)
}

class DisjointSet(n: Int) {
    val parent = IntArray(n) { it }
    fun find(i: Int): Int {
        if (parent[i] == i) return i
        parent[i] = find(parent[i]) // Path compression
        return parent[i]
    }
    fun union(i: Int, j: Int): Boolean {
        val rootI = find(i); val rootJ = find(j)
        if (rootI != rootJ) {
            parent[rootI] = rootJ
            return true
        }
        return false // Cycle detected!
    }
}

fun kruskalMst(vertices: Int, edges: MutableList<GraphEdge>): List<GraphEdge> {
    edges.sort() // 1. Sort edges by weight in O(E log E)
    val dsu = DisjointSet(vertices)
    val mst = mutableListOf<GraphEdge>()
    
    for (edge in edges) {
        // 2. Greedily add edge if endpoints are in different components
        if (dsu.union(edge.src, edge.dest)) {
            mst.add(edge)
            if (mst.size == vertices - 1) break // MST complete!
        }
    }
    return mst
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 5-18", "Disjoint Set with path compression checks cycle formation in near O(1) time."),
                        CodeStepExplanation(2, "Line 21", "Sort all edges by weight in O(E log E) time."),
                        CodeStepExplanation(3, "Lines 26-30", "Greedily add lowest-cost edges that do not connect already-united components.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "An MST connects all V vertices with V - 1 edges and minimal total edge weight.",
                "Kruskal's algorithm sorts edges and uses Disjoint Set Union (Union-Find) to reject cycle-creating edges in O(E log E).",
                "Prim's algorithm grows a tree using a Min-Priority Queue in O((V + E) log V).",
                "Fundamental to telecommunications, electrical power line routing, and network clustering."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_grp_mst_1",
                    question = "How many edges are present in a Minimum Spanning Tree of a connected graph with V vertices?",
                    options = listOf("Exactly V - 1 edges", "V edges", "V * (V - 1) / 2 edges", "2V edges"),
                    correctOptionIndex = 0,
                    explanation = "By mathematical definition, any spanning tree connecting V vertices without forming cycles must have exactly V - 1 edges."
                ),
                LessonPracticeQuestion(
                    id = "pq_grp_mst_2",
                    question = "How does Kruskal's algorithm detect and avoid creating cycles when evaluating candidate edges?",
                    options = listOf(
                        "Using Disjoint Set Union (DSU / Union-Find) to verify if both endpoints already belong to the same connected component",
                        "By running Dijkstra from each endpoint",
                        "By sorting vertices alphabetically",
                        "By checking if edge weight is positive"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If find(u) == find(v), vertices u and v are already connected in the same component; adding edge (u, v) would create a cycle, so Kruskal's skips it."
                )
            )
        )
    )
}
