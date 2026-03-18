package com.x.hrbeep.monitoring

import com.x.hrbeep.data.BleHeartRateRepository
import com.x.hrbeep.data.HeartRateMonitorUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface HeartRateConnectionEvent {
    data class Update(
        val deviceName: String,
        val deviceAddress: String,
        val update: HeartRateMonitorUpdate,
    ) : HeartRateConnectionEvent

    data class ConnectionLost(
        val deviceName: String,
        val deviceAddress: String,
        val errorMessage: String,
    ) : HeartRateConnectionEvent
}

class HeartRateConnectionManager(
    private val bleHeartRateRepository: BleHeartRateRepository,
    private val monitoringController: MonitoringController,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _events = MutableSharedFlow<HeartRateConnectionEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    private var targetDevice: ObservedDevice? = null
    private var connectionJob: Job? = null

    fun observeDevice(
        deviceName: String,
        deviceAddress: String,
    ) {
        val nextDevice = ObservedDevice(deviceName = deviceName, deviceAddress = deviceAddress)
        if (targetDevice == nextDevice && connectionJob?.isActive == true) {
            return
        }

        targetDevice = nextDevice
        connectionJob?.cancel()
        connectionJob = scope.launch {
            observeLoop(nextDevice)
        }
    }

    fun clearObservedDevice() {
        targetDevice = null
        connectionJob?.cancel()
        connectionJob = null
        monitoringController.clearSelectedDevice()
    }

    private suspend fun observeLoop(device: ObservedDevice) {
        while (currentCoroutineContext().isActive &&
            targetDevice == device &&
            bleHeartRateRepository.isBluetoothEnabled()
        ) {
            monitoringController.onConnectionAttempt(
                deviceName = device.deviceName,
                deviceAddress = device.deviceAddress,
            )

            var failureMessage: String? = null
            bleHeartRateRepository.observeHeartRateMonitor(device.deviceAddress)
                .catch { throwable ->
                    failureMessage = throwable.message ?: "Heart-rate strap disconnected."
                }
                .collect { update ->
                    monitoringController.onConnectionUpdate(
                        deviceName = device.deviceName,
                        deviceAddress = device.deviceAddress,
                        update = update,
                    )
                    _events.tryEmit(
                        HeartRateConnectionEvent.Update(
                            deviceName = device.deviceName,
                            deviceAddress = device.deviceAddress,
                            update = update,
                        ),
                    )
                }

            if (!currentCoroutineContext().isActive || targetDevice != device) {
                break
            }

            val errorMessage = failureMessage ?: "Heart-rate strap disconnected."
            monitoringController.onConnectionLost(errorMessage)
            _events.tryEmit(
                HeartRateConnectionEvent.ConnectionLost(
                    deviceName = device.deviceName,
                    deviceAddress = device.deviceAddress,
                    errorMessage = errorMessage,
                ),
            )

            delay(RECONNECT_DELAY_MS)
        }
    }

    private data class ObservedDevice(
        val deviceName: String,
        val deviceAddress: String,
    )

    private companion object {
        const val RECONNECT_DELAY_MS = 1_000L
    }
}
