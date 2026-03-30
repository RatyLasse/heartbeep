package com.x.heartbeep

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomIconsTest {

    @Test
    fun historyIconStillExistsForEmptyState() {
        val source = loadCustomIconsSource()

        assertTrue(source.contains("""name = "History""""))
    }

    @Test
    fun historyTabUsesVisibleTextActionsForImportAndExport() {
        val source = loadHistoryTabSource()

        assertTrue(source.contains("TextButton("))
        assertTrue(source.contains("""text = "Import""""))
        assertTrue(source.contains("""text = "Export""""))
        assertTrue(source.contains("color = MaterialTheme.colorScheme.onSurface"))
        assertTrue(source.contains("contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)"))
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
