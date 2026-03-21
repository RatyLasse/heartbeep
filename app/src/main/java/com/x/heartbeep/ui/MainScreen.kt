package com.x.heartbeep.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x.heartbeep.MainUiState
import com.x.heartbeep.ui.history.HistoryTab
import com.x.heartbeep.ui.monitoring.MonitoringTab

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
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
    onDeleteSession: (Long) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            PageDots(currentPage = pagerState.currentPage, pageCount = 2)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                0 -> MonitoringTab(
                    modifier = Modifier.fillMaxSize(),
                    uiState = uiState,
                    hasMonitoringPermissions = hasMonitoringPermissions,
                    hasLocationPermission = hasLocationPermission,
                    gpsEnabled = gpsEnabled,
                    onGrantPermissions = onGrantPermissions,
                    onGrantLocationPermission = onGrantLocationPermission,
                    onEnableBluetooth = onEnableBluetooth,
                    onThresholdChange = onThresholdChange,
                    onLowerBoundChange = onLowerBoundChange,
                    onScan = onScan,
                    onSelectDevice = onSelectDevice,
                    onStartMonitoring = onStartMonitoring,
                    onStopMonitoring = onStopMonitoring,
                )
                1 -> HistoryTab(
                    modifier = Modifier.fillMaxSize(),
                    sessions = uiState.sessionHistory.filter { it.id != uiState.pendingDeleteId },
                    onDelete = onDeleteSession,
                )
            }
        }
    }
}

@Composable
private fun PageDots(currentPage: Int, pageCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}
