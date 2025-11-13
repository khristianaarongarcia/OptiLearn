package com.lokixcz.optilearn.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lokixcz.optilearn.database.entity.UserProgressEntity

@Dao
interface UserProgressDao {
    
    @Query("SELECT * FROM user_progress LIMIT 1")
    fun getUserProgress(): LiveData<UserProgressEntity?>
    
    @Query("SELECT * FROM user_progress LIMIT 1")
    suspend fun getUserProgressSync(): UserProgressEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(userProgress: UserProgressEntity)
    
    @Update
    suspend fun updateUserProgress(userProgress: UserProgressEntity)
    
    @Query("UPDATE user_progress SET currentLevel = :level WHERE id = :userId")
    suspend fun updateCurrentLevel(userId: Int, level: Int)
    
    @Query("UPDATE user_progress SET optiHints = optiHints + :hints WHERE id = :userId")
    suspend fun addOptiHints(userId: Int, hints: Int)
    
    @Query("UPDATE user_progress SET optiHints = optiHints - 1 WHERE id = :userId AND optiHints > 0")
    suspend fun useOptiHint(userId: Int): Int
    
    @Query("UPDATE user_progress SET totalScore = totalScore + :score WHERE id = :userId")
    suspend fun addScore(userId: Int, score: Int)
    
    @Query("UPDATE user_progress SET completedLevels = completedLevels + 1 WHERE id = :userId")
    suspend fun incrementCompletedLevels(userId: Int)
    
    @Query("UPDATE user_progress SET perfectScores = perfectScores + 1 WHERE id = :userId")
    suspend fun incrementPerfectScores(userId: Int)
    
    @Query("DELETE FROM user_progress")
    suspend fun deleteAll()
}
