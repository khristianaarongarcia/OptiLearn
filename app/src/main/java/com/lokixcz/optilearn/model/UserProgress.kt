package com.lokixcz.optilearn.model

data class UserProgress(
    val id: Int = 1,
    val currentLevel: Int = 1,
    val totalScore: Int = 0,
    val optiHints: Int = 0,
    val completedLevels: Int = 0,
    val perfectScores: Int = 0
) {
    fun getOverallProgress(): Int {
        return (completedLevels * 100) / 15 // 15 total levels
    }
    
    fun canUseOptiHint(): Boolean {
        return optiHints > 0
    }
    
    fun hasCompletedAllLevels(): Boolean {
        return completedLevels >= 15
    }
    
    fun getAverageScore(): Int {
        return if (completedLevels > 0) totalScore / completedLevels else 0
    }
    
    fun getTitle(): String {
        return when {
            completedLevels >= 15 -> "Optics Legend"
            completedLevels >= 12 -> "Optics Master"
            completedLevels >= 9 -> "Optics Expert"
            completedLevels >= 6 -> "Optics Scholar"
            completedLevels >= 3 -> "Optics Student"
            else -> "Optics Beginner"
        }
    }
}
