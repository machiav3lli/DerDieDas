package com.machiav3lli.derdiedas.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class WordViewModel(private val nounDao: NounDao, application: Application) :
    AndroidViewModel(application) {

    val pending: StateFlow<Int>
        field = MutableStateFlow(0)

    val masteredCount: StateFlow<Int>
        field = MutableStateFlow(0)

    val allNouns = masteredCount.flatMapLatest {
        nounDao.getActiveNouns(LEARNING_WINDOW_SIZE + (it / NEW_NOUN_BATCH_SIZE * NEW_NOUN_BATCH_SIZE))
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        listOf(Noun(0, "Jahr", "n", 0))
    )

    init {
        updateCounts()
    }

    private fun updateCounts() {
        viewModelScope.launch {
            masteredCount.update { nounDao.getMasteredNounCount() }
            pending.update { nounDao.getPendingNounCount() }
        }
    }

    fun handleAnswer(noun: Noun, isCorrect: Boolean) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()

            val hoursSinceLastReview = if (noun.lastReviewedAt > 0) {
                (now - noun.lastReviewedAt) / (1000.0 * 60.0 * 60.0)
            } else 0.0

            val updatedNoun = if (isCorrect) {
                val newStreak = noun.correctStreak + 1
                val newScore = calculateCorrectScore(
                    currentScore = noun.reviewScore,
                    streak = newStreak,
                    hoursSinceLastReview = hoursSinceLastReview
                )

                val mastered = newStreak >= MASTERY_STREAK

                noun.copy(
                    reviewScore = newScore,
                    correctStreak = newStreak,
                    lastReviewedAt = now,
                    totalReviews = noun.totalReviews + 1,
                    isMastered = mastered
                )
            } else {
                val newStreak = max(0, 0)
                val newScore = calculateWrongScore(
                    currentScore = noun.reviewScore,
                    previousStreak = noun.correctStreak,
                    hoursSinceLastReview = hoursSinceLastReview
                )

                noun.copy(
                    reviewScore = newScore,
                    correctStreak = newStreak,
                    lastReviewedAt = now,
                    totalReviews = noun.totalReviews + 1
                )
            }

            nounDao.update(updatedNoun)
            updateCounts()
        }
    }

    fun resetMasteredNouns() {
        viewModelScope.launch {
            nounDao.resetAllToActive()
            updateCounts()
        }
    }

    fun resetWithNouns(nouns: List<Noun>) {
        viewModelScope.launch {
            nounDao.deleteAll()
            nouns.forEachIndexed { index, noun ->
                val nounWithOrder = noun.copy(
                    originalOrder = index,
                    reviewScore = INITIAL_SCORE,
                    correctStreak = 0,
                    lastReviewedAt = System.currentTimeMillis(),
                    totalReviews = 0,
                    isMastered = false
                )
                nounDao.insert(nounWithOrder)
            }

            updateCounts()
        }
    }

    class Factory(
        private val database: NounDao,
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
                return WordViewModel(database, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}