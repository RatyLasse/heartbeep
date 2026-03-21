package com.x.heartbeep.monitoring

internal class HeartRateSampleAccumulator {
    private var sampleCount: Long = 0
    private var totalBpm: Long = 0

    fun record(bpm: Int): Int {
        sampleCount += 1
        totalBpm += bpm.toLong()
        return (totalBpm / sampleCount).toInt()
    }
}
