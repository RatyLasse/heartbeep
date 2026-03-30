package com.x.heartbeep.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Filled History icon (from Material Design icons). */
val HistoryIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "History",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path {
            moveTo(13f, 3f)
            curveTo(8.03f, 3f, 4f, 7.03f, 4f, 12f)
            horizontalLineTo(1f)
            lineToRelative(3.89f, 3.89f)
            lineToRelative(0.07f, 0.14f)
            lineTo(9f, 12f)
            horizontalLineTo(6f)
            curveTo(6f, 8.13f, 9.13f, 5f, 13f, 5f)
            curveTo(16.87f, 5f, 20f, 8.13f, 20f, 12f)
            curveTo(20f, 15.87f, 16.87f, 19f, 13f, 19f)
            curveTo(11.07f, 19f, 9.32f, 18.21f, 8.06f, 16.94f)
            lineToRelative(-1.42f, 1.42f)
            curveTo(8.27f, 19.99f, 10.51f, 21f, 13f, 21f)
            curveTo(17.97f, 21f, 22f, 16.97f, 22f, 12f)
            curveTo(22f, 7.03f, 17.97f, 3f, 13f, 3f)
            close()
            moveTo(12f, 8f)
            verticalLineToRelative(5f)
            lineToRelative(4.28f, 2.54f)
            lineToRelative(0.72f, -1.21f)
            lineToRelative(-3.5f, -2.08f)
            verticalLineTo(8f)
            horizontalLineTo(12f)
            close()
        }
    }.build()
}

/** Outlined FileDownload icon (from Material Design icons). */
val FileDownloadIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.FileDownload",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path {
            moveTo(18f, 15f)
            verticalLineToRelative(3f)
            horizontalLineTo(6f)
            verticalLineToRelative(-3f)
            horizontalLineTo(4f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(12f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineToRelative(-3f)
            horizontalLineTo(18f)
            close()
            moveTo(17f, 11f)
            lineToRelative(-1.41f, -1.41f)
            lineTo(13f, 12.17f)
            verticalLineTo(4f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(8.17f)
            lineTo(8.41f, 9.59f)
            lineTo(7f, 11f)
            lineToRelative(5f, 5f)
            lineToRelative(5f, -5f)
            close()
        }
    }.build()
}

/** Outlined FileUpload icon (from Material Design icons). */
val FileUploadIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.FileUpload",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path {
            moveTo(18f, 15f)
            verticalLineToRelative(3f)
            horizontalLineTo(6f)
            verticalLineToRelative(-3f)
            horizontalLineTo(4f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(12f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineToRelative(-3f)
            horizontalLineTo(18f)
            close()
            moveTo(7f, 9f)
            lineToRelative(1.41f, 1.41f)
            lineTo(11f, 7.83f)
            verticalLineTo(16f)
            horizontalLineToRelative(2f)
            verticalLineTo(7.83f)
            lineToRelative(2.59f, 2.58f)
            lineTo(17f, 9f)
            lineToRelative(-5f, -5f)
            lineToRelative(-5f, 5f)
            close()
        }
    }.build()
}
