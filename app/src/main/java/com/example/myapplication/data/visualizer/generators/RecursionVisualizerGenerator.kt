package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.model.visualizer.VizCallFrame

object RecursionVisualizerGenerator {

    // 1. Introduction to Recursion (Countdown)
    fun generateRecursionIntro(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Recursion Anatomy: Base Case vs Recursive Case",
                description = "A recursive function calls itself with smaller subproblems until hitting a base case that stops execution.",
                actionType = "INITIALIZE",
                variables = mapOf("call" to "countdown(3)"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(VizCallFrame("f3", "countdown", mapOf("n" to "3"), depth = 0))
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 6,
                title = "Push Stack Frame: countdown(2)",
                description = "n=3 is not 0 (not base case). Print 3 and call countdown(2). Stack grows deeper.",
                actionType = "CALL",
                variables = mapOf("depth" to "1", "calling" to "countdown(2)"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("f3", "countdown", mapOf("n" to "3"), depth = 0),
                    VizCallFrame("f2", "countdown", mapOf("n" to "2"), depth = 1)
                )
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 6,
                title = "Push Stack Frame: countdown(1) -> countdown(0)",
                description = "Calls continue until reaching n = 0.",
                actionType = "CALL",
                variables = mapOf("depth" to "3", "calling" to "countdown(0)"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("f3", "countdown", mapOf("n" to "3"), depth = 0),
                    VizCallFrame("f2", "countdown", mapOf("n" to "2"), depth = 1),
                    VizCallFrame("f1", "countdown", mapOf("n" to "1"), depth = 2),
                    VizCallFrame("f0", "countdown", mapOf("n" to "0"), depth = 3)
                )
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 6,
                title = "Base Case Reached: n == 0",
                description = "Base condition met! Function returns immediately without making further calls. Call stack unwinds.",
                actionType = "BASE_CASE",
                variables = mapOf("baseCase" to "true", "action" to "RETURN"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("f3", "countdown", mapOf("n" to "3"), depth = 0),
                    VizCallFrame("f2", "countdown", mapOf("n" to "2"), depth = 1),
                    VizCallFrame("f1", "countdown", mapOf("n" to "1"), depth = 2),
                    VizCallFrame("f0", "countdown", mapOf("n" to "0"), returnValue = "DONE", isReturning = true, depth = 3)
                ),
                baseCaseReached = true
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 4,
                totalSteps = 6,
                title = "Call Stack Fully Unwound",
                description = "All activation frames popped off the call stack. Recursion completed in O(N) time and O(N) auxiliary stack space.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SUCCESS", "stackMemoryFreed" to "true"),
                vizType = DsaVizType.RECURSION,
                callStack = emptyList(),
                isCompleted = true,
                summaryResult = "Recursion demonstrated base-case termination and stack unwinding."
            )
        )

        return steps
    }

    // 2. Factorial
    fun generateFactorial(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Factorial(4): Mathematical Recurrence n * fact(n-1)",
                description = "Compute 4! = 4 * 3 * 2 * 1 by pushing recursive call frames.",
                actionType = "INITIALIZE",
                variables = mapOf("n" to "4"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(VizCallFrame("f4", "fact", mapOf("n" to "4"), depth = 0))
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Stack Growth: fact(4) -> fact(3) -> fact(2) -> fact(1)",
                description = "Each activation record preserves local state while waiting for the next return value.",
                actionType = "PUSH_FRAMES",
                variables = mapOf("maxDepth" to "4"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("f4", "fact", mapOf("n" to "4"), depth = 0),
                    VizCallFrame("f3", "fact", mapOf("n" to "3"), depth = 1),
                    VizCallFrame("f2", "fact", mapOf("n" to "2"), depth = 2),
                    VizCallFrame("f1", "fact", mapOf("n" to "1"), returnValue = "1", depth = 3)
                ),
                baseCaseReached = true
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Unwinding: fact(2) = 2 * 1 = 2; fact(3) = 3 * 2 = 6",
                description = "Returning calculated values back up the call chain: 1 -> 2 -> 6.",
                actionType = "RETURN",
                variables = mapOf("fact(2)" to "2", "fact(3)" to "6"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("f4", "fact", mapOf("n" to "4"), depth = 0),
                    VizCallFrame("f3", "fact", mapOf("n" to "3"), returnValue = "6", isReturning = true, depth = 1)
                )
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Final Evaluation: fact(4) = 4 * 6 = 24",
                description = "Root call finishes computation. 4! = 24.",
                actionType = "COMPLETE",
                variables = mapOf("result" to "24", "4!" to "24"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("f4", "fact", mapOf("n" to "4"), returnValue = "24", isReturning = true, depth = 0)
                ),
                isCompleted = true,
                summaryResult = "Factorial(4) = 24 computed in O(N) time and stack space."
            )
        )

        return steps
    }

    // 3. Fibonacci Series
    fun generateFibonacci(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Fibonacci(4): Branching Tree Recursion",
                description = "Recurrence: fib(n) = fib(n-1) + fib(n-2). Spawns binary recursion tree.",
                actionType = "INITIALIZE",
                variables = mapOf("n" to "4", "complexity" to "O(2^N) naive"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(VizCallFrame("fib4", "fib", mapOf("n" to "4"), depth = 0))
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Branch Left: Compute fib(3) = fib(2) + fib(1)",
                description = "Evaluates fib(3) = 2, then branches right to compute fib(2) = 1.",
                actionType = "BRANCH",
                variables = mapOf("fib(3)" to "2", "fib(2)" to "1"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("fib4", "fib", mapOf("n" to "4"), depth = 0),
                    VizCallFrame("fib3", "fib", mapOf("n" to "3"), returnValue = "2", isReturning = true, depth = 1),
                    VizCallFrame("fib2", "fib", mapOf("n" to "2"), returnValue = "1", isReturning = true, depth = 1)
                )
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Combine Branches: fib(4) = fib(3) + fib(2) = 2 + 1 = 3",
                description = "Final result combined. Illustrates why memoization/DP is essential to avoid re-evaluating duplicate subproblems.",
                actionType = "COMPLETE",
                variables = mapOf("fib(4)" to "3", "repeatedCalls" to "fib(2) evaluated twice"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("fib4", "fib", mapOf("n" to "4"), returnValue = "3", isReturning = true, depth = 0)
                ),
                isCompleted = true,
                summaryResult = "Fibonacci(4) = 3 computed via recursion tree."
            )
        )

        return steps
    }

    // 4. Recursive Binary Search
    fun generateRecursiveBinarySearch(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val arr = listOf(3, 8, 12, 19, 25, 33, 47, 56)
        val target = 25

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Recursive Binary Search: binarySearch(arr, target=25, low=0, high=7)",
                description = "Each recursive call passes an updated halved interval (low, high) on the stack.",
                actionType = "INITIALIZE",
                variables = mapOf("low" to "0", "high" to "7", "target" to "$target"),
                vizType = DsaVizType.RECURSION,
                arrayElements = arr,
                callStack = listOf(VizCallFrame("bs1", "binarySearch", mapOf("low" to "0", "high" to "7"), depth = 0)),
                pointerLabels = mapOf(0 to "L", 7 to "H"),
                highlightedIndices = setOf(0, 7)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Mid = 3 (Val: 19) < 25 -> Recurse Right: binarySearch(low=4, high=7)",
                description = "Push child frame binarySearch(low=4, high=7). Halved search space.",
                actionType = "CALL",
                variables = mapOf("low" to "4", "high" to "7", "mid" to "3 (19)"),
                vizType = DsaVizType.RECURSION,
                arrayElements = arr,
                callStack = listOf(
                    VizCallFrame("bs1", "binarySearch", mapOf("low" to "0", "high" to "7"), depth = 0),
                    VizCallFrame("bs2", "binarySearch", mapOf("low" to "4", "high" to "7"), depth = 1)
                ),
                pointerLabels = mapOf(4 to "L", 7 to "H"),
                highlightedIndices = setOf(4, 7),
                eliminatedIndices = (0..3).toSet()
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Mid = 5 (Val: 33) > 25 -> Recurse Left: Mid = 4 (Val: 25) -> MATCH!",
                description = "Found target 25 at index [4]. Return index 4 up the call chain.",
                actionType = "FOUND",
                variables = mapOf("foundIndex" to "4", "returnVal" to "4"),
                vizType = DsaVizType.RECURSION,
                arrayElements = arr,
                callStack = listOf(
                    VizCallFrame("bs1", "binarySearch", mapOf("low" to "0", "high" to "7"), returnValue = "4", isReturning = true, depth = 0),
                    VizCallFrame("bs2", "binarySearch", mapOf("low" to "4", "high" to "7"), returnValue = "4", isReturning = true, depth = 1)
                ),
                pointerLabels = mapOf(4 to "FOUND"),
                highlightedIndices = setOf(4),
                sortedIndices = setOf(4),
                isFound = true,
                isCompleted = true,
                summaryResult = "Recursive binary search found target 25 at index [4] in O(log N) stack frames."
            )
        )

        return steps
    }

    // 5. Tower of Hanoi
    fun generateTowerOfHanoi(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Tower of Hanoi: 3 Disks from Peg A -> Peg C",
                description = "Move N disks from Source (A) to Destination (C) using Auxiliary (B) without placing a larger disk on a smaller one.",
                actionType = "INITIALIZE",
                variables = mapOf("disks" to "3", "source" to "A", "aux" to "B", "dest" to "C"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(VizCallFrame("toh", "hanoi", mapOf("n" to "3", "src" to "A", "dst" to "C"), depth = 0))
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Subproblem: Move Top 2 Disks from A -> B",
                description = "Recursively move 2 smaller disks to auxiliary Peg B to free up bottom disk 3 on Peg A.",
                actionType = "RECURSE",
                variables = mapOf("pegA" to "[Disk 3]", "pegB" to "[Disk 1, 2]", "pegC" to "[]"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("toh", "hanoi", mapOf("n" to "3", "src" to "A", "dst" to "C"), depth = 0),
                    VizCallFrame("toh2", "hanoi", mapOf("n" to "2", "src" to "A", "dst" to "B"), depth = 1)
                )
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Move Disk 3 Directly from Peg A -> Peg C",
                description = "Largest disk placed at destination Peg C.",
                actionType = "MOVE_DISK",
                variables = mapOf("pegA" to "[]", "pegB" to "[Disk 1, 2]", "pegC" to "[Disk 3]"),
                vizType = DsaVizType.RECURSION,
                callStack = listOf(
                    VizCallFrame("toh", "hanoi", mapOf("n" to "3", "src" to "A", "dst" to "C"), depth = 0)
                )
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Subproblem: Move 2 Disks from Peg B -> Peg C",
                description = "All 3 disks now rest safely on Peg C. Completed in 2^N - 1 = 7 total moves.",
                actionType = "COMPLETE",
                variables = mapOf("pegA" to "[]", "pegB" to "[]", "pegC" to "[Disk 1, 2, 3]", "totalMoves" to "7"),
                vizType = DsaVizType.RECURSION,
                callStack = emptyList(),
                isCompleted = true,
                summaryResult = "Tower of Hanoi solved in 7 moves with O(2^N) steps."
            )
        )

        return steps
    }
}
