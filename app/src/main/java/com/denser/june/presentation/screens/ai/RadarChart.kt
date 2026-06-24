package com.denser.june.presentation.screens.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denser.june.core.domain.model.ai.JungianDimension
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Radar chart for displaying Jungian cognitive function scores (荣格八维).
 */
@Composable
fun RadarChart(
    dimensions: List<JungianDimension>,
    modifier: Modifier = Modifier
) {
    if (dimensions.isEmpty()) return

    val primaryColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val fillColor = primaryColor.copy(alpha = 0.15f)
    val strokeColor = primaryColor
    val textColor = MaterialTheme.colorScheme.onSurface

    val density = LocalDensity.current
    val labelPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.GRAY
        textSize = with(density) { 10.sp.toPx() }
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val scorePaint = android.graphics.Paint().apply {
        color = android.graphics.Color.DKGRAY
        textSize = with(density) { 9.sp.toPx() }
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
        isFakeBoldText = true
    }

    val labels = dimensions.map { it.key }
    val scores = dimensions.map { it.score.coerceIn(1, 10).toFloat() }
    val maxScore = 10f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = minOf(centerX, centerY) - 50f
        if (radius <= 0) return@Canvas

        val n = labels.size
        val angleStep = 2 * PI / n

        // Draw concentric pentagon/octagon grids
        val gridLevels = 5
        for (level in 1..gridLevels) {
            val r = radius * level / gridLevels
            val gridPath = Path()
            for (i in 0 until n) {
                val angle = -PI / 2 + i * angleStep
                val x = centerX + r * cos(angle).toFloat()
                val y = centerY + r * sin(angle).toFloat()
                if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
            }
            gridPath.close()
            drawPath(
                path = gridPath,
                color = gridColor,
                style = Stroke(width = 1f)
            )
        }

        // Draw axis lines from center to each vertex
        for (i in 0 until n) {
            val angle = -PI / 2 + i * angleStep
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            drawLine(
                color = gridColor,
                start = Offset(centerX, centerY),
                end = Offset(x, y),
                strokeWidth = 1f
            )
        }

        // Draw the data polygon (filled)
        val dataPath = Path()
        for (i in 0 until n) {
            val score = scores[i] / maxScore
            val r = radius * score
            val angle = -PI / 2 + i * angleStep
            val x = centerX + r * cos(angle).toFloat()
            val y = centerY + r * sin(angle).toFloat()
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()
        drawPath(path = dataPath, color = fillColor, style = Fill)
        drawPath(
            path = dataPath,
            color = strokeColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw score dots
        for (i in 0 until n) {
            val score = scores[i] / maxScore
            val r = radius * score
            val angle = -PI / 2 + i * angleStep
            val x = centerX + r * cos(angle).toFloat()
            val y = centerY + r * sin(angle).toFloat()

            drawCircle(color = Color.White, radius = 6f, center = Offset(x, y))
            drawCircle(color = strokeColor, radius = 4f, center = Offset(x, y))
        }

        // Draw labels at vertices
        for (i in 0 until n) {
            val angle = -PI / 2 + i * angleStep
            val labelR = radius + 22f
            val lx = centerX + labelR * cos(angle).toFloat()
            val ly = centerY + labelR * sin(angle).toFloat()
            drawContext.canvas.nativeCanvas.drawText(labels[i], lx, ly + 4f, labelPaint)

            // Draw score next to label
            val scoreR = radius + 38f
            val sx = centerX + scoreR * cos(angle).toFloat()
            val sy = centerY + scoreR * sin(angle).toFloat()
            drawContext.canvas.nativeCanvas.drawText(
                scores[i].toInt().toString(),
                sx,
                sy + 4f,
                scorePaint
            )
        }
    }
}

/**
 * Detailed personality dimensions list below the radar chart.
 */
@Composable
fun PersonalityDimensionList(
    dimensions: List<JungianDimension>,
    modifier: Modifier = Modifier
) {
    if (dimensions.isEmpty()) return

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        dimensions.forEach { dim ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label
                Text(
                    text = "${dim.key} ${dim.name}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(100.dp)
                )
                // Score bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                ) {
                    // Background bar
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = surfaceVariant,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                        )
                    }
                    // Fill bar
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val fillWidth = size.width * dim.score / 10f
                        drawRoundRect(
                            color = primary,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                            size = androidx.compose.ui.geometry.Size(fillWidth, size.height)
                        )
                    }
                }
                // Score number
                Text(
                    text = "${dim.score}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(24.dp)
                        .padding(start = 4.dp),
                    color = primary
                )
            }
            if (dim.description.isNotBlank()) {
                Text(
                    text = dim.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariant,
                    modifier = Modifier.padding(start = 100.dp)
                )
            }
        }
    }
}
