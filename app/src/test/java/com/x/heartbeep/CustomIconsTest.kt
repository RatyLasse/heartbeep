package com.x.heartbeep

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomIconsTest {

    @Test
    fun fileDownloadIconUsesSimpleBoldGeometry() {
        val source = loadCustomIconsSource()

        assertTrue(source.contains("""name = "HistoryExport""""))
        assertTrue(source.contains("moveTo(5f, 18f)"))
        assertTrue(source.contains("lineToRelative(-4f, 5f)"))
        assertTrue(source.contains("horizontalLineToRelative(3f)"))
    }

    @Test
    fun fileUploadIconUsesSimpleBoldGeometry() {
        val source = loadCustomIconsSource()

        assertTrue(source.contains("""name = "HistoryImport""""))
        assertTrue(source.contains("moveTo(11f, 17f)"))
        assertTrue(source.contains("lineToRelative(-4f, -5f)"))
        assertTrue(source.contains("verticalLineTo(9f)"))
    }

    @Test
    fun historyTabUsesHighContrastTintForImportAndExportIcons() {
        val source = loadHistoryTabSource()

        assertTrue(source.contains("imageVector = FileUploadIcon"))
        assertTrue(source.contains("imageVector = FileDownloadIcon"))
        assertTrue(source.contains("tint = MaterialTheme.colorScheme.onSurface"))
        assertTrue(source.contains("modifier = Modifier.size(20.dp)"))
    }

    private fun loadCustomIconsSource(): String {
        val candidates = listOf(
            File("src/main/java/com/x/heartbeep/ui/CustomIcons.kt"),
            File("app/src/main/java/com/x/heartbeep/ui/CustomIcons.kt"),
        )

        val sourceFile = candidates.firstOrNull(File::exists)
            ?: error("Could not locate CustomIcons.kt from ${System.getProperty("user.dir")}")

        return sourceFile.readText()
    }

    private fun loadHistoryTabSource(): String {
        val candidates = listOf(
            File("src/main/java/com/x/heartbeep/ui/history/HistoryTab.kt"),
            File("app/src/main/java/com/x/heartbeep/ui/history/HistoryTab.kt"),
        )

        val sourceFile = candidates.firstOrNull(File::exists)
            ?: error("Could not locate HistoryTab.kt from ${System.getProperty("user.dir")}")

        return sourceFile.readText()
    }
}
