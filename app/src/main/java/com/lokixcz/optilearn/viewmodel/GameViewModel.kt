package com.lokixcz.optilearn.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lokixcz.optilearn.model.Level
import com.lokixcz.optilearn.model.Question
import com.lokixcz.optilearn.model.UserProgress
import com.lokixcz.optilearn.model.Badge
import com.lokixcz.optilearn.utils.DatabaseHelper
import com.lokixcz.optilearn.utils.PrefManager
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val databaseHelper = DatabaseHelper(application)
    private val prefManager = PrefManager(application)
    
    // LiveData for UI
    private val _userProgress = MutableLiveData<UserProgress>()
    val userProgress: LiveData<UserProgress> = _userProgress
    
    private val _levels = MutableLiveData<List<Level>>()
    val levels: LiveData<List<Level>> = _levels
    
    private val _currentLevel = MutableLiveData<Level>()
    val currentLevel: LiveData<Level> = _currentLevel
    
    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions
    
    private val _earnedBadges = MutableLiveData<List<Badge>>()
    val earnedBadges: LiveData<List<Badge>> = _earnedBadges
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    init {
        initializeApp()
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            try {
                _loading.value = true
                
                // Initialize user progress if first launch
                if (prefManager.isFirstLaunch()) {
                    databaseHelper.initializeUserProgress()
                    prefManager.setFirstLaunch(false)
                }
                
                // Load initial data
                loadUserProgress()
                loadAllLevels()
                loadEarnedBadges()
                
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to initialize app: ${e.message}"
                _loading.value = false
            }
        }
    }
    
    // User Progress Operations
    fun loadUserProgress() {
        viewModelScope.launch {
            try {
                val progress = databaseHelper.getUserProgress()
                _userProgress.value = progress ?: UserProgress()
            } catch (e: Exception) {
                _error.value = "Failed to load user progress: ${e.message}"
            }
        }
    }
    
    fun updateUserProgress(progress: UserProgress) {
        viewModelScope.launch {
            try {
                databaseHelper.updateUserProgress(progress)
                _userProgress.value = progress
            } catch (e: Exception) {
                _error.value = "Failed to update user progress: ${e.message}"
            }
        }
    }
    
    // Level Operations
    fun loadAllLevels() {
        viewModelScope.launch {
            try {
                val levelList = databaseHelper.getAllLevels()
                _levels.value = levelList
            } catch (e: Exception) {
                _error.value = "Failed to load levels: ${e.message}"
            }
        }
    }
    
    fun loadLevel(levelId: Int) {
        viewModelScope.launch {
            try {
                val level = databaseHelper.getLevelById(levelId)
                _currentLevel.value = level
            } catch (e: Exception) {
                _error.value = "Failed to load level: ${e.message}"
            }
        }
    }
    
    fun unlockNextLevel(currentLevelId: Int) {
        viewModelScope.launch {
            try {
                val nextLevelId = currentLevelId + 1
                if (nextLevelId <= 15) {
                    databaseHelper.unlockLevel(nextLevelId)
                    loadAllLevels()
                }
            } catch (e: Exception) {
                _error.value = "Failed to unlock next level: ${e.message}"
            }
        }
    }
    
    fun completeLevel(levelId: Int, score: Int, isPerfect: Boolean) {
        viewModelScope.launch {
            try {
                val userId = prefManager.getUserId()
                
                // Only mark level as completed if score >= 80%
                if (score >= 80) {
                    databaseHelper.completeLevel(levelId)
                    databaseHelper.incrementCompletedLevels(userId)
                    
                    // Unlock next level if passed
                    unlockNextLevel(levelId)
                }
                
                // Always update high score and attempts regardless of pass/fail
                databaseHelper.updateHighScore(levelId, score)
                databaseHelper.incrementAttempts(levelId)
                
                // Update user progress
                databaseHelper.addScore(userId, score)
                
                // Award OptiHint for perfect score
                if (isPerfect) {
                    databaseHelper.addOptiHints(userId, 1)
                    databaseHelper.incrementPerfectScores(userId)
                }
                
                // Reload data
                loadUserProgress()
                loadAllLevels()
                loadEarnedBadges()
            } catch (e: Exception) {
                _error.value = "Failed to complete level: ${e.message}"
            }
        }
    }
    
    // Question Operations
    fun loadQuestionsForLevel(levelId: Int) {
        viewModelScope.launch {
            try {
                // Get the actual number of questions available for this level
                val questionCount = databaseHelper.getQuestionCountByLevel(levelId)
                // Load all available questions for the level
                val questionList = databaseHelper.getRandomQuestions(levelId, questionCount)
                _questions.value = questionList
            } catch (e: Exception) {
                _error.value = "Failed to load questions: ${e.message}"
            }
        }
    }
    
    fun useOptiHint(): Boolean {
        var success = false
        viewModelScope.launch {
            try {
                val userId = prefManager.getUserId()
                success = databaseHelper.useOptiHint(userId)
                if (success) {
                    loadUserProgress()
                }
            } catch (e: Exception) {
                _error.value = "Failed to use OptiHint: ${e.message}"
            }
        }
        return success
    }
    
    // Badge Operations
    fun loadEarnedBadges() {
        viewModelScope.launch {
            try {
                val badges = databaseHelper.getEarnedBadges()
                _earnedBadges.value = badges
            } catch (e: Exception) {
                _error.value = "Failed to load badges: ${e.message}"
            }
        }
    }
    
    // Settings Operations
    fun isSoundEnabled(): Boolean = prefManager.isSoundEnabled()
    fun setSoundEnabled(enabled: Boolean) = prefManager.setSoundEnabled(enabled)
    
    fun isMusicEnabled(): Boolean = prefManager.isMusicEnabled()
    fun setMusicEnabled(enabled: Boolean) = prefManager.setMusicEnabled(enabled)
    
    /**
     * Reset all user progress (levels, scores, badges, OptiHints)
     * This is a destructive operation and should be used carefully
     */
    fun resetAllProgress() {
        viewModelScope.launch {
            try {
                _loading.value = true
                
                // Reset user progress to initial state
                databaseHelper.resetUserProgress()
                
                // Reload all data
                loadUserProgress()
                loadAllLevels()
                loadEarnedBadges()
                
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to reset progress: ${e.message}"
                _loading.value = false
            }
        }
    }
    
    // Utility Methods
    fun calculateScore(correctAnswers: Int, totalQuestions: Int): Int {
        return (correctAnswers * 100) / totalQuestions
    }
    
    fun hasPassedLevel(score: Int): Boolean {
        return score >= 80
    }
    
    fun isPerfectScore(score: Int): Boolean {
        return score == 100
    }
}
