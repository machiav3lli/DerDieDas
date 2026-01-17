package com.machiav3lli.derdiedas.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Noun(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noun: String,
    val gender: String,
    val timesAnswered: Int,
    @ColumnInfo(defaultValue = "0")
    val originalOrder: Int = 0,
    @ColumnInfo(defaultValue = "100.0")
    val reviewScore: Double = 100.0,
    @ColumnInfo(defaultValue = "0")
    val correctStreak: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val lastReviewedAt: Long = 0L,
    @ColumnInfo(defaultValue = "0")
    val totalReviews: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val isMastered: Boolean = false,
)