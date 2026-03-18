package com.x.hrbeep

import org.junit.Assert.assertEquals
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
}
