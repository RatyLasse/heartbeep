package com.x.hrbeep.monitoring

import com.x.hrbeep.data.HeartRateMonitorUpdate
import com.x.hrbeep.data.HeartRateSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
        assertNull(monitoring.distanceMeters)
        assertFalse(monitoring.isDistanceTrackingEnabled)
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
            distanceMeters = 3_620.0,
            isDistanceTrackingEnabled = true,
            batteryLevelPercent = 76,
            threshold = 150,
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
        ).endMonitoring()

        assertFalse(stopped.isMonitoring)
        assertEquals(ConnectionState.Connected, stopped.connectionState)
        assertEquals(156, stopped.currentHr)
        assertEquals(148, stopped.averageHr)
        assertEquals(3_620.0, stopped.distanceMeters!!, 0.0)
        assertTrue(stopped.isDistanceTrackingEnabled)
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

    @Test
    fun `begin monitoring clears previous session distance`() {
        val restarted = MonitoringSessionState(
            averageHr = 148,
            distanceMeters = 4_280.0,
            isDistanceTrackingEnabled = true,
            connectionState = ConnectionState.Connected,
        ).beginMonitoring(threshold = 150)

        assertNull(restarted.averageHr)
        assertNull(restarted.distanceMeters)
        assertFalse(restarted.isDistanceTrackingEnabled)
    }

    @Test
    fun `clear selected device resets distance state`() {
        val cleared = MonitoringSessionState(
            isMonitoring = true,
            connectionState = ConnectionState.Monitoring,
            distanceMeters = 2_145.0,
            isDistanceTrackingEnabled = true,
            averageHr = 141,
            threshold = 150,
            deviceName = "Polar H10",
            deviceAddress = "AA:BB",
        ).clearSelectedDevice()

        assertFalse(cleared.isMonitoring)
        assertEquals(ConnectionState.Idle, cleared.connectionState)
        assertNull(cleared.distanceMeters)
        assertFalse(cleared.isDistanceTrackingEnabled)
        assertNull(cleared.averageHr)
        assertNull(cleared.threshold)
    }
}
