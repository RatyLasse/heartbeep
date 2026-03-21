package com.x.hrbeep.ui.monitoring

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import com.x.hrbeep.ui.NeonCyan
import com.x.hrbeep.ui.NeonRed

@Composable
internal fun StartStopButton(
    isMonitoring: Boolean,
    enabled: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    if (isMonitoring) {
        val stopShape = RoundedCornerShape(14.dp)
        OutlinedButton(
            onClick = onStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(
                    width = 1.5.dp,
                    color = NeonRed.copy(alpha = 0.7f),
                    shape = stopShape,
                ),
            shape = stopShape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = NeonRed,
            ),
        ) {
            Text(
                "\u25A0  Stop",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            )
        }
    } else {
        Button(
            onClick = onStart,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = Color(0xFF0A1018),
                disabledContainerColor = NeonCyan.copy(alpha = 0.25f),
                disabledContentColor = Color(0xFF0A1018).copy(alpha = 0.5f),
            ),
        ) {
            Text(
                "Start",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            )
        }
    }
}
