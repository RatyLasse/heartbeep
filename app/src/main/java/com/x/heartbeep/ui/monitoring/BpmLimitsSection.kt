package com.x.heartbeep.ui.monitoring

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x.heartbeep.MainUiState
import com.x.heartbeep.data.ThresholdRepository
import com.x.heartbeep.ui.CardBackground
import com.x.heartbeep.ui.NeonCyan
import com.x.heartbeep.ui.SubCardBackground
import com.x.heartbeep.ui.SurfaceInset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun BpmLimitsSection(
    uiState: MainUiState,
    onThresholdChange: (String) -> Unit,
    onLowerBoundChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "BPM Limits",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BpmLimitCard(
                label = "Min BPM",
                inputValue = uiState.lowerBoundInput,
                onValueChange = onLowerBoundChange,
                onDecrement = {
                    val lb = uiState.persistedLowerBound
                    if (lb != null) onLowerBoundChange(if (lb <= 20) "" else (lb - 1).toString())
                },
                onIncrement = {
                    val lb = uiState.persistedLowerBound
                    onLowerBoundChange(((lb ?: 39) + 1).coerceAtMost(300).toString())
                },
                imeAction = ImeAction.Next,
                modifier = Modifier.weight(1f),
            )
            BpmLimitCard(
                label = "Max BPM",
                inputValue = uiState.thresholdInput,
                onValueChange = onThresholdChange,
                onDecrement = {
                    val t = uiState.persistedThreshold
                    if (t != null) onThresholdChange((t - 1).coerceAtLeast(20).toString())
                },
                onIncrement = {
                    val t = uiState.persistedThreshold
                    onThresholdChange(((t ?: (ThresholdRepository.DEFAULT_THRESHOLD_BPM - 1)) + 1).coerceAtMost(300).toString())
                },
                imeAction = ImeAction.Done,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BpmLimitCard(
    label: String,
    inputValue: String,
    onValueChange: (String) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val cardShape = RoundedCornerShape(14.dp)
    val segmentCorner = 10.dp

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(CardBackground)
            .padding(top = 12.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Minus button — left rounded corners only
            RepeatButton(
                action = onDecrement,
                shape = RoundedCornerShape(topStart = segmentCorner, bottomStart = segmentCorner),
                background = SubCardBackground,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    "\u2212",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Value field — rectangular, darkest background
            BasicTextField(
                value = inputValue,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .background(SurfaceInset)
                    .padding(vertical = 14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = imeAction,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) },
                    onDone = { focusManager.clearFocus() },
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(NeonCyan),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                        innerTextField()
                    }
                },
            )

            // Plus button — right rounded corners only
            RepeatButton(
                action = onIncrement,
                shape = RoundedCornerShape(topEnd = segmentCorner, bottomEnd = segmentCorner),
                background = SubCardBackground,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    "+",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
internal fun RepeatButton(
    action: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    background: Color = SubCardBackground,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentAction by rememberUpdatedState(action)
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    currentAction()
                    val job = scope.launch {
                        delay(400L)
                        var interval = 150L
                        while (true) {
                            currentAction()
                            delay(interval)
                            interval = (interval - 15L).coerceAtLeast(60L)
                        }
                    }
                    waitForUpOrCancellation()
                    job.cancel()
                }
            }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
