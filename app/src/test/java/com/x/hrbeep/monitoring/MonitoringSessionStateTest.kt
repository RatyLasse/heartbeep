package com.x.hrbeep.monitoring

import com.x.hrbeep.data.HeartRateMonitorUpdate
import com.x.hrbeep.data.HeartRateSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test

class MonitoringSessionStateTest {
    @Test
    fun `begin monitoring keeps an existing sensor connection`() {
        val monitoring = MonitoringSessionState(
            connectionState = ConnectionState.Connected,
            currentHr = 156,
            batteryLevelPercent = 82,
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
        ).beginMonitoring(threshold = 150)

        assertTrue(monitoring.isMonitoring)
        assertEquals(ConnectionState.Monitoring, monitoring.connectionState)
        assertEquals(156, monitoring.currentHr)
        assertEquals(82, monitoring.batteryLevelPercent)
        assertEquals(150, monitoring.threshold)
        assertEquals("Polar H10", monitoring.deviceName)
        assertEquals("AA:BB", monitoring.deviceAddress)
    }

    @Test
    fun `end monitoring keeps the connection alive`() {
        val stopped = MonitoringSessionState(
            isMonitoring = true,
            connectionState = ConnectionState.Monitoring,
            currentHr = 156,
            averageHr = 148,
            batteryLevelPercent = 76,
            threshold = 150,
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
        ).endMonitoring()

        assertFalse(stopped.isMonitoring)
        assertEquals(ConnectionState.Connected, stopped.connectionState)
        assertEquals(156, stopped.currentHr)
        assertEquals(148, stopped.averageHr)
        assertEquals(76, stopped.batteryLevelPercent)
        assertNull(stopped.threshold)
        assertEquals("Polar H10", stopped.deviceName)
        assertEquals("AA:BB", stopped.deviceAddress)
    }

    @Test
    fun `connection updates keep monitoring state active`() {
        val updated = MonitoringSessionState(
            isMonitoring = true,
            connectionState = ConnectionState.Connecting,
            threshold = 150,
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
        ).onConnectionUpdate(
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
            update = HeartRateMonitorUpdate(
                heartRateSample = HeartRateSample(
                    bpm = 151,
                    rrIntervalsMs = emptyList(),
                    contactDetected = true,
                    receivedAtElapsedMs = 1L,
                ),
                batteryLevelPercent = 68,
            ),
        )

        assertTrue(updated.isMonitoring)
        assertEquals(ConnectionState.Monitoring, updated.connectionState)
        assertEquals(151, updated.currentHr)
        assertEquals(68, updated.batteryLevelPercent)
        assertEquals(150, updated.threshold)
    }

    @Test
    fun `connection loss keeps monitoring active until manually stopped`() {
        val disconnected = MonitoringSessionState(
            isMonitoring = true,
            connectionState = ConnectionState.Monitoring,
            currentHr = 151,
            averageHr = 144,
            batteryLevelPercent = 68,
            threshold = 150,
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
        ).onConnectionLost("Heart-rate strap disconnected.")

        assertTrue(disconnected.isMonitoring)
        assertEquals(ConnectionState.Disconnected, disconnected.connectionState)
        assertEquals("Heart-rate strap disconnected.", disconnected.errorMessage)
        assertNull(disconnected.currentHr)
        assertEquals(144, disconnected.averageHr)
    }
}
