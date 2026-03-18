package com.x.hrbeep.monitoring

import org.junit.Assert.assertEquals
import org.junit.Test

class HeartRateSampleAccumulatorTest {
    @Test
    fun `updates running average in constant time`() {
        val accumulator = HeartRateSampleAccumulator()

        assertEquals(120, accumulator.record(120))
        assertEquals(130, accumulator.record(140))
        assertEquals(120, accumulator.record(100))
        assertEquals(145, accumulator.record(220))
    }
}
