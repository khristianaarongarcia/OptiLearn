package com.lokixcz.optilearn.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey
    val levelId: Int,
    val title: String,
    val badgeName: String,
    val badgeIcon: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val highScore: Int = 0,
    val attempts: Int = 0
)
