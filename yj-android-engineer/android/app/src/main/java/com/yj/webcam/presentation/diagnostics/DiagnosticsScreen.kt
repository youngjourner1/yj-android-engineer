package com.yj.webcam.presentation.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yj.webcam.core.theme.YJBackgroundDark
import com.yj.webcam.core.theme.YJSurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    viewModel: DiagnosticsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stream Diagnostics", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = YJBackgroundDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(YJBackgroundDark)
                .padding(16.dp)
        ) {
            DiagnosticMetricCard("Current FPS", "${stats.currentFps} / ${stats.targetFps}")
            Spacer(modifier = Modifier.height(12.dp))
            DiagnosticMetricCard("Network Latency", "${stats.latencyMs} ms")
            Spacer(modifier = Modifier.height(12.dp))
            DiagnosticMetricCard("Current Bitrate", "${stats.bitrateKbps} Kbps")
            Spacer(modifier = Modifier.height(12.dp))
            DiagnosticMetricCard("Dropped Frames", "${stats.droppedFrames} frames")
        }
    }
}

@Composable
fun DiagnosticMetricCard(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(YJSurfaceDark)
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = Color.White)
        }
    }
}
