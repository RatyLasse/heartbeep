package com.x.heartbeep

import android.app.Application
import com.x.heartbeep.data.BleHeartRateRepository
import com.x.heartbeep.data.SessionDatabase
import com.x.heartbeep.data.SessionHistoryRepository
import com.x.heartbeep.data.ThresholdRepository
import com.x.heartbeep.monitoring.AlarmPlayer
import com.x.heartbeep.monitoring.GpsLocationTracker
import com.x.heartbeep.monitoring.HeartRateConnectionManager
import com.x.heartbeep.monitoring.MonitoringController

class HeartBeepApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val monitoringController = MonitoringController()
        val bleHeartRateRepository = BleHeartRateRepository(this)
        val sessionDb = SessionDatabase.getInstance(this)
        appContainer = AppContainer(
            thresholdRepository = ThresholdRepository(this),
            bleHeartRateRepository = bleHeartRateRepository,
            sessionHistoryRepository = SessionHistoryRepository(sessionDb.sessionDao()),
            alarmPlayer = AlarmPlayer(this),
            gpsLocationTracker = GpsLocationTracker(this),
            monitoringController = monitoringController,
            heartRateConnectionManager = HeartRateConnectionManager(
                bleHeartRateRepository = bleHeartRateRepository,
                monitoringController = monitoringController,
            ),
        )
    }
}

data class AppContainer(
    val thresholdRepository: ThresholdRepository,
    val bleHeartRateRepository: BleHeartRateRepository,
    val sessionHistoryRepository: SessionHistoryRepository,
    val alarmPlayer: AlarmPlayer,
    val gpsLocationTracker: GpsLocationTracker,
    val monitoringController: MonitoringController,
    val heartRateConnectionManager: HeartRateConnectionManager,
)
