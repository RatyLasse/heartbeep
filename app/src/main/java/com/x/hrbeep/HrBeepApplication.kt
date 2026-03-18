package com.x.hrbeep

import android.app.Application
import com.x.hrbeep.data.BleHeartRateRepository
import com.x.hrbeep.data.ThresholdRepository
import com.x.hrbeep.monitoring.AlarmPlayer
import com.x.hrbeep.monitoring.HeartRateConnectionManager
import com.x.hrbeep.monitoring.MonitoringController

class HrBeepApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val monitoringController = MonitoringController()
        val bleHeartRateRepository = BleHeartRateRepository(this)
        appContainer = AppContainer(
            thresholdRepository = ThresholdRepository(this),
            bleHeartRateRepository = bleHeartRateRepository,
            alarmPlayer = AlarmPlayer(this),
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
    val alarmPlayer: AlarmPlayer,
    val monitoringController: MonitoringController,
    val heartRateConnectionManager: HeartRateConnectionManager,
)
