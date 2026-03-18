package com.x.hrbeep.monitoring

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null,
)

data class DistanceProgress(
    val totalMeters: Double,
    val completedKilometers: List<Int> = emptyList(),
)

class DistanceTracker(
    private val minimumSegmentMeters: Double = 3.0,
    private val maximumAccuracyMeters: Float = 35f,
) {
    private var lastAcceptedPoint: LocationPoint? = null
    private var totalMeters: Double = 0.0
    private var announcedKilometers: Int = 0

    fun record(point: LocationPoint): DistanceProgress? {
        val accuracyMeters = point.accuracyMeters
        if (accuracyMeters != null && accuracyMeters > maximumAccuracyMeters) {
            return null
        }

        val previousPoint = lastAcceptedPoint
        if (previousPoint == null) {
            lastAcceptedPoint = point
            return DistanceProgress(totalMeters = totalMeters)
        }

        val segmentMeters = haversineMeters(previousPoint, point)
        if (segmentMeters < minimumSegmentMeters) {
            return null
        }

        lastAcceptedPoint = point
        totalMeters += segmentMeters

        val nextCompletedKilometer = floor(totalMeters / METERS_PER_KILOMETER).toInt()
        val completedKilometers = if (nextCompletedKilometer > announcedKilometers) {
            ((announcedKilometers + 1)..nextCompletedKilometer).toList()
        } else {
            emptyList()
        }
        announcedKilometers = nextCompletedKilometer

        return DistanceProgress(
            totalMeters = totalMeters,
            completedKilometers = completedKilometers,
        )
    }

    private fun haversineMeters(
        from: LocationPoint,
        to: LocationPoint,
    ): Double {
        val latitudeDelta = Math.toRadians(to.latitude - from.latitude)
        val longitudeDelta = Math.toRadians(to.longitude - from.longitude)
        val fromLatitude = Math.toRadians(from.latitude)
        val toLatitude = Math.toRadians(to.latitude)

        val haversine = sin(latitudeDelta / 2).pow(2.0) +
            cos(fromLatitude) * cos(toLatitude) * sin(longitudeDelta / 2).pow(2.0)
        val arc = 2 * asin(sqrt(haversine))
        return EARTH_RADIUS_METERS * arc
    }

    private companion object {
        const val EARTH_RADIUS_METERS = 6_371_000.0
        const val METERS_PER_KILOMETER = 1_000.0
    }
}
