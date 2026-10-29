package com.example.myapplication

import com.example.myapplication.data.algorithm.AlgorithmCodeTemplate
import com.example.myapplication.data.algorithm.AlgorithmEngine
import com.example.myapplication.data.algorithm.AlgorithmType
import com.example.myapplication.data.algorithm.CodeLanguage
import com.example.myapplication.data.algorithm.CodeTemplateRegistry
import com.example.myapplication.data.algorithm.CodeValidationResult
import com.example.myapplication.data.algorithm.CodeValidator
import com.example.myapplication.data.algorithm.ExecutionEngine
import com.example.myapplication.data.algorithm.InputValidator
import com.example.myapplication.data.algorithm.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlgorithmEngineTest {

    // --- 1. Binary Search Tests ---

    @Test
    fun binarySearch_findsTarget_successfully() {
        val arr = listOf(2, 5, 8, 12, 16, 23, 38, 56, 72, 91)
        val target = 23
        val steps = AlgorithmEngine.generateBinarySearchSteps(arr, target)

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertTrue("Final step must be marked as found", finalStep.isFound)
        assertTrue("Final step must be marked as completed", finalStep.isCompleted)
        assertEquals("Mid pointer should point to index 5", 5, finalStep.pointer3)
        assertEquals(23, finalStep.array[finalStep.pointer3])
    }

    @Test
    fun binarySearch_targetNotFound() {
        val arr = listOf(2, 5, 8, 12, 16, 23, 38)
        val target = 99
        val steps = AlgorithmEngine.generateBinarySearchSteps(arr, target)

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertFalse("Final step must not be found", finalStep.isFound)
        assertTrue("Final step must be marked completed", finalStep.isCompleted)
        assertTrue("All elements should be marked eliminated", finalStep.eliminatedIndices.containsAll(arr.indices.toList()))
    }

    @Test
    fun binarySearch_handlesDuplicates() {
        val arr = listOf(1, 3, 3, 3, 7, 9)
        val target = 3
        val steps = AlgorithmEngine.generateBinarySearchSteps(arr, target)

        val finalStep = steps.last()
        assertTrue(finalStep.isFound)
        assertEquals(3, arr[finalStep.pointer3])
    }

    @Test
    fun binarySearch_singleElement_foundAndNotFound() {
        val single = listOf(42)
        val stepsFound = AlgorithmEngine.generateBinarySearchSteps(single, 42)
        assertTrue(stepsFound.last().isFound)

        val stepsNotFound = AlgorithmEngine.generateBinarySearchSteps(single, 10)
        assertFalse(stepsNotFound.last().isFound)
    }

    // --- 2. Bubble Sort Tests ---

    @Test
    fun bubbleSort_sortsUnsortedArray() {
        val arr = listOf(25, 12, 8, 34, 19, 6)
        val steps = AlgorithmEngine.generateBubbleSortSteps(arr)

        assertTrue(steps.isNotEmpty())
        val finalStep = steps.last()
        assertTrue(finalStep.isCompleted)
        assertEquals(listOf(6, 8, 12, 19, 25, 34), finalStep.array)
    }

    @Test
    fun bubbleSort_alreadySorted_terminatesEarly() {
        val sorted = listOf(1, 2, 3, 4, 5)
        val steps = AlgorithmEngine.generateBubbleSortSteps(sorted)

        val finalStep = steps.last()
        assertEquals(sorted, finalStep.array)
        assertTrue(finalStep.isCompleted)
    }

    @Test
    fun bubbleSort_reverseSorted() {
        val reverse = listOf(5, 4, 3, 2, 1)
        val steps = AlgorithmEngine.generateBubbleSortSteps(reverse)

        val finalStep = steps.last()
        assertEquals(listOf(1, 2, 3, 4, 5), finalStep.array)
    }

    @Test
    fun bubbleSort_withDuplicates() {
        val withDups = listOf(4, 2, 2, 4, 1, 3)
        val steps = AlgorithmEngine.generateBubbleSortSteps(withDups)

        val finalStep = steps.last()
        assertEquals(listOf(1, 2, 2, 3, 4, 4), finalStep.array)
    }

    // --- 3. Two Pointers Tests ---

    @Test
    fun twoPointers_findsTargetSum_successfully() {
        val arr = listOf(1, 3, 4, 7, 10, 14, 18, 22)
        val targetSum = 17 // 3 + 14 = 17 -> indices 1 and 5
        val steps = AlgorithmEngine.generateTwoPointersSteps(arr, targetSum)

        val finalStep = steps.last()
        assertTrue("Target sum should be found", finalStep.isFound)
        assertEquals(1, finalStep.pointer1)
        assertEquals(5, finalStep.pointer2)
        assertEquals(targetSum, arr[finalStep.pointer1] + arr[finalStep.pointer2])
    }

    @Test
    fun twoPointers_targetSumNotFound() {
        val arr = listOf(1, 2, 4, 8, 16)
        val targetSum = 13 // No pair sums to 13
        val steps = AlgorithmEngine.generateTwoPointersSteps(arr, targetSum)

        val finalStep = steps.last()
        assertFalse(finalStep.isFound)
        assertTrue(finalStep.isCompleted)
    }

    @Test
    fun twoPointers_negativeNumbers() {
        val arr = listOf(-10, -5, 0, 3, 7, 12)
        val targetSum = 2 // -5 + 7 = 2
        val steps = AlgorithmEngine.generateTwoPointersSteps(arr, targetSum)

        val finalStep = steps.last()
        assertTrue(finalStep.isFound)
        assertEquals(targetSum, arr[finalStep.pointer1] + arr[finalStep.pointer2])
    }

    // --- 4. Input Validator Tests ---

    @Test
    fun validator_acceptsValidInput() {
        val res = InputValidator.validate("10, 20, 30, 40", "30", AlgorithmType.BINARY_SEARCH)
        assertTrue(res is ValidationResult.Success)
        val success = res as ValidationResult.Success
        assertEquals(listOf(10, 20, 30, 40), success.array)
        assertEquals(30, success.target)
    }

    @Test
    fun validator_autoSortsWhenNeeded() {
        val res = InputValidator.validate("40, 10, 30, 20", "30", AlgorithmType.BINARY_SEARCH, autoSortIfRequired = true)
        assertTrue(res is ValidationResult.Success)
        val success = res as ValidationResult.Success
        assertEquals(listOf(10, 20, 30, 40), success.array)
    }

    @Test
    fun validator_rejectsInvalidNonInteger() {
        val res = InputValidator.validate("10, abc, 30", "30", AlgorithmType.BINARY_SEARCH)
        assertTrue(res is ValidationResult.Error)
    }

    @Test
    fun validator_rejectsEmptyInput() {
        val res = InputValidator.validate("", "10", AlgorithmType.BINARY_SEARCH)
        assertTrue(res is ValidationResult.Error)
    }

    @Test
    fun validator_rejectsMissingTargetForSearch() {
        val res = InputValidator.validate("1, 2, 3", "", AlgorithmType.BINARY_SEARCH)
        assertTrue(res is ValidationResult.Error)
    }

    // --- 5. CodeTemplateRegistry Tests ---

    @Test
    fun templateRegistry_providesValidTemplatesForAllCombinations() {
        for (algo in AlgorithmType.values()) {
            for (lang in CodeLanguage.values()) {
                val template = CodeTemplateRegistry.getTemplate(algo, lang)
                assertNotNull("Template should not be null for $algo + $lang", template)
                assertTrue("Code should not be blank for $algo + $lang", template.defaultCode.isNotBlank())
                assertTrue("Line mapping should not be empty for $algo + $lang", template.lineMapping.isNotEmpty())

                val lines = template.defaultCode.lines()
                template.lineMapping.forEach { (eventName, lineNum) ->
                    assertTrue(
                        "Line $lineNum mapped to '$eventName' must be within 1..${lines.size} in $algo ($lang)",
                        lineNum in 1..lines.size
                    )
                }
            }
        }
    }

    // --- 6. CodeValidator Tests ---

    @Test
    fun codeValidator_detectsDescendingSortCondition() {
        val customKotlin = """
            fun bubbleSort(arr: IntArray) {
                val n = arr.size
                for (i in 0 until n - 1) {
                    for (j in 0 until n - i - 1) {
                        if (arr[j] < arr[j + 1]) {
                            val temp = arr[j]
                            arr[j] = arr[j + 1]
                            arr[j + 1] = temp
                        }
                    }
                }
            }
        """.trimIndent()

        val result = CodeValidator.validateCode(customKotlin, AlgorithmType.BUBBLE_SORT, CodeLanguage.KOTLIN)
        assertTrue(result is CodeValidationResult.Valid)
        val validResult = result as CodeValidationResult.Valid
        assertTrue("Should detect custom descending sort condition", validResult.isCustomDescendingSort)
    }

    @Test
    fun codeValidator_warnsOnArbitraryEmptyOrIrrelevantCode() {
        val emptyResult = CodeValidator.validateCode("", AlgorithmType.BINARY_SEARCH, CodeLanguage.KOTLIN)
        assertTrue(emptyResult is CodeValidationResult.Warning)

        val irrelevantResult = CodeValidator.validateCode("print('Hello World')", AlgorithmType.BINARY_SEARCH, CodeLanguage.PYTHON)
        assertTrue(irrelevantResult is CodeValidationResult.Warning)
    }

    // --- 7. ExecutionEngine Trace Generation Tests ---

    @Test
    fun executionEngine_binarySearch_kotlinTrace_correctLineSynchronization() {
        val template = CodeTemplateRegistry.getTemplate(AlgorithmType.BINARY_SEARCH, CodeLanguage.KOTLIN)
        val arr = listOf(10, 20, 30, 40, 50, 60, 70)
        val target = 50

        val frames = ExecutionEngine.generateTrace(
            algorithmType = AlgorithmType.BINARY_SEARCH,
            language = CodeLanguage.KOTLIN,
            userCode = template.defaultCode,
            array = arr,
            target = target
        )

        assertTrue("Execution frames must not be empty", frames.isNotEmpty())
        val codeLines = template.defaultCode.lines()

        frames.forEach { frame ->
            assertTrue("Step index must be positive", frame.stepIndex > 0)
            assertTrue(
                "Active line ${frame.activeLineNumber} must be in bounds [1..${codeLines.size}]",
                frame.activeLineNumber in 1..codeLines.size
            )
            assertTrue("Description must not be empty", frame.description.isNotEmpty())
            assertTrue("Array snapshot must match original size", frame.arraySnapshot.size == arr.size)
        }

        val finalFrame = frames.last()
        assertTrue("Binary search should terminate in found state", finalFrame.isFound)
        assertTrue("Binary search should terminate in completed state", finalFrame.isCompleted)
        assertEquals("ReturnValue should indicate target index 4", "Index 4", finalFrame.returnValue)
    }

    @Test
    fun executionEngine_binarySearch_pythonTrace_targetNotFound() {
        val template = CodeTemplateRegistry.getTemplate(AlgorithmType.BINARY_SEARCH, CodeLanguage.PYTHON)
        val arr = listOf(3, 7, 11, 19, 25)
        val target = 100

        val frames = ExecutionEngine.generateTrace(
            algorithmType = AlgorithmType.BINARY_SEARCH,
            language = CodeLanguage.PYTHON,
            userCode = template.defaultCode,
            array = arr,
            target = target
        )

        val finalFrame = frames.last()
        assertFalse("Should not be found", finalFrame.isFound)
        assertTrue("Should be completed", finalFrame.isCompleted)
        assertTrue("Eliminated indices should contain all indices", finalFrame.eliminatedIndices.containsAll(arr.indices.toList()))
        assertEquals("-1 (Not Found)", finalFrame.returnValue)
    }

    @Test
    fun executionEngine_bubbleSort_customDescendingSort() {
        val customKotlin = """
            fun bubbleSort(arr: IntArray) {
                val n = arr.size
                for (i in 0 until n - 1) {
                    var swapped = false
                    for (j in 0 until n - i - 1) {
                        if (arr[j] < arr[j + 1]) {
                            val temp = arr[j]
                            arr[j] = arr[j + 1]
                            arr[j + 1] = temp
                            swapped = true
                        }
                    }
                    if (!swapped) break
                }
            }
        """.trimIndent()

        val input = listOf(14, 3, 29, 8, 42, 1)
        val frames = ExecutionEngine.generateTrace(
            algorithmType = AlgorithmType.BUBBLE_SORT,
            language = CodeLanguage.KOTLIN,
            userCode = customKotlin,
            array = input,
            target = null
        )

        assertTrue(frames.isNotEmpty())
        val finalFrame = frames.last()
        assertTrue(finalFrame.isCompleted)
        assertEquals(listOf(42, 29, 14, 8, 3, 1), finalFrame.arraySnapshot)
    }

    @Test
    fun executionEngine_bubbleSort_javaTrace_ascendingOrder() {
        val template = CodeTemplateRegistry.getTemplate(AlgorithmType.BUBBLE_SORT, CodeLanguage.JAVA)
        val input = listOf(5, 1, 4, 2, 8)

        val frames = ExecutionEngine.generateTrace(
            algorithmType = AlgorithmType.BUBBLE_SORT,
            language = CodeLanguage.JAVA,
            userCode = template.defaultCode,
            array = input,
            target = null
        )

        assertTrue(frames.isNotEmpty())
        val finalFrame = frames.last()
        assertTrue(finalFrame.isCompleted)
        assertEquals(listOf(1, 2, 4, 5, 8), finalFrame.arraySnapshot)
        assertTrue("Total swaps count must be positive for unsorted array", finalFrame.swapsCount > 0)
        assertTrue("Comparisons count must be positive", finalFrame.comparisonsCount > 0)
    }

    @Test
    fun executionEngine_twoPointers_javaTrace_findsMatch() {
        val template = CodeTemplateRegistry.getTemplate(AlgorithmType.TWO_POINTERS, CodeLanguage.JAVA)
        val arr = listOf(2, 4, 7, 11, 15, 20)
        val targetSum = 18 // 7 + 11 = 18 (indices 2 and 3)

        val frames = ExecutionEngine.generateTrace(
            algorithmType = AlgorithmType.TWO_POINTERS,
            language = CodeLanguage.JAVA,
            userCode = template.defaultCode,
            array = arr,
            target = targetSum
        )

        assertTrue(frames.isNotEmpty())
        val finalFrame = frames.last()
        assertTrue(finalFrame.isFound)
        assertTrue(finalFrame.isCompleted)
        assertEquals(2, finalFrame.pointer1)
        assertEquals(3, finalFrame.pointer2)
        assertEquals("Pair(2, 3)", finalFrame.returnValue)
    }

    @Test
    fun executionEngine_twoPointers_pythonTrace_pairNotFound() {
        val template = CodeTemplateRegistry.getTemplate(AlgorithmType.TWO_POINTERS, CodeLanguage.PYTHON)
        val arr = listOf(1, 5, 10, 20)
        val targetSum = 99

        val frames = ExecutionEngine.generateTrace(
            algorithmType = AlgorithmType.TWO_POINTERS,
            language = CodeLanguage.PYTHON,
            userCode = template.defaultCode,
            array = arr,
            target = targetSum
        )

        assertTrue(frames.isNotEmpty())
        val finalFrame = frames.last()
        assertFalse(finalFrame.isFound)
        assertTrue(finalFrame.isCompleted)
        assertEquals("null (Not Found)", finalFrame.returnValue)
    }
}
