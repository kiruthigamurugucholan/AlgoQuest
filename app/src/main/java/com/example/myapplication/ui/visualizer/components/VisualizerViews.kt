package com.example.myapplication.ui.visualizer.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.ui.theme.AlgoTheme

// --- Color Palette Helpers ---
private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val TextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val Green: Color @Composable get() = AlgoTheme.colors.green
private val Gold: Color @Composable get() = AlgoTheme.colors.gold
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val CodeBg: Color @Composable get() = AlgoTheme.colors.codeBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border
private val EliminatedColor: Color @Composable get() = AlgoTheme.colors.eliminated

private val Mono: FontFamily @Composable get() = AlgoTheme.editorFontFamily

/**
 * 1. Array / Searching / Sorting / String View
 */
@Composable
fun ArrayVisualizerView(step: DsaVisualizerStep) {
    val items = if (step.vizType == DsaVizType.STRING && step.stringCharacters.isNotEmpty()) {
        step.stringCharacters.map { it.toString() }
    } else {
        step.arrayElements.map { it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEachIndexed { idx, valStr ->
                val isHighlighted = idx in step.highlightedIndices
                val isSorted = idx in step.sortedIndices
                val isEliminated = idx in step.eliminatedIndices
                val pointerLabel = step.pointerLabels[idx]

                val itemBg by animateColorAsState(
                    targetValue = when {
                        step.isFound && isHighlighted -> Green
                        isSorted -> Green.copy(alpha = 0.35f)
                        isHighlighted && step.actionType == "SWAP" -> Gold
                        isHighlighted -> Purple
                        isEliminated -> EliminatedColor
                        else -> CardBg
                    },
                    animationSpec = tween(300),
                    label = "arrayItemBg"
                )

                val borderCol = when {
                    step.isFound && isHighlighted -> Green
                    isHighlighted -> Cyan
                    isSorted -> Green
                    else -> BorderColor
                }

                Column(
                    modifier = Modifier.width(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Pointer label
                    Box(modifier = Modifier.height(18.dp), contentAlignment = Alignment.Center) {
                        if (!pointerLabel.isNullOrEmpty()) {
                            Text(
                                text = pointerLabel,
                                style = TextStyle(
                                    fontFamily = Mono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (step.isFound) Green else Cyan
                                )
                            )
                        }
                    }

                    // Value cell
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(itemBg)
                            .border(1.5.dp, borderCol, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = valStr,
                            style = TextStyle(
                                fontFamily = Mono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isEliminated) TextMuted else TextPrimary
                            )
                        )
                    }

                    // Index subscript
                    Text(
                        text = "[$idx]",
                        style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = TextMuted)
                    )
                }
            }
        }
    }
}

/**
 * 2. Linked List View
 */
@Composable
fun LinkedListVisualizerView(step: DsaVisualizerStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        step.listNodes.forEachIndexed { idx, node ->
            val pointer = step.pointerLabels[idx] ?: node.label

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pointer tag
                Box(modifier = Modifier.height(18.dp), contentAlignment = Alignment.Center) {
                    if (!pointer.isNullOrEmpty()) {
                        Text(
                            text = pointer,
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Cyan)
                        )
                    }
                }

                // Node container: [Data | Next]
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (node.isHighlighted) Purple.copy(alpha = 0.5f) else CardBg)
                        .border(1.5.dp, if (node.isHighlighted) Cyan else BorderColor, RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = node.value,
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(BorderColor)
                    )
                    Box(
                        modifier = Modifier
                            .background(CodeBg)
                            .padding(horizontal = 6.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "•",
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Cyan)
                        )
                    }
                }
            }

            // Arrow connector
            Text(
                text = if (step.isDoublyLinkedList) " ⇄ " else " ➔ ",
                style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Cyan)
            )
        }

        // Null terminator or circular loopback
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CodeBg)
                .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (step.isCircularLinkedList) "↺ [HEAD]" else "null",
                style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
            )
        }
    }
}

/**
 * 3. Stack View (Vertical beaker)
 */
@Composable
fun StackVisualizerView(step: DsaVisualizerStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(160.dp)
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(CodeBg)
                .border(2.dp, PurpleSoft.copy(alpha = 0.5f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (step.stackItems.isEmpty()) {
                    Text(
                        text = "(Stack Empty)",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextMuted)
                    )
                } else {
                    step.stackItems.asReversed().forEachIndexed { revIdx, item ->
                        val actualIdx = step.stackItems.size - 1 - revIdx
                        val isTop = actualIdx == step.stackTopIndex

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTop) Purple else CardBg)
                                .border(1.dp, if (isTop) Cyan else BorderColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            )
                            if (isTop) {
                                Text(
                                    text = "◀ TOP",
                                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Cyan)
                                )
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = "Stack: LIFO (Push / Pop at Top)",
            style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
        )
    }
}

/**
 * 4. Queue View (Horizontal pipeline)
 */
@Composable
fun QueueVisualizerView(step: DsaVisualizerStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "◀ DEQUEUE (FRONT)", style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = Cyan))
            Text(text = "ENQUEUE (REAR) ◀", style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = Gold))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CodeBg)
                .border(1.5.dp, BorderColor, RoundedCornerShape(12.dp))
                .horizontalScroll(rememberScrollState())
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (step.queueItems.isEmpty()) {
                Text(
                    text = "(Queue Empty)",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextMuted)
                )
            } else {
                step.queueItems.forEachIndexed { idx, item ->
                    val isFront = idx == step.queueFrontIndex
                    val isRear = idx == step.queueRearIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val badge = when {
                            isFront && isRear -> "FRONT/REAR"
                            isFront -> "FRONT"
                            isRear -> "REAR"
                            else -> null
                        }
                        Box(modifier = Modifier.height(16.dp), contentAlignment = Alignment.Center) {
                            badge?.let {
                                Text(text = it, style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 8.sp, color = if (isFront) Cyan else Gold))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFront) Purple else CardBg)
                                .border(1.dp, if (isFront) Cyan else if (isRear) Gold else BorderColor, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5. Tree View (Canvas rendering of binary tree nodes & branches)
 */
@Composable
fun TreeVisualizerView(step: DsaVisualizerStep) {
    val cyan = Cyan
    val purple = Purple
    val green = Green
    val cardBg = CardBg
    val borderCol = BorderColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CodeBg)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val nodeMap = step.treeNodes.associateBy { it.id }

                // Draw edge lines first
                step.treeNodes.forEach { node ->
                    val startX = node.xFraction * size.width
                    val startY = node.yFraction * size.height

                    node.leftChildId?.let { leftId ->
                        nodeMap[leftId]?.let { leftNode ->
                            val endX = leftNode.xFraction * size.width
                            val endY = leftNode.yFraction * size.height
                            drawLine(
                                color = Color.White.copy(alpha = 0.25f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    node.rightChildId?.let { rightId ->
                        nodeMap[rightId]?.let { rightNode ->
                            val endX = rightNode.xFraction * size.width
                            val endY = rightNode.yFraction * size.height
                            drawLine(
                                color = Color.White.copy(alpha = 0.25f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // Draw node circles
                step.treeNodes.forEach { node ->
                    val cx = node.xFraction * size.width
                    val cy = node.yFraction * size.height
                    val radius = 16.dp.toPx()
                    val isActive = node.id == step.activeTreeNodeId || node.isHighlighted

                    drawCircle(
                        color = if (isActive) cyan else if (node.isVisited) green else cardBg,
                        radius = radius,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = if (isActive) Color.White else if (node.isVisited) green else borderCol,
                        radius = radius,
                        center = Offset(cx, cy),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Overlay node text labels
            step.treeNodes.forEach { node ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = (node.xFraction * 280).dp.coerceAtLeast(0.dp),
                            top = (node.yFraction * 120).dp.coerceAtLeast(0.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = node.value,
                        style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary)
                    )
                }
            }
        }

        // Traversal order sequence row
        if (step.treeTraversalOrder.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Traversal: ", style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = Cyan))
                step.treeTraversalOrder.forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CardBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = item, style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Green))
                    }
                }
            }
        }
    }
}

/**
 * 6. Graph View (Canvas vertices & weighted edges)
 */
@Composable
fun GraphVisualizerView(step: DsaVisualizerStep) {
    val cyan = Cyan
    val green = Green
    val gold = Gold
    val cardBg = CardBg
    val borderCol = BorderColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CodeBg)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val vMap = step.graphVertices.associateBy { it.id }

                // Draw edges
                step.graphEdges.forEach { edge ->
                    val fromV = vMap[edge.fromId]
                    val toV = vMap[edge.toId]
                    if (fromV != null && toV != null) {
                        val start = Offset(fromV.xFraction * size.width, fromV.yFraction * size.height)
                        val end = Offset(toV.xFraction * size.width, toV.yFraction * size.height)
                        val isEdgeActive = edge.isHighlighted || edge.isMst

                        drawLine(
                            color = if (edge.isMst) gold else if (edge.isHighlighted) cyan else Color.White.copy(alpha = 0.2f),
                            start = start,
                            end = end,
                            strokeWidth = if (isEdgeActive) 3.dp.toPx() else 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Draw vertices
                step.graphVertices.forEach { v ->
                    val center = Offset(v.xFraction * size.width, v.yFraction * size.height)
                    val radius = 16.dp.toPx()
                    val isActive = v.id == step.activeVertexId || v.isHighlighted
                    val isVisited = v.id in step.visitedVertexIds || v.isVisited

                    drawCircle(
                        color = if (isActive) cyan else if (isVisited) green else cardBg,
                        radius = radius,
                        center = center
                    )
                    drawCircle(
                        color = if (isActive) Color.White else if (isVisited) green else borderCol,
                        radius = radius,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Vertex labels
            step.graphVertices.forEach { v ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = (v.xFraction * 270).dp.coerceAtLeast(0.dp),
                            top = (v.yFraction * 130).dp.coerceAtLeast(0.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = v.label,
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary)
                        )
                        v.distance?.let { d ->
                            Text(text = "d:$d", style = TextStyle(fontFamily = Mono, fontSize = 8.sp, color = Cyan))
                        }
                    }
                }
            }
        }

        // Auxiliary Queue or Stack row
        if (step.auxiliaryQueueOrStack.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Explore Queue/Stack: ", style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = Cyan))
                step.auxiliaryQueueOrStack.forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CardBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = item, style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = TextPrimary))
                    }
                }
            }
        }
    }
}

/**
 * 7. Recursion Call Stack View
 */
@Composable
fun RecursionVisualizerView(step: DsaVisualizerStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBg)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Call Stack Depth: ${step.callStack.size}", style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = Cyan))
            if (step.baseCaseReached) {
                Text(text = "BASE CASE REACHED ✓", style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Green))
            }
        }

        if (step.callStack.isEmpty()) {
            Text(
                text = "(Call Stack Empty – Unwound)",
                modifier = Modifier.padding(vertical = 12.dp),
                style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextMuted)
            )
        } else {
            step.callStack.asReversed().forEach { frame ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = (frame.depth * 8).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (frame.isReturning) Green.copy(alpha = 0.25f) else CardBg)
                        .border(1.dp, if (frame.isReturning) Green else BorderColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${frame.functionName}(${frame.arguments.entries.joinToString { "${it.key}=${it.value}" }})",
                        style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TextPrimary)
                    )
                    frame.returnValue?.let { ret ->
                        Text(
                            text = "➔ $ret",
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Green)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 8. Dynamic Programming Table View
 */
@Composable
fun DpTableVisualizerView(step: DsaVisualizerStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBg)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        step.dpFormula?.let { formula ->
            Text(text = formula, style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Cyan))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header Row
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(60.dp))
                step.dpHeadersX.forEach { h ->
                    Box(modifier = Modifier.width(42.dp), contentAlignment = Alignment.Center) {
                        Text(text = h, style = TextStyle(fontFamily = Mono, fontSize = 8.sp, color = TextMuted))
                    }
                }
            }

            // Grid Rows
            step.dpGrid.forEachIndexed { rIdx, row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rowLabel = step.dpHeadersY.getOrElse(rIdx) { "R$rIdx" }
                    Box(modifier = Modifier.width(60.dp)) {
                        Text(text = rowLabel, maxLines = 1, style = TextStyle(fontFamily = Mono, fontSize = 8.sp, color = TextMuted))
                    }

                    row.forEachIndexed { cIdx, cellVal ->
                        val isActive = step.activeDpCell == Pair(rIdx, cIdx)
                        val isDep = Pair(rIdx, cIdx) in step.dependentDpCells

                        Box(
                            modifier = Modifier
                                .size(42.dp, 30.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isActive) Purple else if (isDep) Cyan.copy(alpha = 0.2f) else CardBg)
                                .border(1.dp, if (isActive) Cyan else if (isDep) Gold else BorderColor, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cellVal,
                                style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = TextPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 9. Greedy Decision View
 */
@Composable
fun GreedyVisualizerView(step: DsaVisualizerStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBg)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Capacity: ${step.greedyCapacityUsed ?: "0"}", style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = Cyan))
            Text(text = "Total Value: ${step.greedyTotalValue ?: "$0"}", style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Gold))
        }

        step.greedyItems.forEach { item ->
            val isSelected = item.status == "SELECTED"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Purple.copy(alpha = 0.35f) else CardBg)
                    .border(1.dp, if (isSelected) Green else BorderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = item.label, style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary))
                    Text(text = "Weight: ${item.weight} | Value: ${item.value}", style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = TextMuted))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) Green.copy(alpha = 0.2f) else CardBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.status,
                        style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = if (isSelected) Green else TextSecondary)
                    )
                }
            }
        }
    }
}

/**
 * 10. Flowchart & Complexity Scale View (Programming Basics)
 */
@Composable
fun FlowchartVisualizerView(step: DsaVisualizerStep) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBg)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        step.flowchartSteps.forEachIndexed { idx, stp ->
            val isActive = idx == step.activeFlowchartStep

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isActive) Purple else CardBg)
                    .border(1.dp, if (isActive) Cyan else BorderColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${idx + 1}. $stp",
                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TextPrimary)
                )
                if (isActive) {
                    Text(text = "ACTIVE", style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Cyan))
                }
            }
        }
    }
}
