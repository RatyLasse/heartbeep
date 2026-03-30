package com.x.heartbeep

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomIconsTest {

    @Test
    fun fileDownloadIconMatchesMaterialOutlinedGeometry() {
        val source = loadCustomIconsSource()

        assertTrue(source.contains("""name = "Outlined.FileDownload""""))
        assertTrue(source.contains("moveTo(18f, 15f)"))
        assertTrue(source.contains("curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)"))
        assertTrue(source.contains("moveTo(17f, 11f)"))
        assertTrue(source.contains("lineToRelative(-1.41f, -1.41f)"))
        assertTrue(source.contains("lineTo(8.41f, 9.59f)"))
    }

    @Test
    fun fileUploadIconMatchesMaterialOutlinedGeometry() {
        val source = loadCustomIconsSource()

        assertTrue(source.contains("""name = "Outlined.FileUpload""""))
        assertTrue(source.contains("moveTo(18f, 15f)"))
        assertTrue(source.contains("curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)"))
        assertTrue(source.contains("moveTo(7f, 9f)"))
        assertTrue(source.contains("lineTo(11f, 7.83f)"))
        assertTrue(source.contains("lineToRelative(2.59f, 2.58f)"))
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
}
