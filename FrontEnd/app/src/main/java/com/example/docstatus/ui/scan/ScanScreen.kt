package com.example.docstatus.ui.scan

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.docstatus.ui.components.CameraPreview
import com.example.docstatus.ui.theme.AppColors

@Composable
fun ScanScreen(viewModel: ScanViewModel = viewModel(), onHistoryClick: () -> Unit, onLogoutClick: () -> Unit) {
    var hasPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }

    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    if (hasPermission) {
        val state by viewModel.uiState.collectAsState()
        Box(Modifier.fillMaxSize()) {
            CameraPreview(onQrDetected = { viewModel.onCodeScanned(it) })

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                Box(Modifier.align(Alignment.Center).size(250.dp).border(2.dp, AppColors.PrimaryBlue, RoundedCornerShape(12.dp)))
                Text("Наведите камеру", Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp), color = Color.White)

                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = "История", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Выход", tint = Color.White, modifier = Modifier.size(32.dp))
                }

                AnimatedVisibility(
                    visible = state.status != DocStatus.IDLE || state.isLoading,
                    enter = slideInVertically { -it } + fadeIn(),
                    exit = slideOutVertically { -it } + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (state.isLoading) AppColors.Surface else state.status.color)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            if (state.isLoading) {
                                Text("Проверка...", color = Color.White)
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(state.status.message, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.resetState() }) { Icon(Icons.Default.Close, null, tint = Color.White) }
                                }
                                Text("Метаданные: ${state.details}", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Нет прав доступа к камере", color = Color.White) }
    }
}