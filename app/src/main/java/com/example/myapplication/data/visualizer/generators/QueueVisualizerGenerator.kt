package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType

object QueueVisualizerGenerator {

    // 1. Simple Queue (FIFO)
    fun generateSimpleQueue(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Simple Queue: First-In, First-Out (FIFO)",
                description = "Enqueue operations insert at Rear. Dequeue operations remove from Front.",
                actionType = "INITIALIZE",
                variables = mapOf("front" to "-1", "rear" to "-1", "size" to "0"),
                vizType = DsaVizType.QUEUE,
                queueItems = emptyList(),
                queueFrontIndex = -1,
                queueRearIndex = -1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Enqueue(10) & Enqueue(20)",
                description = "10 entered at front (index 0). 20 entered behind 10 (index 1).",
                actionType = "ENQUEUE",
                variables = mapOf("front" to "0 (10)", "rear" to "1 (20)"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("10", "20"),
                queueFrontIndex = 0,
                queueRearIndex = 1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Enqueue(30)",
                description = "Rear advances to index 2. Queue contains [10, 20, 30].",
                actionType = "ENQUEUE",
                variables = mapOf("front" to "0 (10)", "rear" to "2 (30)"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("10", "20", "30"),
                queueFrontIndex = 0,
                queueRearIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 5,
                title = "Dequeue() -> 10",
                description = "10 was the first element enqueued, so it is the first element dequeued. Front moves to 20.",
                actionType = "DEQUEUE",
                variables = mapOf("dequeued" to "10", "newFront" to "20"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("20", "30"),
                queueFrontIndex = 0,
                queueRearIndex = 1,
                isCompleted = true,
                summaryResult = "Queue preserved strict FIFO ordering."
            )
        )

        return steps
    }

    // 2. Circular Queue
    fun generateCircularQueue(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Circular Queue: Ring Buffer with Modulo Arithmetic",
                description = "Avoids wasted memory: rear = (rear + 1) % capacity. When rear reaches end, it wraps to index 0.",
                actionType = "INITIALIZE",
                variables = mapOf("capacity" to "5", "front" to "0", "rear" to "4"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("A", "B", "C", "D", "E"),
                queueFrontIndex = 0,
                queueRearIndex = 4,
                isCircularQueue = true
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Dequeue 2 Elements from Front",
                description = "Removed A and B. Front pointer moved to index 2 (val: 'C'). Slots 0 and 1 are now reusable.",
                actionType = "DEQUEUE",
                variables = mapOf("front" to "2 ('C')", "vacantSlots" to "[0, 1]"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("_", "_", "C", "D", "E"),
                queueFrontIndex = 2,
                queueRearIndex = 4,
                isCircularQueue = true
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Enqueue('F'): Rear Wraps Around to Index 0",
                description = "rear = (4 + 1) % 5 = 0. 'F' occupies previously vacated slot 0 without reallocating array!",
                actionType = "WRAP_ENQUEUE",
                variables = mapOf("rear" to "0 ('F')", "front" to "2 ('C')"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("F", "_", "C", "D", "E"),
                queueFrontIndex = 2,
                queueRearIndex = 0,
                isCircularQueue = true,
                isCompleted = true,
                summaryResult = "Circular queue efficiently reused freed memory slots."
            )
        )

        return steps
    }

    // 3. Priority Queue
    fun generatePriorityQueue(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Priority Queue: Elements Ordered by Urgency",
                description = "Items are dequeued based on priority value rather than insertion sequence.",
                actionType = "INITIALIZE",
                variables = mapOf("heapType" to "MAX_PRIORITY"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("Task C (P1)", "Task B (P3)", "Task A (P2)"),
                queueFrontIndex = 0,
                queueRearIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Heapify: Max Priority at Front",
                description = "Task B (Priority 3) moves to front of priority queue.",
                actionType = "HEAPIFY",
                variables = mapOf("highestPriority" to "Task B (P3)"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("Task B (P3)", "Task A (P2)", "Task C (P1)"),
                queueFrontIndex = 0,
                queueRearIndex = 2
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Dequeue Highest Priority: Task B",
                description = "Task B processed first. Task A (P2) bubbles to the top.",
                actionType = "DEQUEUE_MAX",
                variables = mapOf("processed" to "Task B", "next" to "Task A"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("Task A (P2)", "Task C (P1)"),
                queueFrontIndex = 0,
                queueRearIndex = 1,
                isCompleted = true,
                summaryResult = "Priority queue extraction runs in O(log N) time."
            )
        )

        return steps
    }

    // 4. Deque (Double-Ended Queue)
    fun generateDeque(customInput: String?): List<DsaVisualizerStep> {
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Deque: Insert & Delete at Both Ends",
                description = "Supports pushFront, pushRear, popFront, and popRear in O(1) time.",
                actionType = "INITIALIZE",
                variables = mapOf("operations" to "BIDIRECTIONAL"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("Mid1", "Mid2"),
                queueFrontIndex = 0,
                queueRearIndex = 1
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "pushFront('Head') & pushRear('Tail')",
                description = "Added 'Head' before Mid1 and 'Tail' after Mid2.",
                actionType = "DOUBLE_PUSH",
                variables = mapOf("front" to "Head", "rear" to "Tail"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("Head", "Mid1", "Mid2", "Tail"),
                queueFrontIndex = 0,
                queueRearIndex = 3
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "popRear() -> 'Tail'",
                description = "Removed 'Tail' from back without disturbing front elements.",
                actionType = "POP_REAR",
                variables = mapOf("removed" to "Tail"),
                vizType = DsaVizType.QUEUE,
                queueItems = listOf("Head", "Mid1", "Mid2"),
                queueFrontIndex = 0,
                queueRearIndex = 2,
                isCompleted = true,
                summaryResult = "Deque operations executed at both endpoints in O(1) time."
            )
        )

        return steps
    }
}
