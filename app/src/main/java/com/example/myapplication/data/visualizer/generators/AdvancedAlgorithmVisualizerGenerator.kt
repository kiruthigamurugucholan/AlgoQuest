package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.model.visualizer.VizGreedyItem

object AdvancedAlgorithmVisualizerGenerator {

    // 1. Greedy Algorithms (Fractional Knapsack / Activity Selection)
    fun generateGreedy(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val initialItems = listOf(
            VizGreedyItem("i1", "Item A", weight = "10kg", value = "$60", ratio = "6.0 $/kg", status = "AVAILABLE"),
            VizGreedyItem("i2", "Item B", weight = "20kg", value = "$100", ratio = "5.0 $/kg", status = "AVAILABLE"),
            VizGreedyItem("i3", "Item C", weight = "30kg", value = "$120", ratio = "4.0 $/kg", status = "AVAILABLE")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Greedy Strategy: Sort Candidates by Value-to-Weight Ratio",
                description = "Knapsack Capacity = 50kg. Greedily pick item with highest value density first.",
                actionType = "INITIALIZE",
                variables = mapOf("capacity" to "50kg", "strategy" to "MAX_VALUE_DENSITY"),
                vizType = DsaVizType.GREEDY,
                greedyItems = initialItems,
                greedyCapacityUsed = "0 / 50kg",
                greedyTotalValue = "$0"
            )
        )

        val step1Items = listOf(
            initialItems[0].copy(status = "SELECTED"),
            initialItems[1].copy(status = "AVAILABLE"),
            initialItems[2].copy(status = "AVAILABLE")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Pick Item A (Ratio 6.0 $/kg): Takes 10kg, Earns $60",
                description = "Item A has highest ratio. Remaining capacity = 50 - 10 = 40kg. Total value = $60.",
                actionType = "SELECT",
                variables = mapOf("picked" to "Item A", "remainingCap" to "40kg", "totalValue" to "$60"),
                vizType = DsaVizType.GREEDY,
                greedyItems = step1Items,
                greedyCapacityUsed = "10 / 50kg",
                greedyTotalValue = "$60"
            )
        )

        val step2Items = listOf(
            initialItems[0].copy(status = "SELECTED"),
            initialItems[1].copy(status = "SELECTED"),
            initialItems[2].copy(status = "SELECTED") // fraction
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Pick Item B (20kg) + Fraction of Item C (20kg)",
                description = "Item B takes 20kg ($100). Remaining 20kg takes 2/3 of Item C ($80). Total earned = $240.",
                actionType = "COMPLETE",
                variables = mapOf("totalValue" to "$240", "capacityUsed" to "50 / 50kg"),
                vizType = DsaVizType.GREEDY,
                greedyItems = step2Items,
                greedyCapacityUsed = "50 / 50kg (FULL)",
                greedyTotalValue = "$240",
                isCompleted = true,
                summaryResult = "Greedy choice yielded optimal knapsack value: $240."
            )
        )

        return steps
    }

    // 2. Dynamic Programming (0/1 Knapsack DP Table)
    fun generateDp(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val headersX = listOf("W=0", "W=1", "W=2", "W=3", "W=4")
        val headersY = listOf("Item 0 (None)", "Item 1 (w:1, v:1)", "Item 2 (w:2, v:4)", "Item 3 (w:3, v:7)")

        val initialGrid = listOf(
            listOf("0", "0", "0", "0", "0"),
            listOf("0", "-", "-", "-", "-"),
            listOf("0", "-", "-", "-", "-"),
            listOf("0", "-", "-", "-", "-")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "0/1 Knapsack DP Table: Tabulation State Transition",
                description = "dp[i][w] = max(dp[i-1][w], value[i] + dp[i-1][w - weight[i]]). Base row & col = 0.",
                actionType = "INITIALIZE",
                variables = mapOf("rows" to "Items", "cols" to "Weights (0..4)"),
                vizType = DsaVizType.DP_TABLE,
                dpHeadersX = headersX,
                dpHeadersY = headersY,
                dpGrid = initialGrid,
                dpFormula = "dp[i][w] = max(skip, include)"
            )
        )

        val partialGrid = listOf(
            listOf("0", "0", "0", "0", "0"),
            listOf("0", "1", "1", "1", "1"),
            listOf("0", "1", "4", "5", "5"),
            listOf("0", "-", "-", "-", "-")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Fill Row 2: Item 2 (weight=2, val=4)",
                description = "At capacity W=3: include gives 4 + dp[1][1] = 4 + 1 = 5. Skip gives dp[1][3] = 1. Max = 5.",
                actionType = "FILL_ROW",
                variables = mapOf("cell" to "dp[2][3]", "value" to "5", "decision" to "INCLUDE"),
                vizType = DsaVizType.DP_TABLE,
                dpHeadersX = headersX,
                dpHeadersY = headersY,
                dpGrid = partialGrid,
                activeDpCell = Pair(2, 3),
                dependentDpCells = listOf(Pair(1, 3), Pair(1, 1)),
                dpFormula = "dp[2][3] = max(dp[1][3], 4 + dp[1][1]) = 5"
            )
        )

        val finalGrid = listOf(
            listOf("0", "0", "0", "0", "0"),
            listOf("0", "1", "1", "1", "1"),
            listOf("0", "1", "4", "5", "5"),
            listOf("0", "1", "4", "7", "8")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Optimal Solution in Bottom-Right Cell: dp[3][4] = 8",
                description = "Maximum value achievable with capacity 4 is 8 (taking Item 1 + Item 3).",
                actionType = "COMPLETE",
                variables = mapOf("maxValue" to "8", "timeComplexity" to "O(N * W)", "space" to "O(N * W)"),
                vizType = DsaVizType.DP_TABLE,
                dpHeadersX = headersX,
                dpHeadersY = headersY,
                dpGrid = finalGrid,
                activeDpCell = Pair(3, 4),
                isCompleted = true,
                summaryResult = "DP table computed global maximum value of 8 in O(N * W) time."
            )
        )

        return steps
    }

    // 3. Backtracking (4-Queens Problem)
    fun generateBacktracking(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val headers = listOf("Col 0", "Col 1", "Col 2", "Col 3")
        val rows = listOf("Row 0", "Row 1", "Row 2", "Row 3")

        val state1 = listOf(
            listOf("Q", ".", ".", "."),
            listOf(".", ".", "Q", "."),
            listOf(".", ".", ".", "."),
            listOf(".", ".", ".", ".")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "4-Queens Backtracking: Place Non-Attacking Queens",
                description = "Place 4 queens on 4x4 board so no two share same row, column, or diagonal.",
                actionType = "INITIALIZE",
                variables = mapOf("board" to "4x4", "queensToPlace" to "4"),
                vizType = DsaVizType.DP_TABLE,
                dpHeadersX = headers,
                dpHeadersY = rows,
                dpGrid = state1,
                activeDpCell = Pair(1, 2)
            )
        )

        val conflictState = listOf(
            listOf("Q", ".", ".", "."),
            listOf(".", ".", "Q", "."),
            listOf(".", "X", ".", "."), // conflict
            listOf(".", ".", ".", ".")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Conflict Detected at Row 2: Backtrack!",
                description = "Every column in row 2 is under attack. Prune this dead-end branch and backtrack to previous row.",
                actionType = "BACKTRACK",
                variables = mapOf("conflict" to "Row 2 has no safe column", "action" to "BACKTRACK"),
                vizType = DsaVizType.DP_TABLE,
                dpHeadersX = headers,
                dpHeadersY = rows,
                dpGrid = conflictState,
                activeDpCell = Pair(2, 1)
            )
        )

        val solvedState = listOf(
            listOf(".", "Q", ".", "."),
            listOf(".", ".", ".", "Q"),
            listOf("Q", ".", ".", "."),
            listOf(".", ".", "Q", ".")
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Valid Solution Found: [1, 3, 0, 2]",
                description = "Found valid non-attacking configuration: Queens at (0,1), (1,3), (2,0), (3,2).",
                actionType = "COMPLETE",
                variables = mapOf("solution" to "[1, 3, 0, 2]", "status" to "SOLVED"),
                vizType = DsaVizType.DP_TABLE,
                dpHeadersX = headers,
                dpHeadersY = rows,
                dpGrid = solvedState,
                isCompleted = true,
                summaryResult = "Backtracking solved 4-Queens problem without exhaustive brute force."
            )
        )

        return steps
    }

    // 4. Divide and Conquer
    fun generateDivideAndConquer(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val arr = listOf(14, 7, 22, 19, 31, 8)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Divide and Conquer Paradigm",
                description = "1. Divide into subproblems. 2. Conquer subproblems recursively. 3. Combine solutions.",
                actionType = "INITIALIZE",
                variables = mapOf("originalSize" to "${arr.size}"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                highlightedIndices = arr.indices.toSet()
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Divide into Independent Halves",
                description = "Left: [14, 7, 22] | Right: [19, 31, 8]. Subproblems do not overlap.",
                actionType = "DIVIDE",
                variables = mapOf("left" to "[14, 7, 22]", "right" to "[19, 31, 8]"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(0 to "L", 3 to "R"),
                highlightedIndices = setOf(0, 1, 2)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Combine: Merge Sub-Solutions in Linear Time",
                description = "Sorted merged result: [7, 8, 14, 19, 22, 31]. Master theorem: T(N) = 2T(N/2) + O(N) = O(N log N).",
                actionType = "COMPLETE",
                variables = mapOf("result" to "[7, 8, 14, 19, 22, 31]", "time" to "O(N log N)"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr.sorted(),
                sortedIndices = arr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Divide and conquer sorted array in O(N log N) time."
            )
        )

        return steps
    }

    // 5. Hashing (Hash Table & Collision Chaining)
    fun generateHashing(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val keys = listOf("Apple (15)", "Banana (22)", "Cherry (36)", "Date (29)")

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Hash Function: h(key) = key % 7 (Capacity = 7)",
                description = "Maps arbitrary keys directly to bucket indices [0..6] for expected O(1) lookups.",
                actionType = "INITIALIZE",
                variables = mapOf("capacity" to "7", "hashFunc" to "key % 7"),
                vizType = DsaVizType.ARRAY,
                arrayElements = listOf(0, 0, 0, 0, 0, 0, 0)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Hash Collision: 15 % 7 = 1 and 22 % 7 = 1",
                description = "Both 15 and 22 hash to bucket [1]. Handled via Separate Chaining: Bucket 1 -> [15] -> [22].",
                actionType = "COLLISION",
                variables = mapOf("bucket1" to "15 -> 22 (CHAINED)"),
                vizType = DsaVizType.ARRAY,
                arrayElements = listOf(0, 22, 0, 0, 0, 0, 0),
                pointerLabels = mapOf(1 to "chain"),
                highlightedIndices = setOf(1)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Hash Table Populated: Average O(1) Lookups",
                description = "Load factor α = 4/7 ≈ 0.57. Hash table provides average O(1) lookup, insert, and delete.",
                actionType = "COMPLETE",
                variables = mapOf("loadFactor" to "0.57", "lookupTime" to "O(1) avg"),
                vizType = DsaVizType.ARRAY,
                arrayElements = listOf(0, 22, 0, 0, 0, 36, 0),
                sortedIndices = (0..6).toSet(),
                isCompleted = true,
                summaryResult = "Hash table verified: constant O(1) average lookup performance."
            )
        )

        return steps
    }

    // 6. Heap and Priority Queue (Array Representation & Sift Down)
    fun generateHeapAndPriorityQueue(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val heapArr = listOf(90, 80, 70, 50, 40, 60, 30)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Max-Heap in Flat Array: Parent at i, Children at 2i+1, 2i+2",
                description = "Complete binary tree stored compactly in contiguous array without pointer overhead.",
                actionType = "INITIALIZE",
                variables = mapOf("root" to "arr[0] = 90", "left(0)" to "arr[1]=80", "right(0)" to "arr[2]=70"),
                vizType = DsaVizType.ARRAY,
                arrayElements = heapArr,
                pointerLabels = mapOf(0 to "root", 1 to "left", 2 to "right"),
                highlightedIndices = setOf(0, 1, 2)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Insert 95: Bubble-Up / Sift-Up Violation",
                description = "95 added at leaf index 7. 95 > parent(50) -> swap. 95 > root(90) -> swap to new root!",
                actionType = "SIFT_UP",
                variables = mapOf("newVal" to "95", "swaps" to "2", "newRoot" to "95"),
                vizType = DsaVizType.ARRAY,
                arrayElements = listOf(95, 90, 70, 80, 40, 60, 30),
                pointerLabels = mapOf(0 to "maxRoot"),
                highlightedIndices = setOf(0)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Extract-Max: Replace Root with Last Leaf and Sift Down",
                description = "Extract 95. Move last leaf to root and bubble down to restore heap invariant in O(log N) time.",
                actionType = "COMPLETE",
                variables = mapOf("extractedMax" to "95", "time" to "O(log N)"),
                vizType = DsaVizType.ARRAY,
                arrayElements = heapArr,
                sortedIndices = heapArr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Binary heap maintained max priority in O(log N) insert and extract."
            )
        )

        return steps
    }
}
