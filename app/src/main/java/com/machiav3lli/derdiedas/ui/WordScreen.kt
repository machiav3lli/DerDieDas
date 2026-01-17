package com.machiav3lli.derdiedas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.machiav3lli.derdiedas.R
import com.machiav3lli.derdiedas.data.Noun
import com.machiav3lli.derdiedas.data.WordViewModel
import com.machiav3lli.derdiedas.ui.icons.Phosphor
import com.machiav3lli.derdiedas.ui.icons.phosphor.ArrowCircleLeft
import com.machiav3lli.derdiedas.utils.createNounListFromAsset
import com.machiav3lli.derdiedas.utils.getStringByName
import kotlinx.coroutines.delay

@Composable
fun WordScreen(
    viewModel: WordViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allNouns by viewModel.allNouns.collectAsStateWithLifecycle()
    val activeCount by viewModel.pending.collectAsState()
    val masteredCount by viewModel.masteredCount.collectAsState()
    var currentNounData by remember {
        mutableStateOf(allNouns.firstOrNull() to 0)
    }

    LaunchedEffect(allNouns.firstOrNull()) {
        val currentNoun = allNouns.firstOrNull()
        if (currentNoun?.hashCode() != currentNounData.first?.hashCode()) {
            currentNounData = currentNoun to (currentNounData.second + 1)
        }
    }

    AnimatedContent(
        targetState = currentNounData,
        transitionSpec = {
            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        },
        label = "word_transition"
    ) { (noun, _) ->
        if (noun != null) {
            WordContent(
                noun = noun,
                activeCount = activeCount,
                masteredCount = masteredCount,
                onAnswerSelected = { isCorrect ->
                    viewModel.handleAnswer(noun, isCorrect)
                },
                onBack = onBack
            )
        } else {
            FinishedContent(
                masteredCount = masteredCount,
                onRestart = {
                    viewModel.resetWithNouns(context.createNounListFromAsset())
                },
                onReviewMastered = {
                    viewModel.resetMasteredNouns()
                },
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordContent(
    noun: Noun,
    activeCount: Int,
    masteredCount: Int,
    onAnswerSelected: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    var shouldJump by remember(noun.id) { mutableStateOf(false) }
    val jumpOffset by animateFloatAsState(
        targetValue = if (shouldJump) -100f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "jump"
    )

    LaunchedEffect(isAnswered) {
        if (isAnswered && selectedGender == noun.gender) {
            repeat(4) {
                shouldJump = it % 2 == 0
                delay(200)
            }
            onAnswerSelected(true)
        } else if (isAnswered) {
            delay(2000)
            onAnswerSelected(false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            R.string.topbar_state_FORMAT,
                            activeCount,
                            masteredCount
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Phosphor.ArrowCircleLeft,
                            contentDescription = stringResource(R.string.dialog_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NounDisplaySection(
                        noun = noun,
                        selectedGender = selectedGender,
                        isAnswered = isAnswered,
                        jumpOffset = jumpOffset,
                        modifier = Modifier.weight(1f)
                    )

                    GenderButtonsSection(
                        noun = noun,
                        selectedGender = selectedGender,
                        isAnswered = isAnswered,
                        onGenderSelected = { gender ->
                            if (!isAnswered) {
                                selectedGender = gender
                                isAnswered = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NounDisplaySection(
                        noun = noun,
                        selectedGender = selectedGender,
                        isAnswered = isAnswered,
                        jumpOffset = jumpOffset,
                        modifier = Modifier.weight(1f)
                    )

                    GenderButtonsSection(
                        noun = noun,
                        selectedGender = selectedGender,
                        isAnswered = isAnswered,
                        onGenderSelected = { gender ->
                            if (!isAnswered) {
                                selectedGender = gender
                                isAnswered = true
                            }
                        },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NounDisplaySection(
    noun: Noun,
    selectedGender: String?,
    isAnswered: Boolean,
    jumpOffset: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .graphicsLayer { translationY = jumpOffset }
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val displayText = if (isAnswered && selectedGender != null) {
            val article = when (selectedGender) {
                "f" -> stringResource(R.string.die)
                "m" -> stringResource(R.string.der)
                else -> stringResource(R.string.das)
            }
            "$article ${noun.noun}"
        } else {
            noun.noun
        }

        Text(
            text = displayText,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = context.getStringByName(noun.noun),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        if (noun.correctStreak > 0) {
            Text(
                text = stringResource(R.string.streak_state_FORMAT, noun.correctStreak),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun GenderButtonsSection(
    noun: Noun,
    selectedGender: String?,
    isAnswered: Boolean,
    modifier: Modifier = Modifier,
    onGenderSelected: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GenderButton(
            gender = "m",
            text = stringResource(R.string.der),
            correctGender = noun.gender,
            selectedGender = selectedGender,
            isAnswered = isAnswered,
            onClick = { onGenderSelected("m") },
        )

        GenderButton(
            gender = "f",
            text = stringResource(R.string.die),
            correctGender = noun.gender,
            selectedGender = selectedGender,
            isAnswered = isAnswered,
            onClick = { onGenderSelected("f") },
        )

        GenderButton(
            gender = "n",
            text = stringResource(R.string.das),
            correctGender = noun.gender,
            selectedGender = selectedGender,
            isAnswered = isAnswered,
            onClick = { onGenderSelected("n") },
        )
    }
}

@Composable
fun FinishedContent(
    masteredCount: Int,
    onRestart: () -> Unit,
    onReviewMastered: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.finished),
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(R.string.mastered_all_nouns_FORMAT, masteredCount),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.restart_session))
            }

            if (masteredCount > 0) {
                Button(
                    onClick = onReviewMastered,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.review_mastered_nouns))
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.dialog_exit))
            }
        }
    }
}