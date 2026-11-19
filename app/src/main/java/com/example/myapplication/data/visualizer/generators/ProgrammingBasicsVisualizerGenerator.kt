package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType

object ProgrammingBasicsVisualizerGenerator {

    // 1. Introduction to Data Structures
    fun generateIntroDs(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val memoryAddresses = listOf("0x1000", "0x1004", "0x1008", "0x100C", "0x1010")
        val arrayData = listOf(10, 20, 30, 40, 50)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Contiguous Memory: Array vs Node-Based Allocation",
                description = "Arrays allocate consecutive memory addresses. Base address = 0x1000. Element size = 4 bytes.",
                actionType = "INITIALIZE",
                variables = mapOf("baseAddress" to "0x1000", "elementSize" to "4 bytes", "layout" to "CONTIGUOUS"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arrayData,
                pointerLabels = mapOf(0 to "base", 4 to "end"),
                highlightedIndices = setOf(0, 4)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "O(1) Direct Offset Math: Address(i) = base + (i * 4)",
                description = "arr[3] located directly at 0x1000 + (3 * 4) = 0x100C without scanning preceding elements!",
                actionType = "CALCULATE",
                variables = mapOf("index" to "3", "computedAddress" to "0x100C", "value" to "40"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arrayData,
                pointerLabels = mapOf(3 to "0x100C"),
                highlightedIndices = setOf(3)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Linear vs Non-Linear Structures",
                description = "Arrays & Linked Lists arrange elements linearly. Trees & Graphs organize elements hierarchically.",
                actionType = "COMPLETE",
                variables = mapOf("linear" to "Arrays, Lists, Stacks", "nonLinear" to "Trees, Graphs"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arrayData,
                sortedIndices = arrayData.indices.toSet(),
                isCompleted = true,
                summaryResult = "Contiguous structures allow O(1) random memory access."
            )
        )

        return steps
    }

    // 2. Introduction to Algorithms
    fun generateIntroAlgo(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val arr = listOf(5, 3, 8, 4, 2)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Algorithm Pipeline: Input -> Step-by-Step Logic -> Output",
                description = "An algorithm is an unambiguous sequence of finite instructions to solve a problem. Goal: Find Maximum.",
                actionType = "INITIALIZE",
                variables = mapOf("inputSize" to "${arr.size}", "currentMax" to "${arr[0]}"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(0 to "maxSoFar"),
                highlightedIndices = setOf(0)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Iterative Invariant: Updating maxCandidate",
                description = "Comparing arr[2] (8) > currentMax (5). Update currentMax = 8.",
                actionType = "UPDATE_MAX",
                variables = mapOf("compared" to "arr[2] = 8", "newMax" to "8"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(2 to "newMax"),
                highlightedIndices = setOf(2)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Deterministic Termination: Final Output = 8",
                description = "Algorithm examined all N elements and terminated deterministically in O(N) operations.",
                actionType = "COMPLETE",
                variables = mapOf("globalMax" to "8", "comparisons" to "4"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                sortedIndices = setOf(2),
                isCompleted = true,
                summaryResult = "Algorithm terminated deterministically with correct maximum value: 8."
            )
        )

        return steps
    }

    // 3. Time Complexity
    fun generateTimeComplexity(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val nValues = listOf(1, 10, 100, 1000)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Time Complexity: Operation Count as Input N Scales",
                description = "Measures how execution time grows as input size N tends toward infinity. Independent of hardware.",
                actionType = "INITIALIZE",
                variables = mapOf("N" to "10", "O(1)" to "1 op", "O(log N)" to "3 ops", "O(N)" to "10 ops", "O(N^2)" to "100 ops"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("Constant O(1)", "Logarithmic O(log N)", "Linear O(N)", "Quadratic O(N^2)"),
                activeFlowchartStep = 0,
                complexityCurveName = "O(1)"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Scaling Impact at N = 1,000 Elements",
                description = "O(log N) = 10 ops | O(N) = 1,000 ops | O(N^2) = 1,000,000 ops! Algorithmic efficiency dominates hardware speed.",
                actionType = "COMPARE_SCALING",
                variables = mapOf("N" to "1000", "logN" to "10", "linearN" to "1,000", "quadraticN" to "1,000,000"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("O(1): 1", "O(log N): 10", "O(N): 1,000", "O(N^2): 1,000,000"),
                activeFlowchartStep = 2,
                complexityCurveName = "O(N)"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Asymptotic Dominance Hierarchy",
                description = "O(1) < O(log N) < O(N) < O(N log N) < O(N^2) < O(2^N) < O(N!).",
                actionType = "COMPLETE",
                variables = mapOf("fastest" to "O(1)", "slowest" to "O(N!)"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("O(1)", "O(log N)", "O(N)", "O(N log N)", "O(N^2)"),
                activeFlowchartStep = 3,
                isCompleted = true,
                summaryResult = "Time complexity hierarchy: O(1) < O(log N) < O(N) < O(N log N) < O(N^2)."
            )
        )

        return steps
    }

    // 4. Space Complexity
    fun generateSpaceComplexity(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Space Complexity: Fixed vs Auxiliary Working Memory",
                description = "Total Space = Fixed Code Space + Input Space + Auxiliary Space (variables, dynamic heap allocations, call stack frames).",
                actionType = "INITIALIZE",
                variables = mapOf("auxiliarySpace" to "Memory allocated during execution"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("Primitive Variables: O(1)", "Call Stack Frames: O(h)", "Dynamic Array/Buffer: O(N)"),
                activeFlowchartStep = 0
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "In-Place vs Out-of-Place Tradeoffs",
                description = "In-Place algorithms (e.g. Quick Sort, In-Place Reversal) use O(1) extra space. Out-of-Place (e.g. Merge Sort) allocate O(N) auxiliary arrays.",
                actionType = "COMPARE_SPACE",
                variables = mapOf("inPlace" to "O(1) auxiliary", "outOfPlace" to "O(N) auxiliary"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("In-Place: O(1)", "Call Stack: O(log N)", "Buffer Copy: O(N)"),
                activeFlowchartStep = 1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Space Complexity Verified",
                description = "Optimizing auxiliary space prevents memory leaks and OutOfMemoryError in constrained mobile environments.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "OPTIMAL"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("Primitive Variables: O(1)", "Call Stack: O(log N)", "Buffer: O(N)"),
                activeFlowchartStep = 2,
                isCompleted = true,
                summaryResult = "Auxiliary space complexity analysis verified."
            )
        )

        return steps
    }

    // 5. Big O Notation
    fun generateBigONotation(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Big O Asymptotic Notation: Upper Bound O(g(N))",
                description = "Big O describes the mathematical worst-case upper bound: f(N) <= c * g(N) for all N >= N0.",
                actionType = "INITIALIZE",
                variables = mapOf("BigO_O" to "Upper Bound (Worst Case)", "BigOmega_Ω" to "Lower Bound (Best Case)", "BigTheta_Θ" to "Tight Bound (Average Case)"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("Big O: Upper Bound", "Big Omega: Lower Bound", "Big Theta: Tight Bound"),
                activeFlowchartStep = 0,
                complexityCurveName = "O(N)"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Rule of Sum & Product: Dropping Constants",
                description = "f(N) = 3N^2 + 50N + 1000 -> O(N^2). Non-dominant terms and leading constants drop as N grows to infinity.",
                actionType = "DROP_CONSTANTS",
                variables = mapOf("polynomial" to "3N^2 + 50N + 1000", "dominantTerm" to "N^2", "asymptoticClass" to "O(N^2)"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("Drop Constants: 3N -> N", "Drop Non-Dominant: N^2 + N -> N^2", "Identify Worst Case"),
                activeFlowchartStep = 1,
                complexityCurveName = "O(N^2)"
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Big O Notation Mastery Complete",
                description = "Enables comparing algorithms purely based on scaling behavior rather than machine clocks.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "MASTERED"),
                vizType = DsaVizType.FLOWCHART,
                flowchartSteps = listOf("Big O (Worst)", "Big Omega (Best)", "Big Theta (Tight)"),
                activeFlowchartStep = 2,
                isCompleted = true,
                summaryResult = "Big O defines asymptotic upper bound growth rate."
            )
        )

        return steps
    }
}
