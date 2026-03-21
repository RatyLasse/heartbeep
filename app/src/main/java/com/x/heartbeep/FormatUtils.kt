package com.x.heartbeep

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatKilometers(distanceMeters: Double): String =
    String.format(Locale.US, "%.2f", distanceMeters / 1_000.0)

fun formatPace(paceSecondsPerKm: Int): String {
    val minutes = paceSecondsPerKm / 60
    val seconds = paceSecondsPerKm % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

fun formatSessionDate(epochMs: Long): String =
    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(epochMs))

fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}

fun formatDurationLong(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) {
        "$h:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    } else {
        "$m:${s.toString().padStart(2, '0')}"
    }
}
