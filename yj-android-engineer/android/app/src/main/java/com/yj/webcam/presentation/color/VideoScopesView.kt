package com.yj.webcam.presentation.color

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.yj.webcam.domain.model.ScopeData

/**
 * Professional Video Scopes View rendering live Luma, Red, Green, Blue Histograms.
 */
@Composable
fun VideoScopesView(
    scopeData: ScopeData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            val maxLuma = (scopeData.histogramLuma.maxOrNull() ?: 1).coerceAtLeast(1)

            // Draw RGB & Luma Histograms
            drawHistogramPath(scopeData.histogramRed, Color.Red.copy(alpha = 0.6f), width, height, maxLuma)
            drawHistogramPath(scopeData.histogramGreen, Color.Green.copy(alpha = 0.6f), width, height, maxLuma)
            drawHistogramPath(scopeData.histogramBlue, Color.Blue.copy(alpha = 0.6f), width, height, maxLuma)
            drawHistogramPath(scopeData.histogramLuma, Color.White.copy(alpha = 0.9f), width, height, maxLuma)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHistogramPath(
    hist: IntArray,
    color: Color,
    width: Float,
    height: Float,
    maxVal: Int
) {
    if (hist.isEmpty()) return
    val path = Path()
    val dx = width / 255f

    path.moveTo(0f, height)
    for (i in 0..255) {
        val x = i * dx
        val normalized = hist[i].toFloat() / maxVal
        val y = height - (normalized * height)
        path.lineTo(x, y)
    }
    path.lineTo(width, height)
    path.close()

    drawPath(path = path, color = color, style = Stroke(width = 2f))
}
