package com.yj.webcam.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yj.webcam.core.theme.YJBackgroundDark
import com.yj.webcam.core.theme.YJPrimary
import com.yj.webcam.core.theme.YJSurfaceDark
import com.yj.webcam.presentation.components.StatusBadge

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToPairing: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YJBackgroundDark)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "YJ Webcam",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    StatusBadge(status = state.connectionInfo.status)
                }

                Row {
                    IconButton(onClick = onNavigateToPairing) {
                        Icon(Icons.Default.QrCode, contentDescription = "Pairing", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(YJBackgroundDark)
        ) {
            // Camera Preview Viewport Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(YJSurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.isStreamingActive) "STREAMING 1080p 60FPS" else "CAMERA PREVIEW READY",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }

            // Floating Controls Bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AI Toggles Row
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(YJSurfaceDark.copy(alpha = 0.9f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { viewModel.toggleFlash() }) {
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = "Flash",
                            tint = if (state.isFlashOn) YJPrimary else Color.Gray
                        )
                    }
                    IconButton(onClick = { viewModel.toggleAiBlur() }) {
                        Icon(
                            Icons.Default.BlurOn,
                            contentDescription = "Background Blur",
                            tint = if (state.isAiBlurActive) YJPrimary else Color.Gray
                        )
                    }
                    IconButton(onClick = { viewModel.toggleAiAutoFraming() }) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Auto Framing",
                            tint = if (state.isAiAutoFramingActive) YJPrimary else Color.Gray
                        )
                    }
                    IconButton(onClick = { viewModel.switchCamera() }) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streaming Toggle FAB
                FloatingActionButton(
                    onClick = { viewModel.toggleStreaming() },
                    containerColor = YJPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = if (state.isStreamingActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Toggle Stream",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
