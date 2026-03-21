package com.x.heartbeep.data

data class HeartRateMonitorUpdate(
    val heartRateSample: HeartRateSample? = null,
    val batteryLevelPercent: Int? = null,
)
