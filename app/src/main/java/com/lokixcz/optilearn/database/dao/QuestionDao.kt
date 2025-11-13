package com.lokixcz.optilearn.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lokixcz.optilearn.database.entity.QuestionEntity

@Dao
interface QuestionDao {
    
    @Query("SELECT * FROM questions WHERE levelId = :levelId")
    suspend fun getQuestionsByLevel(levelId: Int): List<QuestionEntity>
    
    @Query("SELECT * FROM questions WHERE levelId = :levelId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(levelId: Int, limit: Int): List<QuestionEntity>
    
    @Query("SELECT * FROM questions WHERE questionId = :questionId")
    suspend fun getQuestionById(questionId: Int): QuestionEntity?
    
    @Query("SELECT COUNT(*) FROM questions WHERE levelId = :levelId")
    suspend fun getQuestionCountByLevel(levelId: Int): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<QuestionEntity>)
    
    @Update
    suspend fun updateQuestion(question: QuestionEntity)
    
    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)
    
    @Query("DELETE FROM questions WHERE levelId = :levelId")
    suspend fun deleteQuestionsByLevel(levelId: Int)
    
    @Query("DELETE FROM questions")
    suspend fun deleteAll()
}
