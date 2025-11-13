package com.lokixcz.optilearn.model

data class Level(
    val levelId: Int,
    val title: String,
    val badgeName: String,
    val badgeIcon: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val highScore: Int = 0,
    val attempts: Int = 0
) {
    fun getProgressPercentage(): Int {
        return if (isCompleted) 100 else 0
    }
    
    fun getStarRating(): Int {
        return when {
            highScore >= 100 -> 3 // Perfect score
            highScore >= 80 -> 2  // Pass threshold
            highScore >= 60 -> 1  // Partial
            else -> 0             // Failed
        }
    }
    
    fun canUnlock(): Boolean {
        return isUnlocked || levelId == 1
    }
}
