package com.x.heartbeep.ui.monitoring

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x.heartbeep.MainUiState
import com.x.heartbeep.ui.NeonGreen
import com.x.heartbeep.ui.NeonRed
import kotlinx.coroutines.launch

@Composable
internal fun MonitoringTab(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    uiState: MainUiState,
    hasMonitoringPermissions: Boolean,
    hasLocationPermission: Boolean,
    gpsEnabled: Boolean,
    onGrantPermissions: () -> Unit,
    onGrantLocationPermission: () -> Unit,
    onEnableBluetooth: () -> Unit,
    onThresholdChange: (String) -> Unit,
    onLowerBoundChange: (String) -> Unit,
    onScan: () -> Unit,
    onSelectDevice: (String) -> Unit,
    onStartMonitoring: () -> Unit,
    onStopMonitoring: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showGpsDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showGpsDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDialog = false },
            title = { Text("GPS is off") },
            text = { Text("Enable GPS for distance tracking, or continue with heart-rate only.") },
            confirmButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text("Enable GPS")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    onStartMonitoring()
                }) {
                    Text("HR only")
                }
            },
        )
    }

    Column(
        modifier = modifier
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
    ) {
        // ── Top controls ────────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DeviceStatusCard(
                uiState = uiState,
                hasMonitoringPermissions = hasMonitoringPermissions,
                onGrantPermissions = onGrantPermissions,
                onEnableBluetooth = onEnableBluetooth,
                onScan = onScan,
                onSelectDevice = onSelectDevice,
            )

            BpmLimitsSection(
                uiState = uiState,
                onThresholdChange = onThresholdChange,
                onLowerBoundChange = onLowerBoundChange,
            )

            DistanceStatusSection(
                isMonitoring = uiState.monitoringState.isMonitoring,
                isDistanceTrackingEnabled = uiState.monitoringState.isDistanceTrackingEnabled,
                hasLocationPermission = hasLocationPermission,
                gpsEnabled = gpsEnabled,
                onGrantLocationPermission = onGrantLocationPermission,
            )
        }

        // ── Bottom section: HR graph + stats + button ───────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val hrColorTarget = when {
                !uiState.monitoringState.isMonitoring || uiState.monitoringState.currentHr == null ->
                    MaterialTheme.colorScheme.onSurface
                isHrOutOfBounds(
                    uiState.monitoringState.currentHr,
                    uiState.persistedThreshold,
                    uiState.persistedLowerBound,
                ) -> NeonRed
                else -> NeonGreen
            }
            val hrColor by animateColorAsState(hrColorTarget, label = "hrColor")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                HrGraph(
                    hrHistory = uiState.monitoringState.hrHistory,
                    isMonitoring = uiState.monitoringState.isMonitoring,
                    upperBound = uiState.persistedThreshold,
                    lowerBound = uiState.persistedLowerBound,
                    modifier = Modifier.matchParentSize(),
                )
                Text(
                    text = uiState.monitoringState.currentHr?.let { "$it" } ?: "--",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = hrColor,
                )
            }

            if (uiState.monitoringState.currentHr == null) {
                Text(
                    text = "Waiting for live data",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SessionStatsRow(monitoringState = uiState.monitoringState)

            Spacer(modifier = Modifier.height(12.dp))

            StartStopButton(
                isMonitoring = uiState.monitoringState.isMonitoring,
                enabled = hasMonitoringPermissions && uiState.bluetoothEnabled,
                onStart = {
                    if (hasLocationPermission && !gpsEnabled) {
                        showGpsDialog = true
                    } else {
                        onStartMonitoring()
                    }
                },
                onStop = onStopMonitoring,
                onTapHint = {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("Hold to stop")
                    }
                },
            )
        }
    }
}
