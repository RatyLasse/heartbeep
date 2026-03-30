package com.x.heartbeep.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TcxFormatTest {

    @Test
    fun `buildTcx then parseTcx round-trips sessions`() {
        val original = listOf(
            SessionRecord(
                startTimeMs = 1711800000000L,
                durationSeconds = 600,
                averageHr = 75,
                distanceMeters = 1500.0,
                paceSecondsPerKm = 400,
                hrHistory = "70,72,75,80,78",
                upperBound = 150,
                lowerBound = 60,
            ),
            SessionRecord(
                startTimeMs = 1711900000000L,
                durationSeconds = 300,
                averageHr = 90,
                distanceMeters = null,
                paceSecondsPerKm = null,
                hrHistory = "85,90,95",
            ),
        )

        val tcx = buildTcx(original)
        val parsed = parseTcx(tcx)

        assertEquals(2, parsed.size)

        assertEquals(original[0].startTimeMs, parsed[0].startTimeMs)
        assertEquals(original[0].durationSeconds, parsed[0].durationSeconds)
        assertEquals(original[0].averageHr, parsed[0].averageHr)
        assertEquals(original[0].distanceMeters!!, parsed[0].distanceMeters!!, 0.01)
        assertEquals(original[0].hrHistoryList(), parsed[0].hrHistoryList())

        assertEquals(original[1].startTimeMs, parsed[1].startTimeMs)
        assertEquals(original[1].durationSeconds, parsed[1].durationSeconds)
        assertEquals(original[1].averageHr, parsed[1].averageHr)
        assertNull(parsed[1].distanceMeters)
        assertEquals(original[1].hrHistoryList(), parsed[1].hrHistoryList())
    }

    @Test
    fun `parseTcx handles session without heart rate data`() {
        val tcx = """
            <?xml version="1.0" encoding="UTF-8"?>
            <TrainingCenterDatabase xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">
              <Activities>
                <Activity Sport="Other">
                  <Id>2024-03-30T12:00:00.000Z</Id>
                  <Lap StartTime="2024-03-30T12:00:00.000Z">
                    <TotalTimeSeconds>120</TotalTimeSeconds>
                    <DistanceMeters>500.0</DistanceMeters>
                    <Calories>0</Calories>
                  </Lap>
                </Activity>
              </Activities>
            </TrainingCenterDatabase>
        """.trimIndent()

        val parsed = parseTcx(tcx)
        assertEquals(1, parsed.size)
        assertEquals(120, parsed[0].durationSeconds)
        assertEquals(500.0, parsed[0].distanceMeters!!, 0.01)
        assertNull(parsed[0].averageHr)
        assertEquals(emptyList<Int>(), parsed[0].hrHistoryList())
    }

    @Test
    fun `parseTcx returns empty list for empty input`() {
        val tcx = """
            <?xml version="1.0" encoding="UTF-8"?>
            <TrainingCenterDatabase xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">
              <Activities>
              </Activities>
            </TrainingCenterDatabase>
        """.trimIndent()

        assertEquals(emptyList<SessionRecord>(), parseTcx(tcx))
    }

    @Test
    fun `parseTcx computes pace from distance and duration`() {
        val tcx = """
            <?xml version="1.0" encoding="UTF-8"?>
            <TrainingCenterDatabase xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">
              <Activities>
                <Activity Sport="Other">
                  <Id>2024-03-30T12:00:00.000Z</Id>
                  <Lap StartTime="2024-03-30T12:00:00.000Z">
                    <TotalTimeSeconds>600</TotalTimeSeconds>
                    <DistanceMeters>2000.0</DistanceMeters>
                    <Calories>0</Calories>
                  </Lap>
                </Activity>
              </Activities>
            </TrainingCenterDatabase>
        """.trimIndent()

        val parsed = parseTcx(tcx)
        // 600s / 2km = 300 s/km
        assertEquals(300, parsed[0].paceSecondsPerKm)
    }

    @Test
    fun `buildTcx exports zero distance when null`() {
        val session = SessionRecord(
            startTimeMs = 1711800000000L,
            durationSeconds = 60,
            averageHr = null,
            distanceMeters = null,
        )
        val tcx = buildTcx(listOf(session))
        assert(tcx.contains("<DistanceMeters>0.0</DistanceMeters>"))
    }
}
