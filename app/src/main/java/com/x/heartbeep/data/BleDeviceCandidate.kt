package com.x.heartbeep.data

data class BleDeviceCandidate(
    val address: String,
    val name: String,
    val rssi: Int,
)

