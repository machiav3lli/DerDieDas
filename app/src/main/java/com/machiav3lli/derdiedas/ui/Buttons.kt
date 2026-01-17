package com.machiav3lli.derdiedas.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun GenderButton(
    gender: String,
    text: String,
    correctGender: String,
    selectedGender: String?,
    isAnswered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedGender == gender
    val isCorrect = gender == correctGender
    val shouldFlash = isAnswered && isCorrect && !isSelected
    // Flash animation for correct button when wrong answer
    val currentShouldFlash by rememberUpdatedState(shouldFlash)
    var flashState by remember { mutableIntStateOf(0) }

    LaunchedEffect(shouldFlash) {
        if (shouldFlash) {
            repeat(4) {
                flashState = 1
                delay(250)
                flashState = 0
                delay(250)
            }
        }
    }

    val containerColor by animateColorAsState(
        when {
            isAnswered && isSelected && isCorrect -> MaterialTheme.colorScheme.primaryContainer
            isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
            currentShouldFlash && flashState == 1 -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondaryContainer
        }, label = "containerColor"
    )
    val contentColor by animateColorAsState(
        when {
            isAnswered && isSelected && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
            isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
            currentShouldFlash && flashState == 1 -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onSecondaryContainer
        }, label = "contentColor"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor,
            contentColor = contentColor,
            disabledContentColor = contentColor,
        ),
        enabled = !isAnswered
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}