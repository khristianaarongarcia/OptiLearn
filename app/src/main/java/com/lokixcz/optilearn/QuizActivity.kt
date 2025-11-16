package com.lokixcz.optilearn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.managers.AnimationManager
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.model.Question
import com.lokixcz.optilearn.utils.Constants
import com.lokixcz.optilearn.utils.DebugLogger
import com.lokixcz.optilearn.viewmodel.GameViewModel

class QuizActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var btnMenu: AppCompatImageButton
    private lateinit var tvHintCount: TextView
    private lateinit var tvQuestionCounter: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvTimeBonus: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var btnOptionA: MaterialButton
    private lateinit var btnOptionB: MaterialButton
    private lateinit var btnOptionC: MaterialButton
    private lateinit var btnOptionD: MaterialButton
    private lateinit var btnUseOptiHint: AppCompatImageButton
    private lateinit var tvNoHintsLeft: TextView
    
    // New animation views
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var lottieCorrect: LottieAnimationView
    private lateinit var lottieWrong: LottieAnimationView
    private lateinit var lottieStreak: LottieAnimationView
    private lateinit var layoutStreak: LinearLayout
    private lateinit var tvStreakCount: TextView
    
    // Timer
    private var quizTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 60000 // 60 seconds
    
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
    private var maxStreak: Int = 0  // Track highest streak achieved in this level

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
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter)
        tvTimer = findViewById(R.id.tvTimer)
        tvTimeBonus = findViewById(R.id.tvTimeBonus)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnOptionA = findViewById(R.id.btnOptionA)
        btnOptionB = findViewById(R.id.btnOptionB)
        btnOptionC = findViewById(R.id.btnOptionC)
        btnOptionD = findViewById(R.id.btnOptionD)
        btnUseOptiHint = findViewById(R.id.btnUseOptiHint)
        tvNoHintsLeft = findViewById(R.id.tvNoHintsLeft)
        
        // Add animations to buttons
        setupButtonAnimations(btnMenu)
        setupButtonAnimations(btnUseOptiHint)
        
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
                    displayQuestion()
                }
            }
        }
        
        // Observe user progress for OptiHints
        viewModel.userProgress.observe(this) { progress ->
            progress?.let {
                tvHintCount.text = "💡 ${it.optiHints}"
                setHintButtonEnabled(it.canUseOptiHint() && !hasAnswered)
                
                // Show/hide "No hints left" indicator
                if (it.optiHints == 0) {
                    tvNoHintsLeft.visibility = View.VISIBLE
                } else {
                    tvNoHintsLeft.visibility = View.GONE
                }
            }
        }
    }

    private fun setupClickListeners() {
        val optionButtons = listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD)
        val optionLetters = listOf("A", "B", "C", "D")
        
        optionButtons.forEachIndexed { index, button ->
            // Add touch animation
            button.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        val pressAnim = AnimationUtils.loadAnimation(this, R.anim.button_press)
                        view.startAnimation(pressAnim)
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        val releaseAnim = AnimationUtils.loadAnimation(this, R.anim.button_release)
                        view.startAnimation(releaseAnim)
                    }
                }
                false // Return false to allow click listener to work
            }
            
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
        
        // Update question counter
        updateQuestionCounter()
        
        // Display question and options
        tvQuestion.text = question.questionText
        btnOptionA.text = "A. ${question.optionA}"
        btnOptionB.text = "B. ${question.optionB}"
        btnOptionC.text = "C. ${question.optionC}"
        btnOptionD.text = "D. ${question.optionD}"
        
        // Reset UI
        resetButtonStates()
        setHintButtonEnabled(true)
        hasAnswered = false
        selectedAnswer = null
        
        // Start timer for first question
        if (currentQuestionIndex == 0) {
            timeLeftInMillis = 60000 // Reset to 60 seconds
            startTimer()
        }
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
            
            // Track maximum streak achieved
            if (correctStreak > maxStreak) {
                maxStreak = correctStreak
            }
            
            // Add bonus time (30-45 seconds randomly)
            val bonusSeconds = (30..45).random()
            addBonusTime(bonusSeconds)
            
            // Play correct sound
            SoundManager.playCorrectSound()
            
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
            
            // Play wrong sound
            SoundManager.playWrongSound()
            
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
        // Pause timer while showing feedback
        quizTimer?.cancel()
        
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
            tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.correct_answer))
            lottieFeedback.setAnimation(R.raw.correct_answer)
        } else {
            tvFeedbackTitle.text = "✗ Wrong!"
            tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.wrong_answer))
            lottieFeedback.setAnimation(R.raw.wrong_answer)
        }
        
        lottieFeedback.playAnimation()
        tvExplanationText.text = explanation
        
        // Continue button click
        btnContinue.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            
            // Resume timer after dialog closes
            startTimer()
            
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
        setHintButtonEnabled(false)
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
        
        // Award bonus hints based on max streak achieved
        val bonusHints = when {
            maxStreak >= 10 -> 5  // 10+ streak = 5 bonus hints
            maxStreak >= 7 -> 3   // 7-9 streak = 3 bonus hints
            maxStreak >= 5 -> 2   // 5-6 streak = 2 bonus hints
            maxStreak >= 3 -> 1   // 3-4 streak = 1 bonus hint
            else -> 0             // < 3 streak = no bonus
        }
        
        if (bonusHints > 0) {
            viewModel.addOptiHints(bonusHints)
            DebugLogger.info("Streak Bonus: Awarded $bonusHints OptiHints for $maxStreak max streak!")
        }
        
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
            putExtra("EXTRA_MAX_STREAK", maxStreak)
            putExtra("EXTRA_BONUS_HINTS", bonusHints)
        }
        startActivity(intent)
        finish()
    }
    
    /**
     * Show streak animation in center with fade in/out effect
     * Applies pixelation effect for retro look
     */
    private fun showStreakAnimation(streak: Int) {
        // Update streak text
        tvStreakCount.text = "🔥 $streak Streak!"
        
        // Apply strong pixelation effect - render at very low resolution then scale up
        val paint = Paint()
        paint.isAntiAlias = false
        paint.isFilterBitmap = false
        paint.isDither = false
        
        // Force software rendering for pixelation
        lottieStreak.setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
        
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
        // Pause timer while menu is open
        quizTimer?.cancel()
        
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pause_menu)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Initialize dialog views
        val btnClosePause = dialog.findViewById<ImageView>(R.id.btnClosePause)
    val btnResume = dialog.findViewById<AppCompatImageButton>(R.id.btnResume)
    val btnRestart = dialog.findViewById<AppCompatImageButton>(R.id.btnRestart)
    val btnExitQuiz = dialog.findViewById<AppCompatImageButton>(R.id.btnExitQuiz)
        
        // Add hover and click effects to buttons
        setupButtonAnimations(btnResume)
        setupButtonAnimations(btnRestart)
        setupButtonAnimations(btnExitQuiz)
        
        // Close button - dismiss dialog
        btnClosePause.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            // Resume timer when dialog closes
            startTimer()
        }
        
        // Resume button - dismiss dialog and continue
        btnResume.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            // Resume timer when resuming
            startTimer()
        }
        
        // Restart button - restart level
        btnRestart.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            // Restart by launching QuizTransitionActivity again
            val intent = Intent(this@QuizActivity, QuizTransitionActivity::class.java).apply {
                putExtra(Constants.EXTRA_LEVEL_ID, levelId)
                putExtra(Constants.EXTRA_LEVEL_TITLE, levelTitle)
                putExtra(Constants.EXTRA_BADGE_NAME, badgeName)
                putExtra(Constants.EXTRA_BADGE_ICON, badgeIcon)
            }
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        
        // Exit button - go back to quest map
        btnExitQuiz.setOnClickListener {
            SoundManager.playButtonClick()
            dialog.dismiss()
            // Navigate back to Quest Map with transition flag
            val intent = Intent(this@QuizActivity, QuestMapActivity::class.java)
            intent.putExtra("SHOW_TRANSITION", true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        
        // Resume timer if dialog is dismissed by tapping outside
        dialog.setOnDismissListener {
            if (!isFinishing) {
                startTimer()
            }
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
     * Setup button animations for hover and click effects
     */
    private fun setupButtonAnimations(button: View) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    // Click effect - scale down
                    view.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .alpha(0.8f)
                        .setDuration(100)
                        .start()
                }
                android.view.MotionEvent.ACTION_UP, 
                android.view.MotionEvent.ACTION_CANCEL -> {
                    // Release effect - scale back with overshoot
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .setDuration(150)
                        .setInterpolator(android.view.animation.OvershootInterpolator())
                        .start()
                }
            }
            false // Return false to allow click listener to work
        }
        
        // Hover effect - enlarge slightly when focused
        button.setOnHoverListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_HOVER_ENTER -> {
                    view.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(150)
                        .setInterpolator(android.view.animation.OvershootInterpolator())
                        .start()
                }
                android.view.MotionEvent.ACTION_HOVER_EXIT -> {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .start()
                }
            }
            false
        }
    }

    /**
     * Start countdown timer
     */
    private fun startTimer() {
        quizTimer?.cancel() // Cancel any existing timer
        
        quizTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                // Time's up - auto submit quiz
                timeLeftInMillis = 0
                updateTimerDisplay()
                handleTimeOut()
            }
        }.start()
    }

    /**
     * Update timer display
     */
    private fun updateTimerDisplay() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        
        tvTimer.text = String.format("⏱ %02d:%02d", minutes, secs)
        
        // Change color based on time left
        when {
            seconds <= 10 -> tvTimer.setTextColor(ContextCompat.getColor(this, R.color.wrong_answer)) // Red
            seconds <= 20 -> tvTimer.setTextColor(ContextCompat.getColor(this, R.color.warning)) // Yellow
            else -> tvTimer.setTextColor(ContextCompat.getColor(this, R.color.correct_answer)) // Teal
        }
    }

    /**
     * Add bonus time for correct answer
     */
    private fun addBonusTime(seconds: Int) {
        timeLeftInMillis += seconds * 1000L
        showTimeBonusPopup(seconds)
        
        // Restart timer with new time
        startTimer()
    }

    /**
     * Show time bonus popup animation
     */
    private fun showTimeBonusPopup(seconds: Int) {
        tvTimeBonus.text = "+$seconds"
        tvTimeBonus.visibility = View.VISIBLE
        
        // Animate popup
        tvTimeBonus.alpha = 0f
        tvTimeBonus.translationY = 0f
        tvTimeBonus.animate()
            .alpha(1f)
            .translationY(-30f)
            .setDuration(250)
            .withEndAction {
                // Fade out after 800ms
                tvTimeBonus.animate()
                    .alpha(0f)
                    .setStartDelay(800)
                    .setDuration(250)
                    .withEndAction {
                        tvTimeBonus.visibility = View.GONE
                        tvTimeBonus.translationY = 0f
                    }
                    .start()
            }
            .start()
    }

    /**
     * Handle quiz timeout
     */
    private fun handleTimeOut() {
        if (hasAnswered) return
        
        hasAnswered = true
        disableAllOptions()
        
        // Show custom game over dialog
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_game_over)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        
        val btnContinue = dialog.findViewById<AppCompatImageButton>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            finishQuiz()
        }
        
        dialog.show()
    }

    /**
     * Update question counter display
     */
    private fun updateQuestionCounter() {
        tvQuestionCounter.text = "${currentQuestionIndex + 1}/${questions.size}"
    }

    override fun onResume() {
        super.onResume()
        SoundManager.startBackgroundMusic(R.raw.music_quiz)
    }

    override fun onPause() {
        super.onPause()
        SoundManager.pauseBackgroundMusic()
        quizTimer?.cancel()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        quizTimer?.cancel()
    }
    
    /**
     * Play celebration animation when answer is correct
     * Now uses correct_answer animation
     */
    private fun playCelebrationAnimation() {
        // Already handled in selectAnswer() using AnimationManager
        // This method is kept for compatibility but can be removed
    }

    private fun setHintButtonEnabled(enabled: Boolean) {
        btnUseOptiHint.isEnabled = enabled
        btnUseOptiHint.alpha = if (enabled) 1f else 0.5f
    }

    override fun onBackPressed() {
        // Show pause menu instead of directly exiting
        // This provides consistent UX whether using hardware back or menu button
        showPauseMenu()
    }

}
