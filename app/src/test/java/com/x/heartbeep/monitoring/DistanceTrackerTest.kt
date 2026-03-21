package com.x.heartbeep.monitoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DistanceTrackerTest {
    @Test
    fun `records distance and emits kilometer markers once`() {
        val tracker = DistanceTracker(minimumSegmentMeters = 1.0, maximumAccuracyMeters = 50f)

        val start = tracker.record(pointAt(distanceMeters = 0.0, accuracyMeters = 5f))
        val firstMarker = tracker.record(pointAt(distanceMeters = 400.0, accuracyMeters = 5f))
        val secondMarker = tracker.record(pointAt(distanceMeters = 1_050.0, accuracyMeters = 5f))
        val thirdMarker = tracker.record(pointAt(distanceMeters = 2_105.0, accuracyMeters = 5f))

        assertEquals(0.0, start!!.totalMeters, 0.01)
        assertTrue(firstMarker!!.completedKilometers.isEmpty())
        assertEquals(listOf(1), secondMarker!!.completedKilometers)
        assertEquals(listOf(2), thirdMarker!!.completedKilometers)
        assertEquals(2_105.0, thirdMarker.totalMeters, 8.0)
    }

    @Test
    fun `ignores inaccurate and tiny updates`() {
        val tracker = DistanceTracker(minimumSegmentMeters = 10.0, maximumAccuracyMeters = 20f)

        tracker.record(pointAt(distanceMeters = 0.0, accuracyMeters = 5f))

        assertNull(tracker.record(pointAt(distanceMeters = 2.0, accuracyMeters = 5f)))
        assertNull(tracker.record(pointAt(distanceMeters = 50.0, accuracyMeters = 45f)))

        val accepted = tracker.record(pointAt(distanceMeters = 25.0, accuracyMeters = 5f))

        assertEquals(25.0, accepted!!.totalMeters, 2.0)
        assertTrue(accepted.completedKilometers.isEmpty())
    }

    private fun pointAt(
        distanceMeters: Double,
        accuracyMeters: Float,
    ): LocationPoint = LocationPoint(
        latitude = BASE_LATITUDE + (distanceMeters / METERS_PER_DEGREE_LATITUDE),
        longitude = BASE_LONGITUDE,
        accuracyMeters = accuracyMeters,
    )

    private companion object {
        const val BASE_LATITUDE = 60.1699
        const val BASE_LONGITUDE = 24.9384
        const val METERS_PER_DEGREE_LATITUDE = 111_320.0
    }
}
