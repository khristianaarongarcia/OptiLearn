package com.lokixcz.optilearn.utils

import android.content.Context
import com.lokixcz.optilearn.database.AppDatabase
import com.lokixcz.optilearn.database.entity.LevelEntity
import com.lokixcz.optilearn.database.entity.QuestionEntity
import com.lokixcz.optilearn.database.entity.UserProgressEntity
import com.lokixcz.optilearn.model.Badge
import com.lokixcz.optilearn.model.Level
import com.lokixcz.optilearn.model.Question
import com.lokixcz.optilearn.model.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val userProgressDao = database.userProgressDao()
    private val levelDao = database.levelDao()
    private val questionDao = database.questionDao()
    
    // User Progress Operations
    suspend fun getUserProgress(): UserProgress? {
        return withContext(Dispatchers.IO) {
            userProgressDao.getUserProgressSync()?.let { entity ->
                UserProgress(
                    id = entity.id,
                    currentLevel = entity.currentLevel,
                    totalScore = entity.totalScore,
                    optiHints = entity.optiHints,
                    completedLevels = entity.completedLevels,
                    perfectScores = entity.perfectScores
                )
            }
        }
    }
    
    suspend fun initializeUserProgress() {
        withContext(Dispatchers.IO) {
            val existing = userProgressDao.getUserProgressSync()
            if (existing == null) {
                userProgressDao.insertUserProgress(
                    UserProgressEntity(
                        id = 1,
                        currentLevel = 1,
                        totalScore = 0,
                        optiHints = 0,
                        completedLevels = 0,
                        perfectScores = 0
                    )
                )
            }
        }
    }
    
    suspend fun updateUserProgress(progress: UserProgress) {
        withContext(Dispatchers.IO) {
            userProgressDao.updateUserProgress(
                UserProgressEntity(
                    id = progress.id,
                    currentLevel = progress.currentLevel,
                    totalScore = progress.totalScore,
                    optiHints = progress.optiHints,
                    completedLevels = progress.completedLevels,
                    perfectScores = progress.perfectScores
                )
            )
        }
    }
    
    suspend fun addScore(userId: Int, score: Int) {
        withContext(Dispatchers.IO) {
            userProgressDao.addScore(userId, score)
        }
    }
    
    suspend fun addOptiHints(userId: Int, hints: Int) {
        withContext(Dispatchers.IO) {
            userProgressDao.addOptiHints(userId, hints)
        }
    }
    
    suspend fun useOptiHint(userId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            userProgressDao.useOptiHint(userId) > 0
        }
    }
    
    suspend fun incrementCompletedLevels(userId: Int) {
        withContext(Dispatchers.IO) {
            userProgressDao.incrementCompletedLevels(userId)
        }
    }
    
    suspend fun incrementPerfectScores(userId: Int) {
        withContext(Dispatchers.IO) {
            userProgressDao.incrementPerfectScores(userId)
        }
    }
    
    /**
     * Reset all user progress to initial state
     * Resets user progress and locks all levels except level 1
     */
    suspend fun resetUserProgress() {
        withContext(Dispatchers.IO) {
            // Reset user progress to initial values
            userProgressDao.updateUserProgress(
                UserProgressEntity(
                    id = 1,
                    currentLevel = 1,
                    totalScore = 0,
                    optiHints = 0,
                    completedLevels = 0,
                    perfectScores = 0
                )
            )
            
            // Reset all levels - unlock level 1, lock all others
            val allLevels = levelDao.getAllLevelsSync()
            allLevels.forEach { level ->
                levelDao.updateLevel(
                    level.copy(
                        isUnlocked = level.levelId == 1,
                        isCompleted = false,
                        highScore = 0,
                        attempts = 0
                    )
                )
            }
        }
    }
    
    // Level Operations
    suspend fun getAllLevels(): List<Level> {
        return withContext(Dispatchers.IO) {
            levelDao.getAllLevelsSync().map { entity ->
                Level(
                    levelId = entity.levelId,
                    title = entity.title,
                    badgeName = entity.badgeName,
                    badgeIcon = entity.badgeIcon,
                    isUnlocked = entity.isUnlocked,
                    isCompleted = entity.isCompleted,
                    highScore = entity.highScore,
                    attempts = entity.attempts
                )
            }
        }
    }
    
    suspend fun getLevelById(levelId: Int): Level? {
        return withContext(Dispatchers.IO) {
            levelDao.getLevelById(levelId)?.let { entity ->
                Level(
                    levelId = entity.levelId,
                    title = entity.title,
                    badgeName = entity.badgeName,
                    badgeIcon = entity.badgeIcon,
                    isUnlocked = entity.isUnlocked,
                    isCompleted = entity.isCompleted,
                    highScore = entity.highScore,
                    attempts = entity.attempts
                )
            }
        }
    }
    
    suspend fun unlockLevel(levelId: Int) {
        withContext(Dispatchers.IO) {
            levelDao.unlockLevel(levelId)
        }
    }
    
    suspend fun completeLevel(levelId: Int) {
        withContext(Dispatchers.IO) {
            levelDao.completeLevel(levelId)
        }
    }
    
    suspend fun updateHighScore(levelId: Int, score: Int) {
        withContext(Dispatchers.IO) {
            levelDao.updateHighScore(levelId, score)
        }
    }
    
    suspend fun incrementAttempts(levelId: Int) {
        withContext(Dispatchers.IO) {
            levelDao.incrementAttempts(levelId)
        }
    }
    
    // Question Operations
    suspend fun getQuestionsByLevel(levelId: Int): List<Question> {
        return withContext(Dispatchers.IO) {
            questionDao.getQuestionsByLevel(levelId).map { entity ->
                Question(
                    questionId = entity.questionId,
                    levelId = entity.levelId,
                    questionText = entity.questionText,
                    optionA = entity.optionA,
                    optionB = entity.optionB,
                    optionC = entity.optionC,
                    optionD = entity.optionD,
                    correctAnswer = entity.correctAnswer,
                    explanation = entity.explanation,
                    imageResource = entity.imageResource
                )
            }
        }
    }
    
    suspend fun getQuestionCountByLevel(levelId: Int): Int {
        return withContext(Dispatchers.IO) {
            questionDao.getQuestionCountByLevel(levelId)
        }
    }
    
    suspend fun getRandomQuestions(levelId: Int, limit: Int = Constants.QUESTIONS_PER_LEVEL): List<Question> {
        return withContext(Dispatchers.IO) {
            questionDao.getRandomQuestions(levelId, limit).map { entity ->
                Question(
                    questionId = entity.questionId,
                    levelId = entity.levelId,
                    questionText = entity.questionText,
                    optionA = entity.optionA,
                    optionB = entity.optionB,
                    optionC = entity.optionC,
                    optionD = entity.optionD,
                    correctAnswer = entity.correctAnswer,
                    explanation = entity.explanation,
                    imageResource = entity.imageResource
                )
            }
        }
    }
    
    // Badge Operations
    suspend fun getEarnedBadges(): List<Badge> {
        return withContext(Dispatchers.IO) {
            val completedLevels = levelDao.getAllLevelsSync().filter { it.isCompleted }
            completedLevels.map { level ->
                Badge(
                    badgeId = level.levelId,
                    name = level.badgeName,
                    icon = level.badgeIcon,
                    description = "Completed ${level.title}",
                    levelRequired = level.levelId,
                    isEarned = true,
                    earnedDate = System.currentTimeMillis()
                )
            }
        }
    }
    
    // Progress Calculation
    suspend fun calculateScore(levelId: Int, correctAnswers: Int, totalQuestions: Int): Int {
        return (correctAnswers * 100) / totalQuestions
    }
    
    suspend fun hasPassedLevel(score: Int): Boolean {
        return score >= Constants.PASS_THRESHOLD
    }
    
    suspend fun isPerfectScore(score: Int): Boolean {
        return score == Constants.PERFECT_SCORE
    }
}
