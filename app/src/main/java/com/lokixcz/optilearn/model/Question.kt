package com.lokixcz.optilearn.model

data class Question(
    val questionId: Int = 0,
    val levelId: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // A, B, C, or D
    val explanation: String = ""
) {
    fun getOptions(): List<String> {
        return listOf(optionA, optionB, optionC, optionD)
    }
    
    fun getOptionByLetter(letter: String): String {
        return when (letter.uppercase()) {
            "A" -> optionA
            "B" -> optionB
            "C" -> optionC
            "D" -> optionD
            else -> ""
        }
    }
    
    fun isCorrect(answer: String): Boolean {
        return answer.uppercase() == correctAnswer.uppercase()
    }
}
