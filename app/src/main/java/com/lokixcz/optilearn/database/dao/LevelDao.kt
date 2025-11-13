package com.lokixcz.optilearn.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lokixcz.optilearn.database.entity.LevelEntity

@Dao
interface LevelDao {
    
    @Query("SELECT * FROM levels ORDER BY levelId ASC")
    fun getAllLevels(): LiveData<List<LevelEntity>>
    
    @Query("SELECT * FROM levels ORDER BY levelId ASC")
    suspend fun getAllLevelsSync(): List<LevelEntity>
    
    @Query("SELECT * FROM levels WHERE levelId = :levelId")
    suspend fun getLevelById(levelId: Int): LevelEntity?
    
    @Query("SELECT * FROM levels WHERE isUnlocked = 1 ORDER BY levelId ASC")
    fun getUnlockedLevels(): LiveData<List<LevelEntity>>
    
    @Query("SELECT * FROM levels WHERE isCompleted = 1 ORDER BY levelId ASC")
    fun getCompletedLevels(): LiveData<List<LevelEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevel(level: LevelEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLevels(levels: List<LevelEntity>)
    
    @Update
    suspend fun updateLevel(level: LevelEntity)
    
    @Query("UPDATE levels SET isUnlocked = 1 WHERE levelId = :levelId")
    suspend fun unlockLevel(levelId: Int)
    
    @Query("UPDATE levels SET isCompleted = 1 WHERE levelId = :levelId")
    suspend fun completeLevel(levelId: Int)
    
    @Query("UPDATE levels SET highScore = :score WHERE levelId = :levelId AND highScore < :score")
    suspend fun updateHighScore(levelId: Int, score: Int)
    
    @Query("UPDATE levels SET attempts = attempts + 1 WHERE levelId = :levelId")
    suspend fun incrementAttempts(levelId: Int)
    
    @Query("DELETE FROM levels")
    suspend fun deleteAll()
}
