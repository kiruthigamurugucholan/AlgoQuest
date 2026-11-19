package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

object SearchingVisualizerGenerator {

    private fun parseArrayAndTarget(input: String?, defaultArr: List<Int>, defaultTarget: Int): Pair<List<Int>, Int> {
        if (input.isNullOrBlank()) return Pair(defaultArr, defaultTarget)
        val parts = input.split(";")
        val arr = parts[0].split(",").mapNotNull { it.trim().toIntOrNull() }
        val target = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: defaultTarget
        val finalArr = if (arr.size in 2..10) arr else defaultArr
        return Pair(finalArr, target)
    }

    // 1. Linear Search
    fun generateLinearSearch(customInput: String?): List<DsaVisualizerStep> {
        val (arr, target) = parseArrayAndTarget(customInput, listOf(18, 42, 7, 91, 53, 34, 88), 53)
        val steps = mutableListOf<DsaVisualizerStep>()
        val total = arr.size + 2

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = total,
                title = "Linear Search: Looking for Target = $target",
                description = "Iterate through elements sequentially from index 0 until target is found or array ends.",
                actionType = "INITIALIZE",
                variables = mapOf("target" to "$target", "index" to "0"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = mapOf(0 to "i"),
                highlightedIndices = setOf(0)
            )
        )

        var foundIndex = -1
        for (i in arr.indices) {
            val isMatch = arr[i] == target
            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = total,
                    title = "Compare arr[$i] (${arr[i]}) with Target ($target)",
                    description = if (isMatch) "Match found! arr[$i] == $target." else "arr[$i] (${arr[i]}) != $target. Move to next index.",
                    actionType = if (isMatch) "FOUND" else "COMPARE",
                    variables = mapOf("index" to "$i", "element" to "${arr[i]}", "target" to "$target"),
                    vizType = DsaVizType.SEARCH,
                    arrayElements = arr,
                    targetValue = "$target",
                    pointerLabels = mapOf(i to if (isMatch) "FOUND" else "i"),
                    highlightedIndices = setOf(i),
                    eliminatedIndices = if (!isMatch) (0..i).toSet() else (0 until i).toSet(),
                    isFound = isMatch
                )
            )

            if (isMatch) {
                foundIndex = i
                break
            }
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = if (foundIndex != -1) "Target $target Found at Index [$foundIndex]!" else "Target $target Not Found",
                description = if (foundIndex != -1) "Linear search completed successfully in ${foundIndex + 1} comparisons." else "Exhausted all ${arr.size} elements without finding target.",
                actionType = "COMPLETE",
                variables = mapOf("foundAt" to if (foundIndex != -1) "$foundIndex" else "-1", "comparisons" to "${if (foundIndex != -1) foundIndex + 1 else arr.size}"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = if (foundIndex != -1) mapOf(foundIndex to "FOUND") else emptyMap(),
                highlightedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                sortedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                isCompleted = true,
                summaryResult = if (foundIndex != -1) "Target $target found at index [$foundIndex]." else "Target $target not found in array."
            )
        )

        return steps
    }

    // 2. Binary Search
    fun generateBinarySearch(customInput: String?): List<DsaVisualizerStep> {
        val (rawArr, target) = parseArrayAndTarget(customInput, listOf(4, 9, 15, 23, 38, 56, 72, 91), 38)
        val arr = rawArr.sorted()
        val steps = mutableListOf<DsaVisualizerStep>()

        var low = 0
        var high = arr.size - 1
        var foundIndex = -1
        val eliminated = mutableSetOf<Int>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Binary Search: Target = $target in Sorted Array",
                description = "Initial interval [low = 0, high = ${arr.size - 1}]. Array must be sorted.",
                actionType = "INITIALIZE",
                variables = mapOf("low" to "$low", "high" to "$high", "target" to "$target"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = mapOf(low to "L", high to "H"),
                highlightedIndices = setOf(low, high)
            )
        )

        while (low <= high && steps.size < 10) {
            val mid = low + (high - low) / 2
            val midVal = arr[mid]
            val isMatch = midVal == target

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 6,
                    title = "Evaluate Midpoint arr[$mid] = $midVal",
                    description = if (isMatch) {
                        "Target found! arr[$mid] == $target."
                    } else if (midVal < target) {
                        "arr[$mid] ($midVal) < $target. Target must be in right half. Discard [0..$mid]."
                    } else {
                        "arr[$mid] ($midVal) > $target. Target must be in left half. Discard [$mid..${arr.size - 1}]."
                    },
                    actionType = if (isMatch) "FOUND" else "COMPARE",
                    variables = mapOf("low" to "$low", "mid" to "$mid ($midVal)", "high" to "$high", "target" to "$target"),
                    vizType = DsaVizType.SEARCH,
                    arrayElements = arr,
                    targetValue = "$target",
                    pointerLabels = mapOf(low to "L", mid to "M", high to "H"),
                    highlightedIndices = setOf(mid),
                    eliminatedIndices = eliminated.toSet(),
                    isFound = isMatch
                )
            )

            if (isMatch) {
                foundIndex = mid
                break
            } else if (midVal < target) {
                for (k in low..mid) eliminated.add(k)
                low = mid + 1
            } else {
                for (k in mid..high) eliminated.add(k)
                high = mid - 1
            }
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = if (foundIndex != -1) "Binary Search Complete: Found at [$foundIndex]" else "Binary Search Complete: Target Not Found",
                description = if (foundIndex != -1) "Target $target located in O(log N) iterations." else "Search space halved until low > high.",
                actionType = "COMPLETE",
                variables = mapOf("foundIndex" to "$foundIndex", "target" to "$target"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = if (foundIndex != -1) mapOf(foundIndex to "FOUND") else emptyMap(),
                highlightedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                sortedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                isCompleted = true,
                summaryResult = if (foundIndex != -1) "Target $target found at index [$foundIndex] in O(log N) time." else "Target not present in sorted array."
            )
        )

        return steps
    }

    // 3. Jump Search
    fun generateJumpSearch(customInput: String?): List<DsaVisualizerStep> {
        val (rawArr, target) = parseArrayAndTarget(customInput, listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34), 13)
        val arr = rawArr.sorted()
        val n = arr.size
        val m = floor(sqrt(n.toDouble())).toInt().coerceAtLeast(1)
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Jump Search: Block Size m = √$n ≈ $m",
                description = "Jump through blocks of size $m until arr[step] >= target ($target), then run linear search backwards.",
                actionType = "INITIALIZE",
                variables = mapOf("blockSize" to "$m", "target" to "$target", "n" to "$n"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = mapOf(0 to "start"),
                highlightedIndices = setOf(0)
            )
        )

        var prev = 0
        var step = m
        while (arr[min(step, n) - 1] < target) {
            val checkIdx = min(step, n) - 1
            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 6,
                    title = "Block Jump: Check arr[$checkIdx] = ${arr[checkIdx]}",
                    description = "arr[$checkIdx] (${arr[checkIdx]}) < $target. Jump forward by block size $m.",
                    actionType = "JUMP",
                    variables = mapOf("prevBlock" to "$prev", "currentStep" to "$step", "value" to "${arr[checkIdx]}"),
                    vizType = DsaVizType.SEARCH,
                    arrayElements = arr,
                    targetValue = "$target",
                    pointerLabels = mapOf(prev to "prev", checkIdx to "jump"),
                    highlightedIndices = setOf(checkIdx),
                    eliminatedIndices = (0..prev).toSet()
                )
            )
            prev = step
            step += m
            if (prev >= n) break
        }

        var foundIndex = -1
        val bound = min(step, n)
        for (i in prev until bound) {
            val isMatch = arr[i] == target
            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 6,
                    title = "Linear Backward Scan at [$i] = ${arr[i]}",
                    description = if (isMatch) "Found target $target at index [$i]!" else "arr[$i] (${arr[i]}) != $target, check next.",
                    actionType = if (isMatch) "FOUND" else "COMPARE",
                    variables = mapOf("index" to "$i", "val" to "${arr[i]}", "target" to "$target"),
                    vizType = DsaVizType.SEARCH,
                    arrayElements = arr,
                    targetValue = "$target",
                    pointerLabels = mapOf(i to if (isMatch) "FOUND" else "scan"),
                    highlightedIndices = setOf(i),
                    isFound = isMatch
                )
            )
            if (isMatch) {
                foundIndex = i
                break
            }
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = if (foundIndex != -1) "Jump Search Success!" else "Jump Search Completed (Not Found)",
                description = if (foundIndex != -1) "Found target $target in O(√N) time." else "Target not present in array.",
                actionType = "COMPLETE",
                variables = mapOf("foundIndex" to "$foundIndex", "blockSize" to "$m"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = if (foundIndex != -1) mapOf(foundIndex to "FOUND") else emptyMap(),
                highlightedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                sortedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                isCompleted = true,
                summaryResult = if (foundIndex != -1) "Target found at index [$foundIndex] in O(√N) time." else "Target not found."
            )
        )

        return steps
    }

    // 4. Interpolation Search
    fun generateInterpolationSearch(customInput: String?): List<DsaVisualizerStep> {
        val (rawArr, target) = parseArrayAndTarget(customInput, listOf(10, 20, 30, 40, 50, 60, 70, 80), 50)
        val arr = rawArr.sorted()
        val steps = mutableListOf<DsaVisualizerStep>()

        var low = 0
        var high = arr.size - 1
        var foundIndex = -1

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Interpolation Search: Uniform Distribution Probing",
                description = "Formula: pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low]). O(log log N) average.",
                actionType = "INITIALIZE",
                variables = mapOf("low" to "$low", "high" to "$high", "target" to "$target"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = mapOf(low to "L", high to "H"),
                highlightedIndices = setOf(low, high)
            )
        )

        while (low <= high && target >= arr[low] && target <= arr[high] && steps.size < 6) {
            val pos = if (arr[high] == arr[low]) low else low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low])
            val posClamped = pos.coerceIn(0, arr.size - 1)
            val isMatch = arr[posClamped] == target

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 5,
                    title = "Calculated Probe Position pos = [$posClamped] (Val: ${arr[posClamped]})",
                    description = if (isMatch) "Direct hit! arr[$posClamped] == $target." else if (arr[posClamped] < target) "arr[$posClamped] < $target. Adjust low = ${posClamped + 1}." else "arr[$posClamped] > $target. Adjust high = ${posClamped - 1}.",
                    actionType = if (isMatch) "FOUND" else "COMPARE",
                    variables = mapOf("pos" to "$posClamped", "val" to "${arr[posClamped]}", "target" to "$target"),
                    vizType = DsaVizType.SEARCH,
                    arrayElements = arr,
                    targetValue = "$target",
                    pointerLabels = mapOf(low to "L", posClamped to "probe", high to "H"),
                    highlightedIndices = setOf(posClamped),
                    isFound = isMatch
                )
            )

            if (isMatch) {
                foundIndex = posClamped
                break
            } else if (arr[posClamped] < target) {
                low = posClamped + 1
            } else {
                high = posClamped - 1
            }
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = if (foundIndex != -1) "Target Found at Index [$foundIndex]!" else "Interpolation Search Finished",
                description = if (foundIndex != -1) "Interpolation search converged in O(log log N) steps." else "Target value not in range.",
                actionType = "COMPLETE",
                variables = mapOf("target" to "$target", "foundIndex" to "$foundIndex"),
                vizType = DsaVizType.SEARCH,
                arrayElements = arr,
                targetValue = "$target",
                pointerLabels = if (foundIndex != -1) mapOf(foundIndex to "FOUND") else emptyMap(),
                highlightedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                sortedIndices = if (foundIndex != -1) setOf(foundIndex) else emptySet(),
                isCompleted = true,
                summaryResult = if (foundIndex != -1) "Target $target found at index [$foundIndex]." else "Target not present."
            )
        )

        return steps
    }
}
