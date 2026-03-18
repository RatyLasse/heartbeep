package com.x.hrbeep.monitoring

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmDeciderTest {
    @Test
    fun `beeps when first crossing above threshold`() {
        val decider = AlarmDecider(minimumIntervalMs = 300L, maximumIntervalMs = 2_000L)

        assertTrue(decider.shouldBeep(currentHr = 151, threshold = 150, nowElapsedMs = 100L))
    }

    @Test
    fun `suppresses repeated beeps until the bpm-based interval passes`() {
        val decider = AlarmDecider(minimumIntervalMs = 300L, maximumIntervalMs = 2_000L)

        assertTrue(decider.shouldBeep(currentHr = 120, threshold = 100, nowElapsedMs = 100L))
        assertFalse(decider.shouldBeep(currentHr = 120, threshold = 100, nowElapsedMs = 450L))
        assertTrue(decider.shouldBeep(currentHr = 120, threshold = 100, nowElapsedMs = 650L))
    }

    @Test
    fun `uses current bpm cadence even when it differs from the threshold`() {
        val decider = AlarmDecider(minimumIntervalMs = 300L, maximumIntervalMs = 2_000L)

        assertTrue(decider.shouldBeep(currentHr = 150, threshold = 145, nowElapsedMs = 100L))
        assertFalse(decider.shouldBeep(currentHr = 150, threshold = 145, nowElapsedMs = 450L))
        assertTrue(decider.shouldBeep(currentHr = 150, threshold = 145, nowElapsedMs = 500L))
    }

    @Test
    fun `resets once heart rate drops back below threshold`() {
        val decider = AlarmDecider(minimumIntervalMs = 300L, maximumIntervalMs = 2_000L)

        assertTrue(decider.shouldBeep(currentHr = 151, threshold = 150, nowElapsedMs = 100L))
        assertFalse(decider.shouldBeep(currentHr = 149, threshold = 150, nowElapsedMs = 500L))
        assertTrue(decider.shouldBeep(currentHr = 151, threshold = 150, nowElapsedMs = 600L))
    }
}
