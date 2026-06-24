package com.denser.june.presentation.screens.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denser.june.core.domain.model.ai.MoodEntry

private data class ChartConfig(
    val lineColor: Color,
    val dotColor: Color,
    val gridColor: Color,
    val textColor: Color
)

@Composable
fun MoodLineChart(
    entries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    val config = ChartConfig(
        lineColor = MaterialTheme.colorScheme.primary,
        dotColor = MaterialTheme.colorScheme.primary,
        gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        textColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val tertiary = MaterialTheme.colorScheme.tertiary

    Column(modifier = modifier) {
        // Legend row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(color = Color(0xFF4CAF50), label = "积极 (8-10)")
            LegendItem(color = Color(0xFFFFC107), label = "一般 (5-7)")
            LegendItem(color = Color(0xFFF44336), label = "消极 (1-4)")
        }

        val density = LocalDensity.current
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            val paddingLeft = 40f
            val paddingRight = 16f
            val paddingTop = 16f
            val paddingBottom = 40f

            val chartWidth = width - paddingLeft - paddingRight
            val chartHeight = height - paddingTop - paddingBottom

            if (entries.size < 2 || chartWidth <= 0 || chartHeight <= 0) return@Canvas

            val minScore = 1
            val maxScore = 10
            val scoreRange = (maxScore - minScore).toFloat()

            // Draw horizontal grid lines
            val gridSteps = 5
            for (i in 0..gridSteps) {
                val y = paddingTop + chartHeight - (chartHeight * i / gridSteps)
                val score = minScore + (scoreRange * i / gridSteps).toInt()

                drawLine(
                    color = config.gridColor,
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1f
                )

                // Score label
                drawContext.canvas.nativeCanvas.drawText(
                    "$score",
                    paddingLeft - 8f,
                    y + 4f,
                    textPaint
                )
            }

            // Calculate points
            val points = entries.mapIndexed { index, entry ->
                val x = paddingLeft + (chartWidth * index / (entries.size - 1).coerceAtLeast(1))
                val normalizedScore = (entry.score.coerceIn(minScore, maxScore) - minScore) / scoreRange
                val y = paddingTop + chartHeight - (chartHeight * normalizedScore)
                Offset(x, y) to entry
            }

            // Draw the filled area under the line (gradient effect)
            val fillPath = Path().apply {
                val first = points.first().first
                val last = points.last().first
                moveTo(first.x, paddingTop + chartHeight)
                points.forEach { (point, _) ->
                    lineTo(point.x, point.y)
                }
                lineTo(last.x, paddingTop + chartHeight)
                close()
            }
            drawPath(
                path = fillPath,
                color = config.lineColor.copy(alpha = 0.1f)
            )

            // Draw the line
            val linePath = Path().apply {
                points.forEachIndexed { index, (point, _) ->
                    if (index == 0) moveTo(point.x, point.y)
                    else lineTo(point.x, point.y)
                }
            }
            drawPath(
                path = linePath,
                color = config.lineColor,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw dots and date labels
            val maxLabels = (width / 80f).toInt().coerceAtLeast(1)
            val labelInterval = (entries.size / maxLabels).coerceAtLeast(1)

            points.forEachIndexed { index, (point, entry) ->
                val dotColor = when {
                    entry.score >= 8 -> Color(0xFF4CAF50)
                    entry.score >= 5 -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }

                // Draw outer white circle
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = point
                )
                // Draw inner colored circle
                drawCircle(
                    color = dotColor,
                    radius = 4f,
                    center = point
                )

                // Draw date labels (spaced out)
                if (index % labelInterval == 0 || index == points.lastIndex) {
                    val label = entry.date.takeLast(5) // "01-15" format
                    val textWidth = textPaint.measureText(label)
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        point.x - textWidth / 2,
                        height - 4f,
                        textPaint
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
