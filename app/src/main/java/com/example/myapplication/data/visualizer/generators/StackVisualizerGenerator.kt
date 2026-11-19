package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType

object StackVisualizerGenerator {

    // 1. Stack Operations
    fun generateStackOperations(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Stack: LIFO (Last-In, First-Out) Structure",
                description = "All operations happen strictly at the 'Top' of the stack. Push, Pop, and Peek run in O(1) time.",
                actionType = "INITIALIZE",
                variables = mapOf("top" to "-1 (EMPTY)", "capacity" to "6"),
                vizType = DsaVizType.STACK,
                stackItems = emptyList(),
                stackTopIndex = -1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Push 'Alpha' onto Stack",
                description = "Increment top pointer to 0; assign stack[0] = 'Alpha'.",
                actionType = "PUSH",
                variables = mapOf("top" to "0", "item" to "Alpha"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("Alpha"),
                stackTopIndex = 0
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Push 'Beta' & 'Gamma'",
                description = "Pushed 'Beta' (top=1), then pushed 'Gamma' (top=2). Most recent item is at the top.",
                actionType = "PUSH",
                variables = mapOf("top" to "2", "item" to "Gamma"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("Alpha", "Beta", "Gamma"),
                stackTopIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Peek Operation: Read Top without Removal",
                description = "stack.peek() returns 'Gamma' while leaving stack contents completely unchanged.",
                actionType = "PEEK",
                variables = mapOf("peeked" to "Gamma", "top" to "2"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("Alpha", "Beta", "Gamma"),
                stackTopIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 4,
                totalSteps = 5,
                title = "Pop Operation: Remove Top Element",
                description = "Popped 'Gamma'. Top pointer decrements to index 1 ('Beta').",
                actionType = "POP",
                variables = mapOf("popped" to "Gamma", "newTop" to "1 ('Beta')"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("Alpha", "Beta"),
                stackTopIndex = 1,
                isCompleted = true,
                summaryResult = "Stack operations verified in O(1) time and space."
            )
        )

        return steps
    }

    // 2. Push and Pop Mechanics
    fun generatePushPop(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Push Sequence: Fill Stack",
                description = "Pushing 10 -> 20 -> 30 sequentially into vertical stack.",
                actionType = "INITIALIZE",
                variables = mapOf("count" to "0"),
                vizType = DsaVizType.STACK,
                stackItems = emptyList(),
                stackTopIndex = -1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 6,
                title = "Push(10)",
                description = "Added 10 at bottom of stack (top = 0).",
                actionType = "PUSH",
                variables = mapOf("top" to "0", "val" to "10"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("10"),
                stackTopIndex = 0
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 6,
                title = "Push(20) & Push(30)",
                description = "Stack now holds [10, 20, 30]. Top element is 30.",
                actionType = "PUSH",
                variables = mapOf("top" to "2", "val" to "30"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("10", "20", "30"),
                stackTopIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 6,
                title = "Pop() -> 30",
                description = "LIFO: The last item pushed (30) is the first item popped.",
                actionType = "POP",
                variables = mapOf("popped" to "30", "newTop" to "1 (20)"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("10", "20"),
                stackTopIndex = 1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 4,
                totalSteps = 6,
                title = "Pop() -> 20",
                description = "Popped 20. Remaining item: [10].",
                actionType = "POP",
                variables = mapOf("popped" to "20", "newTop" to "0 (10)"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("10"),
                stackTopIndex = 0,
                isCompleted = true,
                summaryResult = "Push & Pop obey strict LIFO ordering."
            )
        )

        return steps
    }

    // 3. Infix to Postfix Conversion
    fun generateInfixToPostfix(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val expr = "A + B * C"

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Infix Expression: $expr",
                description = "Convert infix expression to postfix (Reverse Polish Notation) using operator precedence stack.",
                actionType = "INITIALIZE",
                variables = mapOf("infix" to expr, "output" to ""),
                vizType = DsaVizType.STACK,
                stackItems = emptyList(),
                stackTopIndex = -1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Read 'A' -> Output",
                description = "Operands ('A') append directly to output. Read '+' operator -> push onto operator stack.",
                actionType = "PUSH_OP",
                variables = mapOf("output" to "A", "opStack" to "[+]"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("+"),
                stackTopIndex = 0
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Read 'B' -> Output, '*' has higher precedence than '+'",
                description = "'*' pushed on top of '+' because multiplication takes precedence over addition.",
                actionType = "PUSH_OP",
                variables = mapOf("output" to "A B", "opStack" to "[+, *]"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("+", "*"),
                stackTopIndex = 1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Read 'C' -> Output, Pop Operators",
                description = "Expression end reached. Pop '*' then '+' into output string.",
                actionType = "POP_ALL",
                variables = mapOf("output" to "A B C * +", "status" to "CONVERTED"),
                vizType = DsaVizType.STACK,
                stackItems = emptyList(),
                stackTopIndex = -1,
                isCompleted = true,
                summaryResult = "Infix '$expr' converted to Postfix 'A B C * +'."
            )
        )

        return steps
    }

    // 4. Balanced Parentheses
    fun generateBalancedParentheses(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val seq = "{ [ ( ) ] }"

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Validate Brackets: $seq",
                description = "Push opening brackets '{', '[', '(' onto stack. When closing bracket encountered, pop and match.",
                actionType = "INITIALIZE",
                variables = mapOf("input" to seq),
                vizType = DsaVizType.STACK,
                stackItems = emptyList(),
                stackTopIndex = -1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Pushed '{', '[', '('",
                description = "Pushed 3 open brackets onto stack in order.",
                actionType = "PUSH",
                variables = mapOf("stack" to "[{, [, (]"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("{", "[", "("),
                stackTopIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Encounter ')': Pop '(' and Match",
                description = "')' matches popped '('. Valid pair. Stack top is now '['.",
                actionType = "MATCH",
                variables = mapOf("matched" to "()", "remaining" to "[{, []"),
                vizType = DsaVizType.STACK,
                stackItems = listOf("{", "["),
                stackTopIndex = 1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Match Remaining ']' and '}'",
                description = "Popped and matched '[' with ']' and '{' with '}'. Stack is now empty.",
                actionType = "COMPLETE",
                variables = mapOf("isBalanced" to "true", "stackSize" to "0"),
                vizType = DsaVizType.STACK,
                stackItems = emptyList(),
                stackTopIndex = -1,
                isCompleted = true,
                summaryResult = "String '$seq' contains properly balanced parentheses."
            )
        )

        return steps
    }

    // 5. Applications of Stack (Monotonic Stack)
    fun generateStackApplications(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()
        val arr = listOf(4, 5, 2, 25)

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Next Greater Element using Monotonic Stack",
                description = "Find next greater element for each array item [4, 5, 2, 25] in linear O(N) time.",
                actionType = "INITIALIZE",
                variables = mapOf("array" to "$arr"),
                vizType = DsaVizType.STACK,
                arrayElements = arr,
                stackItems = emptyList(),
                stackTopIndex = -1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "4 -> Next Greater is 5",
                description = "When 5 arrives, it is greater than 4 on stack. Resolved NGE(4) = 5.",
                actionType = "RESOLVE",
                variables = mapOf("NGE(4)" to "5"),
                vizType = DsaVizType.STACK,
                arrayElements = arr,
                stackItems = listOf("5"),
                stackTopIndex = 0,
                highlightedIndices = setOf(0, 1)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Final Resolution: [5, 25, 25, -1]",
                description = "NGE results: 4->5, 5->25, 2->25, 25->-1.",
                actionType = "COMPLETE",
                variables = mapOf("result" to "[5, 25, 25, -1]"),
                vizType = DsaVizType.STACK,
                arrayElements = listOf(5, 25, 25, -1),
                sortedIndices = (0..3).toSet(),
                isCompleted = true,
                summaryResult = "Monotonic stack resolves Next Greater Element in O(N) time."
            )
        )

        return steps
    }
}
