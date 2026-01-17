package com.machiav3lli.derdiedas.data

fun calculateCorrectScore(
    currentScore: Double,
    streak: Int,
    hoursSinceLastReview: Double
): Double {
    val streakBonus = 0.1 * streak
    val multiplier = (CORRECT_SCORE_MULTIPLIER - streakBonus).coerceAtLeast(0.1)

    val baseScore = currentScore * multiplier
    val timeDecay = hoursSinceLastReview * TIME_DECAY_PER_HOUR

    return (baseScore + timeDecay).coerceIn(MIN_SCORE, MAX_SCORE)
}

fun calculateWrongScore(
    currentScore: Double,
    previousStreak: Int,
    hoursSinceLastReview: Double
): Double {
    val progressReduction = previousStreak * 5.0
    val penalty = (WRONG_SCORE_INCREASE - progressReduction).coerceAtLeast(20.0)

    val baseScore = currentScore * 1.3 + penalty
    val timeDecay = hoursSinceLastReview * TIME_DECAY_PER_HOUR

    return (baseScore + timeDecay).coerceIn(MIN_SCORE, MAX_SCORE)
}

const val REPETITION_FOR_WRONG = 10
const val REPETITION_FOR_CORRECT = 20
const val TIMES_TO_ANSWER_TO_REMOVE = 5
const val MASTERY_STREAK = 5
const val LEARNING_WINDOW_SIZE = 30
const val NEW_NOUN_BATCH_SIZE = 10

private const val MIN_SCORE = 0.0
const val INITIAL_SCORE = 100.0
private const val MAX_SCORE = 200.0

// Score increases over time
private const val TIME_DECAY_PER_HOUR = 0.5
private const val CORRECT_SCORE_MULTIPLIER = 0.6
private const val WRONG_SCORE_INCREASE = 40.0
