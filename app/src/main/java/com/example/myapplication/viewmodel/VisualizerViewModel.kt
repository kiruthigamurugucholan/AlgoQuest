package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.LearningRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VizStepState(
    val array: List<Int>,
    val pointer1: Int, // e.g. low or left or j
    val pointer2: Int, // e.g. high or right or j+1
    val pointer3: Int = -1, // e.g. mid
    val pointerLabels: Map<Int, String> = emptyMap(),
    val description: String,
    val activeLine: Int,
    val isFound: Boolean = false,
    val isSwap: Boolean = false,
    val eliminatedIndices: Set<Int> = emptySet()
)

data class AlgorithmMeta(
    val name: String,
    val slug: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val complexityNote: String,
    val codeLines: List<String>
)

class VisualizerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LearningRepository(application)

    private val _selectedAlg = MutableStateFlow(0) // 0: Binary Search, 1: Bubble Sort, 2: Two Pointers
    val selectedAlg: StateFlow<Int> = _selectedAlg.asStateFlow()

    private val _currentStepIdx = MutableStateFlow(0)
    val currentStepIdx: StateFlow<Int> = _currentStepIdx.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _speed = MutableStateFlow(1f) // 0.5f, 1f, 2f
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private var autoPlayJob: Job? = null

    // Algorithm metadata definitions
    val algMetadata = listOf(
        AlgorithmMeta(
            name = "Binary Search",
            slug = "binary-search",
            timeComplexity = "O(log N)",
            spaceComplexity = "O(1)",
            complexityNote = "Halves search space each step",
            codeLines = listOf(
                "fun binarySearch(arr: IntArray, target: Int): Int {",
                "    var low = 0; var high = arr.size - 1",
                "    while (low <= high) {",
                "        val mid = low + (high - low) / 2",
                "        if (arr[mid] == target) return mid",
                "        else if (arr[mid] < target) low = mid + 1",
                "        else high = mid - 1",
                "    }",
                "    return -1",
                "}"
            )
        ),
        AlgorithmMeta(
            name = "Bubble Sort",
            slug = "bubble-sort",
            timeComplexity = "O(N^2)",
            spaceComplexity = "O(1)",
            complexityNote = "Pairs swapped in nested passes",
            codeLines = listOf(
                "fun bubbleSort(arr: IntArray) {",
                "    val n = arr.size",
                "    for (i in 0 until n - 1) {",
                "        for (j in 0 until n - i - 1) {",
                "            if (arr[j] > arr[j + 1]) {",
                "                swap(arr, j, j + 1)",
                "            }",
                "        }",
                "    }",
                "}"
            )
        ),
        AlgorithmMeta(
            name = "Two Pointers",
            slug = "two-pointers",
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            complexityNote = "Linear inward scan on sorted data",
            codeLines = listOf(
                "fun twoSumSorted(arr: IntArray, target: Int): Pair<Int, Int>? {",
                "    var left = 0; var right = arr.size - 1",
                "    while (left < right) {",
                "        val sum = arr[left] + arr[right]",
                "        if (sum == target) return Pair(left, right)",
                "        else if (sum < target) left++",
                "        else right--",
                "    }",
                "    return null",
                "}"
            )
        )
    )

    // Precomputed deterministic steps
    private val binarySearchSteps: List<VizStepState> by lazy {
        val arr = listOf(2, 5, 8, 12, 16, 23, 38, 56, 72, 91)
        val target = 23
        listOf(
            VizStepState(arr, 0, 9, 4, mapOf(0 to "L", 9 to "H", 4 to "M"), "Initial state: target = $target. Set low = 0 (val 2), high = 9 (val 91).", 1),
            VizStepState(arr, 0, 9, 4, mapOf(0 to "L", 9 to "H", 4 to "M"), "Calculate mid = (0 + 9) / 2 = 4 (value 16). Compare 16 with $target.", 3),
            VizStepState(arr, 0, 9, 4, mapOf(0 to "L", 9 to "H", 4 to "M"), "16 < $target: Target must be in right half. Set low = mid + 1 = 5.", 5, eliminatedIndices = (0..4).toSet()),
            VizStepState(arr, 5, 9, 7, mapOf(5 to "L", 9 to "H", 7 to "M"), "New range [5..9]. Calculate mid = (5 + 9) / 2 = 7 (value 56).", 3, eliminatedIndices = (0..4).toSet()),
            VizStepState(arr, 5, 9, 7, mapOf(5 to "L", 9 to "H", 7 to "M"), "56 > $target: Target must be in left half. Set high = mid - 1 = 6.", 6, eliminatedIndices = (0..4).plus(7..9).toSet()),
            VizStepState(arr, 5, 6, 5, mapOf(5 to "L M", 6 to "H"), "New range [5..6]. Calculate mid = (5 + 6) / 2 = 5 (value 23).", 3, eliminatedIndices = (0..4).plus(7..9).toSet()),
            VizStepState(arr, 5, 6, 5, mapOf(5 to "FOUND!"), "23 == $target! Target found at index 5 in 3 iterations!", 4, isFound = true, eliminatedIndices = (0..4).plus(7..9).toSet())
        )
    }

    private val bubbleSortSteps: List<VizStepState> by lazy {
        listOf(
            VizStepState(listOf(25, 12, 8, 34, 19, 6), 0, 1, description = "Pass 1: Compare arr[0] (25) and arr[1] (12). 25 > 12 -> Swap!", activeLine = 4, isSwap = true, pointerLabels = mapOf(0 to "j", 1 to "j+1")),
            VizStepState(listOf(12, 25, 8, 34, 19, 6), 1, 2, description = "Compare arr[1] (25) and arr[2] (8). 25 > 8 -> Swap!", activeLine = 5, isSwap = true, pointerLabels = mapOf(1 to "j", 2 to "j+1")),
            VizStepState(listOf(12, 8, 25, 34, 19, 6), 2, 3, description = "Compare arr[2] (25) and arr[3] (34). 25 < 34 -> No swap needed.", activeLine = 4, pointerLabels = mapOf(2 to "j", 3 to "j+1")),
            VizStepState(listOf(12, 8, 25, 19, 34, 6), 3, 4, description = "Compare arr[3] (34) and arr[4] (19). 34 > 19 -> Swap!", activeLine = 5, isSwap = true, pointerLabels = mapOf(3 to "j", 4 to "j+1")),
            VizStepState(listOf(12, 8, 25, 19, 6, 34), 4, 5, description = "Compare arr[4] (34) and arr[5] (6). 34 > 6 -> Swap! 34 is now in its sorted position.", activeLine = 5, isSwap = true, pointerLabels = mapOf(5 to "Sorted"), isFound = true)
        )
    }

    private val twoPointersSteps: List<VizStepState> by lazy {
        val arr = listOf(1, 3, 4, 7, 10, 14, 18, 22)
        val target = 17
        listOf(
            VizStepState(arr, 0, 7, pointerLabels = mapOf(0 to "L", 7 to "R"), description = "Target = $target. Set Left = 0 (val 1), Right = 7 (val 22). Sum = 1 + 22 = 23.", activeLine = 1),
            VizStepState(arr, 0, 7, pointerLabels = mapOf(0 to "L", 7 to "R"), description = "Sum (23) > Target (17). Sum too large: Move Right pointer left (Right = 6).", activeLine = 6),
            VizStepState(arr, 0, 6, pointerLabels = mapOf(0 to "L", 6 to "R"), description = "Left = 0 (val 1), Right = 6 (val 18). Sum = 1 + 18 = 19 > 17. Move Right pointer left (Right = 5).", activeLine = 6),
            VizStepState(arr, 0, 5, pointerLabels = mapOf(0 to "L", 5 to "R"), description = "Left = 0 (val 1), Right = 5 (val 14). Sum = 1 + 14 = 15 < 17. Sum too small: Move Left pointer right (Left = 1).", activeLine = 5),
            VizStepState(arr, 1, 5, pointerLabels = mapOf(1 to "L", 5 to "R"), description = "Left = 1 (val 3), Right = 5 (val 14). Sum = 3 + 14 = 17 == Target! Match found at indices [1, 5].", activeLine = 4, isFound = true)
        )
    }

    fun getCurrentSteps(): List<VizStepState> = when (_selectedAlg.value) {
        0 -> binarySearchSteps
        1 -> bubbleSortSteps
        else -> twoPointersSteps
    }

    fun getCurrentStep(): VizStepState {
        val steps = getCurrentSteps()
        val idx = _currentStepIdx.value.coerceIn(0, steps.size - 1)
        return steps[idx]
    }

    fun selectAlgorithm(idx: Int) {
        _selectedAlg.value = idx
        _currentStepIdx.value = 0
        pause()
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        _isPlaying.value = true
        autoPlayJob?.cancel()
        autoPlayJob = viewModelScope.launch {
            val steps = getCurrentSteps()
            while (_currentStepIdx.value < steps.size - 1 && _isPlaying.value) {
                delay((1500 / _speed.value).toLong())
                _currentStepIdx.value++
            }
            _isPlaying.value = false
        }
    }

    fun pause() {
        _isPlaying.value = false
        autoPlayJob?.cancel()
    }

    fun stepNext() {
        val steps = getCurrentSteps()
        if (_currentStepIdx.value < steps.size - 1) {
            _currentStepIdx.value++
        }
    }

    fun stepPrev() {
        if (_currentStepIdx.value > 0) {
            _currentStepIdx.value--
        }
    }

    fun reset() {
        pause()
        _currentStepIdx.value = 0
    }

    fun toggleSpeed() {
        _speed.value = when (_speed.value) {
            1f -> 2f
            2f -> 0.5f
            else -> 1f
        }
    }

    fun completeVisualizerActivity(slug: String) {
        viewModelScope.launch {
            // Lesson 3 is the visualizer lab in chapter 3
            repository.completeLesson(3)
        }
    }
}

