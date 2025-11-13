package com.lokixcz.optilearn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.managers.AnimationManager
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.model.Question
import com.lokixcz.optilearn.utils.Constants
import com.lokixcz.optilearn.viewmodel.GameViewModel

class QuizActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var btnMenu: ImageView
    private lateinit var tvHintCount: TextView
    private lateinit var tvQuestionProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvQuestion: TextView
    private lateinit var btnOptionA: MaterialButton
    private lateinit var btnOptionB: MaterialButton
    private lateinit var btnOptionC: MaterialButton
    private lateinit var btnOptionD: MaterialButton
    private lateinit var btnUseOptiHint: MaterialButton
    
    // New animation views
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var lottieCorrect: LottieAnimationView
    private lateinit var lottieWrong: LottieAnimationView
    private lateinit var lottieStreak: LottieAnimationView
    private lateinit var layoutStreak: LinearLayout
    private lateinit var tvStreakCount: TextView
    
    private var levelId: Int = 1
    private var levelTitle: String = ""
    private var badgeName: String = ""
    private var badgeIcon: String = ""
    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex: Int = 0
    private var correctAnswers: Int = 0
    private var selectedAnswer: String? = null
    private var hasAnswered: Boolean = false
    private var correctStreak: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        
        // Get intent extras
        levelId = intent.getIntExtra(Constants.EXTRA_LEVEL_ID, 1)
        levelTitle = intent.getStringExtra(Constants.EXTRA_LEVEL_TITLE) ?: "Level $levelId"
        badgeName = intent.getStringExtra(Constants.EXTRA_BADGE_NAME) ?: ""
        badgeIcon = intent.getStringExtra(Constants.EXTRA_BADGE_ICON) ?: ""
        
        initializeViews()
        setupToolbar()
        setupClickListeners()
        setupObservers()
        
        // Load questions for this level
        viewModel.loadQuestionsForLevel(levelId)
        viewModel.loadUserProgress()
    }

    private fun initializeViews() {
        btnMenu = findViewById(R.id.btnMenu)
        tvHintCount = findViewById(R.id.tvHintCount)
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        progressBar = findViewById(R.id.progressBar)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnOptionA = findViewById(R.id.btnOptionA)
        btnOptionB = findViewById(R.id.btnOptionB)
        btnOptionC = findViewById(R.id.btnOptionC)
        btnOptionD = findViewById(R.id.btnOptionD)
        btnUseOptiHint = findViewById(R.id.btnUseOptiHint)
        
        // Initialize animation views
        lottieLoading = findViewById(R.id.lottieLoading)
        lottieCorrect = findViewById(R.id.lottieCorrect)
        lottieWrong = findViewById(R.id.lottieWrong)
        lottieStreak = findViewById(R.id.lottieStreak)
        layoutStreak = findViewById(R.id.layoutStreak)
        tvStreakCount = findViewById(R.id.tvStreakCount)
    }

    private fun setupToolbar() {
        // Set title manually
        val tvToolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        tvToolbarTitle.text = levelTitle
        
        // Menu button click listener
        btnMenu.setOnClickListener {
            SoundManager.playButtonClick()
            android.util.Log.d("QuizActivity", "Menu button clicked!")
            showPauseMenu()
        }
    }

    private fun setupObservers() {
        // Observe questions
        viewModel.questions.observe(this) { questionList ->
            questionList?.let {
                if (it.isNotEmpty()) {
                    questions = it
                    progressBar.max = questions.size
                    displayQuestion()
                }
            }
        }
        
        // Observe user progress for OptiHints
        viewModel.userProgress.observe(this) { progress ->
            progress?.let {
                tvHintCount.text = "💡 ${it.optiHints}"
                btnUseOptiHint.isEnabled = it.canUseOptiHint() && !hasAnswered
            }
        }
    }

    private fun setupClickListeners() {
        val optionButtons = listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD)
        val optionLetters = listOf("A", "B", "C", "D")
        
        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (!hasAnswered) {
                    SoundManager.playButtonClick()
                    selectAnswer(optionLetters[index], button)
                }
            }
        }
        
        btnUseOptiHint.setOnClickListener {
            SoundManager.playButtonClick()
            useOptiHint()
        }
    }

    private fun displayQuestion() {
        if (currentQuestionIndex >= questions.size) {
            finishQuiz()
            return
        }
        
        val question = questions[currentQuestionIndex]
        
        // Update progress
        tvQuestionProgress.text = "Question ${currentQuestionIndex + 1} of ${questions.size}"
        progressBar.progress = currentQuestionIndex + 1
        
        // Display question and options
        tvQuestion.text = question.questionText
        btnOptionA.text = "A. ${question.optionA}"
        btnOptionB.text = "B. ${question.optionB}"
        btnOptionC.text = "C. ${question.optionC}"
        btnOptionD.text = "D. ${question.optionD}"
        
        // Reset UI
        resetButtonStates()
        btnUseOptiHint.isEnabled = true
        hasAnswered = false
        selectedAnswer = null
    }

    private fun selectAnswer(answer: String, button: MaterialButton) {
        if (hasAnswered) return
        
        selectedAnswer = answer
        hasAnswered = true
        
        val question = questions[currentQuestionIndex]
        val isCorrect = question.isCorrect(answer)
        
        // Highlight selected answer
        if (isCorrect) {
            button.setBackgroundResource(R.drawable.btn_quiz_option_correct)
            button.setTextColor(Color.WHITE)
            correctAnswers++
            correctStreak++
            
            // Play correct sound and animation
            SoundManager.playCorrectSound()
            AnimationManager.playFeedback(lottieCorrect, true)
            
            // Play streak animation if 3+ correct in a row
            if (correctStreak >= 3) {
                SoundManager.playStreakSound()
                showStreakAnimation(correctStreak)
            }
            
            // Show feedback dialog
            showFeedbackDialog(true, question.explanation)
        } else {
            button.setBackgroundResource(R.drawable.btn_quiz_option_wrong)
            button.setTextColor(Color.WHITE)
            correctStreak = 0  // Reset streak
            
            // Play wrong sound and animation
            SoundManager.playWrongSound()
            AnimationManager.playFeedback(lottieWrong, false)
            
            // Highlight correct answer
            highlightCorrectAnswer(question.correctAnswer)
            
            // Show feedback dialog
            showFeedbackDialog(false, question.explanation)
        }
        
        // Disable all option buttons
        disableAllOptions()
    }
    
    /**
     * Show feedback dialog with explanation and continue button
     */
    private fun showFeedbackDialog(isCorrect: Boolean, explanation: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_feedback)
        dialog.setCancelable(false)
        
        // Make dialog background transparent for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Initialize dialog views
        val lottieFeedback = dialog.findViewById<LottieAnimationView>(R.id.lottieFeedback)
        val tvFeedbackTitle = dialog.findViewById<TextView>(R.id.tvFeedbackTitle)
        val tvExplanationText = dialog.findViewById<TextView>(R.id.tvExplanationText)
        val btnContinue = dialog.findViewById<MaterialButton>(R.id.btnContinue)
        
        // Set feedback content based on correctness
        if (isCorrect) {
            tvFeedbackTitle.text = "✓ Correct!"
            tvFeedbackTitle.setTextColor(Color.parseColor("#00B894"))
            lottieFeedback.setAnimation(R.raw.correct_answer)
        } else {
            tvFeedbackTitle.text = "✗ Wrong!"
            tvFeedbackTitle.setTextColor(Color.parseColor("#D63031"))
            lottieFeedback.setAnimation(R.raw.wrong_answer)
        }
        
        lottieFeedback.playAnimation()
        tvExplanationText.text = explanation
        
        // Continue button click
        btnContinue.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            moveToNextQuestion()
        }
        
        // Show dialog with scale animation
        dialog.show()
        
        // Animate dialog entrance
        dialog.window?.decorView?.apply {
            scaleX = 0.8f
            scaleY = 0.8f
            alpha = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    private fun highlightCorrectAnswer(correctAnswer: String) {
        val correctButton = when (correctAnswer.uppercase()) {
            "A" -> btnOptionA
            "B" -> btnOptionB
            "C" -> btnOptionC
            "D" -> btnOptionD
            else -> return
        }
        correctButton.setBackgroundResource(R.drawable.btn_quiz_option_correct)
        correctButton.setTextColor(Color.WHITE)
    }

    private fun resetButtonStates() {
        val buttons = listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD)
        buttons.forEach { button ->
            button.isEnabled = true
            button.setBackgroundResource(R.drawable.btn_quiz_option)
            button.setTextColor(Color.WHITE)
        }
    }

    private fun disableAllOptions() {
        listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD).forEach {
            it.isEnabled = false
        }
        btnUseOptiHint.isEnabled = false
    }

    private fun useOptiHint() {
        if (hasAnswered) return
        
        val progress = viewModel.userProgress.value
        if (progress != null && progress.canUseOptiHint()) {
            // Play hint sound
            SoundManager.playHintUse()
            
            // Use the hint
            viewModel.useOptiHint()
            
            // Reveal correct answer
            val question = questions[currentQuestionIndex]
            highlightCorrectAnswer(question.correctAnswer)
            
            // Auto-select correct answer
            selectAnswer(question.correctAnswer, getCorrectButton(question.correctAnswer))
        }
    }

    private fun getCorrectButton(answer: String): MaterialButton {
        return when (answer.uppercase()) {
            "A" -> btnOptionA
            "B" -> btnOptionB
            "C" -> btnOptionC
            "D" -> btnOptionD
            else -> btnOptionA
        }
    }

    private fun moveToNextQuestion() {
        currentQuestionIndex++
        displayQuestion()
    }

    private fun finishQuiz() {
        val score = viewModel.calculateScore(correctAnswers, questions.size)
        val isPerfect = viewModel.isPerfectScore(score)
        val hasPassed = viewModel.hasPassedLevel(score)
        
        // Play level complete sound
        SoundManager.playLevelComplete()
        
        // Save progress
        viewModel.completeLevel(levelId, score, isPerfect)
        
        // Navigate to result activity
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(Constants.EXTRA_LEVEL_ID, levelId)
            putExtra(Constants.EXTRA_LEVEL_TITLE, levelTitle)
            putExtra(Constants.EXTRA_BADGE_NAME, badgeName)
            putExtra(Constants.EXTRA_BADGE_ICON, badgeIcon)
            putExtra(Constants.EXTRA_SCORE, score)
            putExtra(Constants.EXTRA_CORRECT_ANSWERS, correctAnswers)
            putExtra(Constants.EXTRA_TOTAL_QUESTIONS, questions.size)
            putExtra(Constants.EXTRA_IS_PERFECT, isPerfect)
        }
        startActivity(intent)
        finish()
    }
    
    /**
     * Show streak animation in center with fade in/out effect
     */
    private fun showStreakAnimation(streak: Int) {
        // Update streak text
        tvStreakCount.text = "🔥 $streak Streak!"
        
        // Start Lottie animation
        lottieStreak.playAnimation()
        
        // Fade in animation
        layoutStreak.visibility = View.VISIBLE
        layoutStreak.animate()
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                // Keep visible for 2 seconds, then fade out
                layoutStreak.postDelayed({
                    layoutStreak.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction {
                            layoutStreak.visibility = View.GONE
                            lottieStreak.cancelAnimation()
                        }
                        .start()
                }, 2000)
            }
            .start()
    }
    
    /**
     * Show pause menu dialog
     */
    private fun showPauseMenu() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pause_menu)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Initialize dialog views
        val btnClosePause = dialog.findViewById<ImageView>(R.id.btnClosePause)
        val btnResume = dialog.findViewById<MaterialButton>(R.id.btnResume)
        val btnRestart = dialog.findViewById<MaterialButton>(R.id.btnRestart)
        val btnExitQuiz = dialog.findViewById<MaterialButton>(R.id.btnExitQuiz)
        
        // Close button - dismiss dialog
        btnClosePause.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
        }
        
        // Resume button - dismiss dialog and continue
        btnResume.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
        }
        
        // Restart button - restart level
        btnRestart.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            // Restart by recreating activity
            recreate()
        }
        
        // Exit button - go back to quest map
        btnExitQuiz.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            finish()
        }
        
        // Show dialog with fade animation
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.decorView?.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }
    }
    
    /**
     * Play celebration animation when answer is correct
     * Now uses correct_answer animation
     */
    private fun playCelebrationAnimation() {
        // Already handled in selectAnswer() using AnimationManager
        // This method is kept for compatibility but can be removed
    }
}
