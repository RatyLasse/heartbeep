package com.x.hrbeep

import java.util.Locale

fun formatKilometers(distanceMeters: Double): String =
    String.format(Locale.US, "%.2f", distanceMeters / 1_000.0)

fun formatPace(paceSecondsPerKm: Int): String {
    val minutes = paceSecondsPerKm / 60
    val seconds = paceSecondsPerKm % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
