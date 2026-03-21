package com.x.heartbeep.ui.monitoring

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.x.heartbeep.MainUiState
import com.x.heartbeep.monitoring.ConnectionState
import com.x.heartbeep.ui.CardBackground
import com.x.heartbeep.ui.NeonCyan
import com.x.heartbeep.ui.NeonGreen
import com.x.heartbeep.ui.NeonOrange
import com.x.heartbeep.ui.NeonRed

@Composable
internal fun DeviceStatusCard(
    uiState: MainUiState,
    hasMonitoringPermissions: Boolean,
    onGrantPermissions: () -> Unit,
    onEnableBluetooth: () -> Unit,
    onScan: () -> Unit,
    onSelectDevice: (String) -> Unit,
) {
    val monitoringState = uiState.monitoringState
    val isConnected = monitoringState.connectionState == ConnectionState.Connected ||
        monitoringState.connectionState == ConnectionState.Monitoring
    val isConnecting = monitoringState.connectionState == ConnectionState.Connecting
    val hasError = monitoringState.connectionState == ConnectionState.Disconnected ||
        monitoringState.connectionState == ConnectionState.Error
    val enabled = hasMonitoringPermissions &&
        uiState.bluetoothEnabled &&
        !uiState.monitoringState.isMonitoring

    val borderColorTarget = when {
        !hasMonitoringPermissions || !uiState.bluetoothEnabled || hasError -> NeonRed.copy(alpha = 0.6f)
        isConnected -> NeonGreen.copy(alpha = 0.7f)
        isConnecting -> NeonOrange.copy(alpha = 0.6f)
        else -> NeonCyan.copy(alpha = 0.5f)
    }
    val borderColor by animateColorAsState(borderColorTarget, label = "deviceBorder")
    val cardShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.5.dp, color = borderColor, shape = cardShape)
            .clip(cardShape)
            .background(CardBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isConnected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Connected",
                    tint = NeonGreen,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                when {
                    !hasMonitoringPermissions -> {
                        Text(
                            "Permissions required",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonRed,
                        )
                    }
                    !uiState.bluetoothEnabled -> {
                        Text(
                            "Bluetooth is off",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonRed,
                        )
                    }
                    uiState.availableDevices.isEmpty() && !isConnected -> {
                        Text(
                            "No device selected",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    else -> {
                        val device = uiState.availableDevices.firstOrNull {
                            it.address == uiState.selectedDeviceAddress
                        } ?: uiState.availableDevices.firstOrNull()
                        Text(
                            text = device?.name ?: monitoringState.deviceName ?: "Heart rate monitor",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        val batteryText = when {
                            monitoringState.batteryLevelPercent != null ->
                                "Battery ${monitoringState.batteryLevelPercent}%"
                            isConnecting -> "Connecting..."
                            isConnected -> "Connected"
                            else -> null
                        }
                        if (batteryText != null) {
                            Text(
                                text = batteryText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            when {
                !hasMonitoringPermissions -> {
                    TextButton(onClick = onGrantPermissions) {
                        Text("Grant", color = NeonCyan)
                    }
                }
                !uiState.bluetoothEnabled -> {
                    TextButton(onClick = onEnableBluetooth) {
                        Text("Enable", color = NeonCyan)
                    }
                }
                else -> {
                    TextButton(onClick = onScan, enabled = enabled) {
                        Text(
                            text = if (uiState.isScanning) "Scanning..." else "Scan",
                            color = if (enabled) NeonCyan else NeonCyan.copy(alpha = 0.4f),
                        )
                    }
                }
            }
        }

        // Multi-device selection
        if (uiState.availableDevices.size > 1 && hasMonitoringPermissions && uiState.bluetoothEnabled) {
            uiState.availableDevices.forEach { device ->
                val isSelected = device.address == uiState.selectedDeviceAddress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = enabled) { onSelectDevice(device.address) }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = if (enabled) { { onSelectDevice(device.address) } } else null,
                    )
                    Text(
                        device.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
