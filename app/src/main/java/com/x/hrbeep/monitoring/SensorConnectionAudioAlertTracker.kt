package com.x.hrbeep.monitoring

import com.x.hrbeep.data.HeartRateMonitorUpdate

class SensorConnectionAudioAlertTracker {
    var hasSeenLiveHeartRate = false
        private set

    fun onMonitoringStarted(hasLiveHeartRate: Boolean) {
        hasSeenLiveHeartRate = hasLiveHeartRate
    }

    fun onMonitorUpdate(update: HeartRateMonitorUpdate): SessionAudioAlert? {
        if (hasSeenLiveHeartRate || update.heartRateSample == null) {
            return null
        }

        hasSeenLiveHeartRate = true
        return SessionAudioAlert.SensorConnected
    }

    fun onMonitoringFailure(): SessionAudioAlert? =
        if (hasSeenLiveHeartRate) {
            hasSeenLiveHeartRate = false
            SessionAudioAlert.SensorDisconnected
        } else {
            null
        }
}
