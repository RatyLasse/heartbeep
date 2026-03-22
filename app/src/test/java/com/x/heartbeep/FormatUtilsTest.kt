package com.x.heartbeep

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatUtilsTest {
    @Test
    fun `formatPace formats whole minutes with zero seconds`() {
        assertEquals("5:00", formatPace(300))
    }

    @Test
    fun `formatPace pads seconds with leading zero`() {
        assertEquals("5:05", formatPace(305))
    }

    @Test
    fun `formatPace formats minutes and seconds correctly`() {
        assertEquals("6:30", formatPace(390))
    }

    @Test
    fun `formatPace formats fast pace correctly`() {
        assertEquals("3:15", formatPace(195))
    }

    @Test
    fun `formatPace formats slow pace correctly`() {
        assertEquals("10:01", formatPace(601))
    }

    @Test
    fun `formatSessionDate uses ISO date format with year`() {
        val result = formatSessionDate(1711200000000L) // 2024-03-23 in UTC
        assertTrue(
            "Expected YYYY-MM-DD HH:MM format but got: $result",
            result.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")),
        )
    }
}
