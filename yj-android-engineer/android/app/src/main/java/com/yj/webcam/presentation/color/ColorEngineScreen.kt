package com.yj.webcam.presentation.color

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yj.webcam.domain.model.LutModel
import com.yj.webcam.domain.model.ProcessingMode
import com.yj.webcam.domain.model.TierState
import com.yj.webcam.presentation.paywall.PaywallDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorEngineScreen(
    viewModel: ColorEngineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val luts by viewModel.luts.collectAsState()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                val filename = it.lastPathSegment ?: "imported.cube"
                viewModel.importLutFile(stream, filename)
            }
        }
    }

    if (uiState.showPaywall) {
        PaywallDialog(
            onDismiss = { viewModel.dismissPaywall() },
            onUpgrade = { tier ->
                viewModel.entitlementManager.updateTier(tier)
                viewModel.dismissPaywall()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JY Color Engine", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.toggleBeforeAfterSplit() }) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Before/After Split",
                            tint = if (uiState.isBeforeAfterSplit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.toggleScopes() }) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Toggle Video Scopes",
                            tint = if (uiState.showScopes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.resetAdjustments() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Controls")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Simulated Live Camera Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color.DarkGray)
            ) {
                Text(
                    text = "Live Camera Stream (GPU Color Pipeline)",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (uiState.showScopes) {
                    VideoScopesView(
                        scopeData = uiState.scopes,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    )
                }
            }

            // Mode Selector Bar
            ProcessingModeBar(
                currentMode = uiState.processingMode,
                onModeSelected = { viewModel.setProcessingMode(it) }
            )

            // LUT & Adjustments Control Panel
            var selectedTabIndex by remember { mutableStateOf(0) }
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("LUTs", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("Basic", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                    Text("Effects", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> LutTabContent(
                        luts = luts,
                        activeLut = uiState.activeLut,
                        intensity = uiState.adjustment.lutIntensity,
                        onLutSelected = { viewModel.selectLut(it) },
                        onIntensityChanged = { viewModel.updateLutIntensity(it) },
                        onImportClick = { filePickerLauncher.launch("*/*") }
                    )
                    1 -> BasicControlsContent(
                        adjustment = uiState.adjustment,
                        onExposureChange = { viewModel.updateExposure(it) },
                        onContrastChange = { viewModel.updateContrast(it) },
                        onSaturationChange = { viewModel.updateSaturation(it) },
                        onTemperatureChange = { viewModel.updateTemperature(it) },
                        onTintChange = { viewModel.updateTint(it) }
                    )
                    2 -> EffectsTabContent()
                }
            }
        }
    }
}

@Composable
private fun ProcessingModeBar(
    currentMode: ProcessingMode,
    onModeSelected: (ProcessingMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Processing Location:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Row {
            ModeChip(
                label = "Mobile GPU",
                isSelected = currentMode == ProcessingMode.MOBILE_MODE,
                onClick = { onModeSelected(ProcessingMode.MOBILE_MODE) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ModeChip(
                label = "Desktop PC",
                isSelected = currentMode == ProcessingMode.DESKTOP_MODE,
                onClick = { onModeSelected(ProcessingMode.DESKTOP_MODE) }
            )
        }
    }
}

@Composable
private fun ModeChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LutTabContent(
    luts: List<LutModel>,
    activeLut: LutModel?,
    intensity: Float,
    onLutSelected: (LutModel?) -> Unit,
    onIntensityChanged: (Float) -> Unit,
    onImportClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("LUT Library", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = onImportClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Import .CUBE LUT")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                LutCard(
                    title = "None (Original)",
                    isSelected = activeLut == null,
                    onClick = { onLutSelected(null) }
                )
            }
            items(luts) { lut ->
                LutCard(
                    title = lut.title,
                    isSelected = activeLut?.id == lut.id,
                    onClick = { onLutSelected(lut) }
                )
            }
        }

        if (activeLut != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("LUT Intensity: ${(intensity * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
            Slider(
                value = intensity,
                onValueChange = onIntensityChanged,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun LutCard(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(70.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun BasicControlsContent(
    adjustment: com.yj.webcam.domain.model.ColorAdjustment,
    onExposureChange: (Float) -> Unit,
    onContrastChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    onTemperatureChange: (Float) -> Unit,
    onTintChange: (Float) -> Unit
) {
    Column {
        ControlSlider("Exposure EV", adjustment.exposure, -2f..2f, onExposureChange)
        ControlSlider("Contrast", adjustment.contrast, 0.5f..1.8f, onContrastChange)
        ControlSlider("Saturation", adjustment.saturation, 0f..2f, onSaturationChange)
        ControlSlider("Temperature", adjustment.temperature, -1f..1f, onTemperatureChange)
        ControlSlider("Tint", adjustment.tint, -1f..1f, onTintChange)
    }
}

@Composable
private fun ControlSlider(label: String, value: Float, range: ClosedRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(String.format("%.2f", value), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}

@Composable
private fun EffectsTabContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Live Camera Effects (Pro)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Skin Smoothing, Film Grain, Vignette, and Soft Focus adjustments are accessible with Pro entitlement.", fontSize = 14.sp)
    }
}
