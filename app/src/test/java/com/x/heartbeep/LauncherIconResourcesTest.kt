package com.x.heartbeep

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherIconResourcesTest {

    @Test
    fun launcherForegroundUsesGroupScaledViewport() {
        val xml = loadForegroundVector()

        // Icon uses a 24-unit viewport with a <group> to scale into the
        // 108dp adaptive-icon safe zone (same approach as the original icon).
        assertTrue(xml.contains("android:viewportWidth=\"24\""))
        assertTrue(xml.contains("android:viewportHeight=\"24\""))
        assertTrue(xml.contains("android:width=\"108dp\""))
        assertTrue(xml.contains("android:height=\"108dp\""))
    }

    @Test
    fun launcherForegroundHasHeartAndEcgPaths() {
        val xml = loadForegroundVector()

        // Must reference the heart stroke and ECG pulse colours
        assertTrue(xml.contains("@color/launcher_heart_stroke"))
        assertTrue(xml.contains("@color/launcher_pulse"))
    }

    @Test
    fun launcherForegroundHasNoOpaqueBackground() {
        val xml = loadForegroundVector()

        // Foreground layer must be transparent — no solid-fill rectangle that would
        // obscure the background layer of the adaptive icon.
        assertFalse(xml.contains("android:shape=\"rectangle\""))
    }

    private fun loadForegroundVector(): String {
        val candidates = listOf(
            File("src/main/res/drawable/ic_launcher_foreground.xml"),
            File("app/src/main/res/drawable/ic_launcher_foreground.xml"),
        )

        val resourceFile = candidates.firstOrNull(File::exists)
            ?: error("Could not locate ic_launcher_foreground.xml from ${System.getProperty("user.dir")}")

        return resourceFile.readText()
    }
}
