package com.lokixcz.optilearn.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val currentLevel: Int = 1,
    val totalScore: Int = 0,
    val optiHints: Int = 0,
    val completedLevels: Int = 0,
    val perfectScores: Int = 0
)
