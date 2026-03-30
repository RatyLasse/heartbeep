package com.x.heartbeep.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource
import java.io.StringReader

private fun tcxDateFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

fun buildTcx(sessions: List<SessionRecord>): String {
    val fmt = tcxDateFormat()
    return buildString {
        appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        appendLine("""<TrainingCenterDatabase xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">""")
        appendLine("  <Activities>")
        for (s in sessions) {
            val startTime = fmt.format(Date(s.startTimeMs))
            appendLine("""    <Activity Sport="Other">""")
            appendLine("      <Id>$startTime</Id>")
            appendLine("""      <Lap StartTime="$startTime">""")
            appendLine("        <TotalTimeSeconds>${s.durationSeconds}</TotalTimeSeconds>")
            appendLine("        <DistanceMeters>${s.distanceMeters ?: 0.0}</DistanceMeters>")
            appendLine("        <Calories>0</Calories>")
            appendLine("        <Intensity>Active</Intensity>")
            appendLine("        <TriggerMethod>Manual</TriggerMethod>")
            val samples = s.hrHistoryList()
            if (samples.isNotEmpty()) {
                val intervalMs = (s.durationSeconds * 1000.0 / samples.size).toLong()
                appendLine("        <Track>")
                for ((i, bpm) in samples.withIndex()) {
                    val time = fmt.format(Date(s.startTimeMs + i * intervalMs))
                    appendLine("          <Trackpoint>")
                    appendLine("            <Time>$time</Time>")
                    appendLine("            <HeartRateBpm><Value>$bpm</Value></HeartRateBpm>")
                    appendLine("          </Trackpoint>")
                }
                appendLine("        </Track>")
            }
            if (s.averageHr != null) {
                appendLine("        <AverageHeartRateBpm><Value>${s.averageHr}</Value></AverageHeartRateBpm>")
            }
            val maxHr = samples.maxOrNull()
            if (maxHr != null) {
                appendLine("        <MaximumHeartRateBpm><Value>$maxHr</Value></MaximumHeartRateBpm>")
            }
            appendLine("      </Lap>")
            appendLine("    </Activity>")
        }
        appendLine("  </Activities>")
        appendLine("</TrainingCenterDatabase>")
    }
}

fun parseTcx(tcx: String): List<SessionRecord> {
    val factory = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = false
    }
    val doc = factory.newDocumentBuilder().parse(InputSource(StringReader(tcx)))
    val activities = doc.getElementsByTagName("Activity")
    val fmt = tcxDateFormat()
    val results = mutableListOf<SessionRecord>()

    for (a in 0 until activities.length) {
        val activity = activities.item(a)
        val laps = activity.childNodes
        for (l in 0 until laps.length) {
            val lap = laps.item(l)
            if (lap.nodeName != "Lap") continue

            val startTimeStr = lap.attributes?.getNamedItem("StartTime")?.textContent
            val startTimeMs = startTimeStr?.let { runCatching { fmt.parse(it)?.time }.getOrNull() }
                ?: continue

            var durationSeconds = 0
            var distanceMeters: Double? = null
            var averageHr: Int? = null
            val hrSamples = mutableListOf<Int>()

            val lapChildren = lap.childNodes
            for (c in 0 until lapChildren.length) {
                val child = lapChildren.item(c)
                when (child.nodeName) {
                    "TotalTimeSeconds" -> {
                        durationSeconds = child.textContent.trim().toDoubleOrNull()?.toInt() ?: 0
                    }
                    "DistanceMeters" -> {
                        val d = child.textContent.trim().toDoubleOrNull()
                        if (d != null && d > 0.0) distanceMeters = d
                    }
                    "AverageHeartRateBpm" -> {
                        averageHr = firstChildValue(child)
                    }
                    "Track" -> {
                        val trackpoints = child.childNodes
                        for (t in 0 until trackpoints.length) {
                            val tp = trackpoints.item(t)
                            if (tp.nodeName != "Trackpoint") continue
                            val tpChildren = tp.childNodes
                            for (h in 0 until tpChildren.length) {
                                val hNode = tpChildren.item(h)
                                if (hNode.nodeName == "HeartRateBpm") {
                                    firstChildValue(hNode)?.let { hrSamples.add(it) }
                                }
                            }
                        }
                    }
                }
            }

            val paceSecondsPerKm = if (distanceMeters != null && distanceMeters > 0 && durationSeconds > 0) {
                (durationSeconds * 1000.0 / distanceMeters).toInt()
            } else {
                null
            }

            results.add(
                SessionRecord(
                    startTimeMs = startTimeMs,
                    durationSeconds = durationSeconds,
                    averageHr = averageHr ?: hrSamples.takeIf { it.isNotEmpty() }?.average()?.toInt(),
                    distanceMeters = distanceMeters,
                    paceSecondsPerKm = paceSecondsPerKm,
                    hrHistory = hrSamples.takeIf { it.isNotEmpty() }?.joinToString(","),
                )
            )
        }
    }
    return results
}

private fun firstChildValue(parent: org.w3c.dom.Node): Int? {
    val children = parent.childNodes
    for (i in 0 until children.length) {
        if (children.item(i).nodeName == "Value") {
            return children.item(i).textContent.trim().toIntOrNull()
        }
    }
    return null
}
