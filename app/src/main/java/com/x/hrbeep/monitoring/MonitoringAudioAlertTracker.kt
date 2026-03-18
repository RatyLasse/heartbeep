package com.x.hrbeep.monitoring

import com.x.hrbeep.data.HeartRateMonitorUpdate

enum class MonitoringAudioAlert {
    SensorConnected,
    SensorDisconnected,
}

class MonitoringAudioAlertTracker {
    var hasSeenLiveHeartRate = false
        private set

    fun onMonitoringStarted() {
        hasSeenLiveHeartRate = false
    }

    fun onMonitorUpdate(update: HeartRateMonitorUpdate): MonitoringAudioAlert? {
        if (hasSeenLiveHeartRate || update.heartRateSample == null) {
            return null
        }

        hasSeenLiveHeartRate = true
        return MonitoringAudioAlert.SensorConnected
    }

    fun onMonitoringFailure(): MonitoringAudioAlert? =
        if (hasSeenLiveHeartRate) {
            MonitoringAudioAlert.SensorDisconnected
        } else {
            null
        }
}
