package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType

object SortingVisualizerGenerator {

    private fun parseArrayInput(input: String?, defaultArr: List<Int>): List<Int> {
        if (input.isNullOrBlank()) return defaultArr
        val parsed = input.split(",").mapNotNull { it.trim().toIntOrNull() }
        return if (parsed.size in 2..8) parsed else defaultArr
    }

    // 1. Bubble Sort
    fun generateBubbleSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(29, 10, 14, 37, 13))
        val arr = initialArr.toMutableList()
        val steps = mutableListOf<DsaVisualizerStep>()
        val sortedSet = mutableSetOf<Int>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 10,
                title = "Bubble Sort: Adjacent Pair Comparisons",
                description = "Iterate through array, comparing adjacent pairs (arr[j], arr[j+1]) and bubbling larger values right.",
                actionType = "INITIALIZE",
                variables = mapOf("pass" to "1", "swaps" to "0"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.toList(),
                pointerLabels = mapOf(0 to "j", 1 to "j+1"),
                highlightedIndices = setOf(0, 1)
            )
        )

        var pass = 1
        var totalSwaps = 0
        val n = arr.size

        for (i in 0 until n - 1) {
            var swappedInPass = false
            for (j in 0 until n - i - 1) {
                val shouldSwap = arr[j] > arr[j + 1]

                if (shouldSwap) {
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    swappedInPass = true
                    totalSwaps++

                    steps.add(
                        DsaVisualizerStep(
                            stepIndex = steps.size,
                            totalSteps = 10,
                            title = "Swap arr[$j] (${arr[j+1]}) & arr[${j+1}] (${arr[j]})",
                            description = "Swapped adjacent pair because ${arr[j+1]} > ${arr[j]}.",
                            actionType = "SWAP",
                            variables = mapOf("pass" to "$pass", "totalSwaps" to "$totalSwaps"),
                            vizType = DsaVizType.SORT,
                            arrayElements = arr.toList(),
                            pointerLabels = mapOf(j to "j", j + 1 to "j+1"),
                            highlightedIndices = setOf(j, j + 1),
                            swappedIndices = Pair(j, j + 1),
                            sortedIndices = sortedSet.toSet()
                        )
                    )
                } else {
                    steps.add(
                        DsaVisualizerStep(
                            stepIndex = steps.size,
                            totalSteps = 10,
                            title = "Compare arr[$j] (${arr[j]}) <= arr[${j+1}] (${arr[j+1]})",
                            description = "In correct order (${arr[j]} <= ${arr[j+1]}). No swap needed.",
                            actionType = "COMPARE",
                            variables = mapOf("pass" to "$pass", "totalSwaps" to "$totalSwaps"),
                            vizType = DsaVizType.SORT,
                            arrayElements = arr.toList(),
                            pointerLabels = mapOf(j to "j", j + 1 to "j+1"),
                            highlightedIndices = setOf(j, j + 1),
                            sortedIndices = sortedSet.toSet()
                        )
                    )
                }

                if (steps.size >= 12) break
            }
            sortedSet.add(n - i - 1)
            pass++
            if (!swappedInPass || steps.size >= 12) break
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = "Bubble Sort Complete",
                description = "Array sorted in non-decreasing order with $totalSwaps swaps.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SORTED", "totalSwaps" to "$totalSwaps"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.sorted(),
                sortedIndices = arr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Array successfully sorted in O(N^2) time with $totalSwaps total swaps."
            )
        )

        return steps
    }

    // 2. Selection Sort
    fun generateSelectionSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(64, 25, 12, 22, 11))
        val arr = initialArr.toMutableList()
        val steps = mutableListOf<DsaVisualizerStep>()
        val n = arr.size
        val sortedSet = mutableSetOf<Int>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 8,
                title = "Selection Sort: Repeated Minimum Selection",
                description = "Find the minimum element in unsorted subarray [i..${n - 1}] and swap it into position i.",
                actionType = "INITIALIZE",
                variables = mapOf("i" to "0", "minIdx" to "0"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.toList(),
                pointerLabels = mapOf(0 to "i", 0 to "min"),
                highlightedIndices = setOf(0)
            )
        )

        for (i in 0 until n - 1) {
            var minIdx = i
            for (j in i + 1 until n) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j
                }
            }

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 8,
                    title = "Found Minimum in Suffix: arr[$minIdx] = ${arr[minIdx]}",
                    description = "Minimum element in unsorted subarray [${i}..${n - 1}] is ${arr[minIdx]} at index [$minIdx].",
                    actionType = "FIND_MIN",
                    variables = mapOf("i" to "$i", "minIdx" to "$minIdx", "minVal" to "${arr[minIdx]}"),
                    vizType = DsaVizType.SORT,
                    arrayElements = arr.toList(),
                    pointerLabels = mapOf(i to "i", minIdx to "min"),
                    highlightedIndices = setOf(i, minIdx),
                    sortedIndices = sortedSet.toSet()
                )
            )

            // Swap min with arr[i]
            val temp = arr[minIdx]
            arr[minIdx] = arr[i]
            arr[i] = temp
            sortedSet.add(i)

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 8,
                    title = "Swap arr[$i] and arr[$minIdx]",
                    description = "Moved minimum ${arr[i]} into sorted position index [$i].",
                    actionType = "SWAP",
                    variables = mapOf("placedAt" to "$i", "value" to "${arr[i]}"),
                    vizType = DsaVizType.SORT,
                    arrayElements = arr.toList(),
                    pointerLabels = mapOf(i to "sorted"),
                    highlightedIndices = setOf(i),
                    swappedIndices = Pair(i, minIdx),
                    sortedIndices = sortedSet.toSet()
                )
            )

            if (steps.size >= 10) break
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = "Selection Sort Completed",
                description = "All $n elements placed in sorted order with exactly ${n - 1} swaps.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SORTED", "comparisons" to "O(N^2)"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.sorted(),
                sortedIndices = arr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Selection sort completed in O(N^2) comparisons and O(N) swaps."
            )
        )

        return steps
    }

    // 3. Insertion Sort
    fun generateInsertionSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(12, 11, 13, 5, 6))
        val arr = initialArr.toMutableList()
        val steps = mutableListOf<DsaVisualizerStep>()
        val sortedSet = mutableSetOf(0)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 8,
                title = "Insertion Sort: Growing Sorted Prefix",
                description = "arr[0] (${arr[0]}) is trivially sorted. Elements from index 1 will be sifted into the prefix.",
                actionType = "INITIALIZE",
                variables = mapOf("key" to "none", "sortedPrefix" to "[0]"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.toList(),
                pointerLabels = mapOf(0 to "sorted"),
                sortedIndices = sortedSet.toSet()
            )
        )

        for (i in 1 until arr.size) {
            val key = arr[i]
            var j = i - 1

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 8,
                    title = "Pick Key: arr[$i] = $key",
                    description = "Extract key $key from index [$i]. Compare backwards into sorted prefix [0..${i - 1}].",
                    actionType = "PICK_KEY",
                    variables = mapOf("i" to "$i", "key" to "$key"),
                    vizType = DsaVizType.SORT,
                    arrayElements = arr.toList(),
                    pointerLabels = mapOf(i to "key"),
                    highlightedIndices = setOf(i),
                    sortedIndices = sortedSet.toSet()
                )
            )

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j]
                steps.add(
                    DsaVisualizerStep(
                        stepIndex = steps.size,
                        totalSteps = 8,
                        title = "Shift arr[$j] (${arr[j]}) Right",
                        description = "${arr[j]} > $key, so shift it right to index [${j + 1}].",
                        actionType = "SHIFT",
                        variables = mapOf("shiftFrom" to "$j", "shiftTo" to "${j + 1}", "key" to "$key"),
                        vizType = DsaVizType.SORT,
                        arrayElements = arr.toList(),
                        pointerLabels = mapOf(j to "shift", j + 1 to "dest"),
                        highlightedIndices = setOf(j, j + 1),
                        sortedIndices = sortedSet.toSet()
                    )
                )
                j--
                if (steps.size >= 10) break
            }
            arr[j + 1] = key
            sortedSet.add(i)

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 8,
                    title = "Insert Key $key at Index [${j + 1}]",
                    description = "Found sorted slot. Key $key inserted at index [${j + 1}].",
                    actionType = "INSERT",
                    variables = mapOf("slot" to "${j + 1}", "key" to "$key"),
                    vizType = DsaVizType.SORT,
                    arrayElements = arr.toList(),
                    pointerLabels = mapOf(j + 1 to "inserted"),
                    highlightedIndices = setOf(j + 1),
                    sortedIndices = (0..i).toSet()
                )
            )

            if (steps.size >= 10) break
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = "Insertion Sort Complete",
                description = "Prefix expanded across entire array. Adaptive O(N) best case, O(N^2) average.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SORTED"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.sorted(),
                sortedIndices = arr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Insertion sort completed successfully."
            )
        )

        return steps
    }

    // 4. Merge Sort
    fun generateMergeSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(38, 27, 43, 3, 9, 82))
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Merge Sort: Divide and Conquer",
                description = "Recursively divide array into halves until single-element subarrays, then merge back in sorted order.",
                actionType = "INITIALIZE",
                variables = mapOf("size" to "${initialArr.size}", "mid" to "${initialArr.size / 2}"),
                vizType = DsaVizType.SORT,
                arrayElements = initialArr,
                pointerLabels = mapOf(0 to "L", initialArr.size / 2 to "mid", initialArr.size - 1 to "R"),
                highlightedIndices = setOf(0, initialArr.size - 1)
            )
        )

        // Split step
        val mid = initialArr.size / 2
        val leftHalf = initialArr.subList(0, mid)
        val rightHalf = initialArr.subList(mid, initialArr.size)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 6,
                title = "Divide: Left Half $leftHalf vs Right Half $rightHalf",
                description = "Split into subproblems: Left [0..${mid - 1}] and Right [$mid..${initialArr.size - 1}].",
                actionType = "DIVIDE",
                variables = mapOf("leftHalf" to "$leftHalf", "rightHalf" to "$rightHalf"),
                vizType = DsaVizType.SORT,
                arrayElements = initialArr,
                highlightedIndices = (0 until mid).toSet(),
                eliminatedIndices = (mid until initialArr.size).toSet()
            )
        )

        val sortedLeft = leftHalf.sorted()
        val sortedRight = rightHalf.sorted()
        val mergedHalf = sortedLeft + sortedRight

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 6,
                title = "Conquer: Subarrays Sorted Separately",
                description = "Left sorted: $sortedLeft | Right sorted: $sortedRight. Ready for 2-pointer merge.",
                actionType = "CONQUER",
                variables = mapOf("sortedLeft" to "$sortedLeft", "sortedRight" to "$sortedRight"),
                vizType = DsaVizType.SORT,
                arrayElements = mergedHalf,
                pointerLabels = mapOf(0 to "i", mid to "j"),
                highlightedIndices = setOf(0, mid)
            )
        )

        // Merge step
        val fullySorted = initialArr.sorted()
        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 6,
                title = "Merge: Two-Finger Pointer Interleaving",
                description = "Compare front elements of left and right halves; pick smaller element into auxiliary buffer in O(N) time.",
                actionType = "MERGE",
                variables = mapOf("buffer" to "$fullySorted"),
                vizType = DsaVizType.SORT,
                arrayElements = fullySorted,
                sortedIndices = fullySorted.indices.toSet(),
                isCompleted = true,
                summaryResult = "Merge sort completed in guaranteed O(N log N) time and O(N) auxiliary space."
            )
        )

        return steps
    }

    // 5. Quick Sort
    fun generateQuickSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(10, 80, 30, 90, 40, 50, 70))
        val steps = mutableListOf<DsaVisualizerStep>()
        val arr = initialArr.toMutableList()
        val pivotIdx = arr.size - 1
        val pivotVal = arr[pivotIdx]

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Quick Sort: Lomuto Partitioning",
                description = "Select pivot element arr[$pivotIdx] = $pivotVal. Partition array: elements < pivot left, elements > pivot right.",
                actionType = "INITIALIZE",
                variables = mapOf("pivot" to "$pivotVal", "pivotIndex" to "$pivotIdx"),
                vizType = DsaVizType.SORT,
                arrayElements = arr.toList(),
                pointerLabels = mapOf(0 to "i", pivotIdx to "pivot"),
                highlightedIndices = setOf(pivotIdx)
            )
        )

        val smaller = arr.filter { it < pivotVal }
        val larger = arr.filter { it > pivotVal }
        val partitioned = smaller + listOf(pivotVal) + larger
        val placedPivotIdx = smaller.size

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Partitioning: Sifting around Pivot $pivotVal",
                description = "Elements smaller than pivot ($smaller) placed left. Elements larger ($larger) placed right.",
                actionType = "PARTITION",
                variables = mapOf("leftPart" to "$smaller", "pivot" to "$pivotVal", "rightPart" to "$larger"),
                vizType = DsaVizType.SORT,
                arrayElements = partitioned,
                pointerLabels = mapOf(placedPivotIdx to "pivotFixed"),
                highlightedIndices = setOf(placedPivotIdx),
                sortedIndices = setOf(placedPivotIdx)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Pivot $pivotVal Fixed in Final Position [$placedPivotIdx]",
                description = "Pivot $pivotVal is now at its exact final sorted index. Recurse on left and right partitions.",
                actionType = "RECURSE",
                variables = mapOf("fixedIndex" to "$placedPivotIdx"),
                vizType = DsaVizType.SORT,
                arrayElements = partitioned,
                sortedIndices = setOf(placedPivotIdx)
            )
        )

        val fullySorted = initialArr.sorted()
        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Quick Sort Complete",
                description = "All sub-partitions recursively resolved in O(N log N) average time.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SORTED", "time" to "O(N log N)"),
                vizType = DsaVizType.SORT,
                arrayElements = fullySorted,
                sortedIndices = fullySorted.indices.toSet(),
                isCompleted = true,
                summaryResult = "Quick sort partitioned and ordered array in O(N log N) average time."
            )
        )

        return steps
    }

    // 6. Heap Sort
    fun generateHeapSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(4, 10, 3, 5, 1))
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Heap Sort: Phase 1 – Build Max-Heap",
                description = "Convert array into Max-Heap where parent >= children (parent at i, children at 2i+1, 2i+2).",
                actionType = "BUILD_HEAP",
                variables = mapOf("size" to "${initialArr.size}", "heapType" to "MAX_HEAP"),
                vizType = DsaVizType.SORT,
                arrayElements = initialArr,
                highlightedIndices = setOf(0)
            )
        )

        val maxHeap = listOf(10, 5, 3, 4, 1) // max heap of (4, 10, 3, 5, 1)
        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Max-Heap Built: Root arr[0] = 10 is Global Maximum",
                description = "Root node holds largest element. Swap root with last element and shrink active heap boundary.",
                actionType = "HEAP_MAX",
                variables = mapOf("root" to "${maxHeap[0]}", "last" to "${maxHeap.last()}"),
                vizType = DsaVizType.SORT,
                arrayElements = maxHeap,
                pointerLabels = mapOf(0 to "maxRoot", maxHeap.size - 1 to "last"),
                highlightedIndices = setOf(0, maxHeap.size - 1)
            )
        )

        val sorted = initialArr.sorted()
        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Heap Sort Complete: Sift Down Iterations",
                description = "Repeatedly extracted max root to back of array in O(N log N) guaranteed in-place time.",
                actionType = "COMPLETE",
                variables = mapOf("auxSpace" to "O(1) in-place", "time" to "O(N log N)"),
                vizType = DsaVizType.SORT,
                arrayElements = sorted,
                sortedIndices = sorted.indices.toSet(),
                isCompleted = true,
                summaryResult = "Heap sort completed in guaranteed O(N log N) time without extra memory."
            )
        )

        return steps
    }

    // 7. Counting Sort
    fun generateCountingSort(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(4, 2, 2, 8, 3, 3, 1))
        val steps = mutableListOf<DsaVisualizerStep>()
        val maxVal = initialArr.maxOrNull() ?: 8

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Counting Sort: Non-Comparative Frequency Array",
                description = "Find max value ($maxVal). Allocate count array of size ${maxVal + 1} initialized to 0.",
                actionType = "INITIALIZE",
                variables = mapOf("maxVal" to "$maxVal", "countArraySize" to "${maxVal + 1}"),
                vizType = DsaVizType.SORT,
                arrayElements = initialArr,
                highlightedIndices = setOf(0)
            )
        )

        val freqMap = initialArr.groupingBy { it }.eachCount()
        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Frequency Histogram Populated",
                description = "Frequencies: ${freqMap.entries.sortedBy { it.key }.joinToString { "${it.key}:${it.value}" }}.",
                actionType = "COUNT",
                variables = freqMap.mapKeys { "count[${it.key}]" }.mapValues { "${it.value}" },
                vizType = DsaVizType.SORT,
                arrayElements = initialArr,
                sortedIndices = emptySet()
            )
        )

        val sorted = initialArr.sorted()
        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Reconstruct Output Array from Counts",
                description = "Traverse count array and populate sorted elements directly in linear O(N + K) time.",
                actionType = "COMPLETE",
                variables = mapOf("time" to "O(N + K)", "space" to "O(K)"),
                vizType = DsaVizType.SORT,
                arrayElements = sorted,
                sortedIndices = sorted.indices.toSet(),
                isCompleted = true,
                summaryResult = "Counting sort reconstructed array in linear O(N + K) time."
            )
        )

        return steps
    }
}
