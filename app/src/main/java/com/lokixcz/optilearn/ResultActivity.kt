package com.lokixcz.optilearn

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.managers.AnimationManager
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.utils.Constants

class ResultActivity : AppCompatActivity() {

    private lateinit var tvResultTitle: TextView
    private lateinit var tvBadgeIcon: TextView
    private lateinit var tvBadgeName: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvCorrectAnswers: TextView
    private lateinit var tvPerfectMessage: TextView
    private lateinit var btnNextLevel: MaterialButton
    private lateinit var btnRetry: MaterialButton
    private lateinit var btnBackToMap: MaterialButton
    private lateinit var cardScore: CardView
    
    // Lottie animations
    private lateinit var lottieCharacterCelebrate: LottieAnimationView
    private lateinit var lottieBadgeUnlock: LottieAnimationView
    private lateinit var lottieCoinCollect: LottieAnimationView
    private lateinit var lottieLevelUp: LottieAnimationView
    
    private var levelId: Int = 1
    private var levelTitle: String = ""
    private var badgeName: String = ""
    private var badgeIcon: String = ""
    private var score: Int = 0
    private var correctAnswers: Int = 0
    private var totalQuestions: Int = 0
    private var isPerfect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        
        // Get intent extras
        levelId = intent.getIntExtra(Constants.EXTRA_LEVEL_ID, 1)
        levelTitle = intent.getStringExtra(Constants.EXTRA_LEVEL_TITLE) ?: ""
        badgeName = intent.getStringExtra(Constants.EXTRA_BADGE_NAME) ?: ""
        badgeIcon = intent.getStringExtra(Constants.EXTRA_BADGE_ICON) ?: ""
        score = intent.getIntExtra(Constants.EXTRA_SCORE, 0)
        correctAnswers = intent.getIntExtra(Constants.EXTRA_CORRECT_ANSWERS, 0)
        totalQuestions = intent.getIntExtra(Constants.EXTRA_TOTAL_QUESTIONS, 0)
        isPerfect = intent.getBooleanExtra(Constants.EXTRA_IS_PERFECT, false)
        
        initializeViews()
        displayResults()
        setupClickListeners()
    }

    private fun initializeViews() {
        tvResultTitle = findViewById(R.id.tvResultTitle)
        tvBadgeIcon = findViewById(R.id.tvBadgeIcon)
        tvBadgeName = findViewById(R.id.tvBadgeName)
        tvScore = findViewById(R.id.tvScore)
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers)
        tvPerfectMessage = findViewById(R.id.tvPerfectMessage)
        btnNextLevel = findViewById(R.id.btnNextLevel)
        btnRetry = findViewById(R.id.btnRetry)
        btnBackToMap = findViewById(R.id.btnBackToMap)
        cardScore = findViewById(R.id.cardScore)
        
        // Initialize Lottie animations
        lottieCharacterCelebrate = findViewById(R.id.lottieCharacterCelebrate)
        lottieBadgeUnlock = findViewById(R.id.lottieBadgeUnlock)
        lottieCoinCollect = findViewById(R.id.lottieCoinCollect)
        lottieLevelUp = findViewById(R.id.lottieLevelUp)
        
        // Initially hide elements for animation
        tvResultTitle.alpha = 0f
        tvBadgeIcon.alpha = 0f
        tvBadgeName.alpha = 0f
        cardScore.alpha = 0f
        btnNextLevel.alpha = 0f
        btnRetry.alpha = 0f
        btnBackToMap.alpha = 0f
    }

    private fun displayResults() {
        // Set badge
        tvBadgeIcon.text = badgeIcon
        tvBadgeName.text = "Badge: $badgeName"
        
        // Set score
        tvScore.text = "$score%"
        tvCorrectAnswers.text = "$correctAnswers / $totalQuestions Correct"
        
        // Determine pass/fail
        val hasPassed = score >= Constants.PASS_THRESHOLD
        if (hasPassed) {
            tvResultTitle.text = getString(R.string.level_passed)
            tvResultTitle.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            tvResultTitle.text = getString(R.string.level_failed)
            tvResultTitle.setTextColor(Color.parseColor("#F44336"))
            btnNextLevel.visibility = View.GONE
        }
        
        // Show perfect score message
        if (isPerfect) {
            tvPerfectMessage.visibility = View.VISIBLE
        }
        
        // Hide next level button if last level (15)
        if (levelId >= 15) {
            btnNextLevel.visibility = View.GONE
        }
        
        // Play animations based on result
        if (hasPassed) {
            // Play celebration sounds
            if (isPerfect) {
                SoundManager.playBadgeUnlock()
            }
            SoundManager.playCoinCollect()
            
            // Play Lottie animations
            playResultAnimations(isPerfect)
        }
        
        // Play entrance animations
        playEntranceAnimations()
        
        // All information is displayed on the screen, no need for dialog
    }
    
    /**
     * Play staggered entrance animations for result screen elements
     */
    private fun playEntranceAnimations() {
        val handler = Handler(Looper.getMainLooper())
        
        // 1. Title slides in from top (0ms delay)
        handler.postDelayed({
            val titleAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
            tvResultTitle.startAnimation(titleAnim)
            tvResultTitle.alpha = 1f
        }, 0)
        
        // 2. Badge pops up with bounce (300ms delay)
        handler.postDelayed({
            val badgeAnim = AnimationUtils.loadAnimation(this, R.anim.badge_popup)
            tvBadgeIcon.startAnimation(badgeAnim)
            tvBadgeIcon.alpha = 1f
        }, 300)
        
        // 3. Badge name fades in (600ms delay)
        handler.postDelayed({
            val nameAnim = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
            tvBadgeName.startAnimation(nameAnim)
            tvBadgeName.alpha = 1f
        }, 600)
        
        // 4. Score card slides up (900ms delay)
        handler.postDelayed({
            val scoreAnim = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
            cardScore.startAnimation(scoreAnim)
            cardScore.alpha = 1f
        }, 900)
        
        // 5. Buttons fade in (1200ms delay)
        handler.postDelayed({
            val buttonAnim = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
            btnNextLevel.startAnimation(buttonAnim)
            btnNextLevel.alpha = 1f
            
            handler.postDelayed({
                btnRetry.startAnimation(buttonAnim)
                btnRetry.alpha = 1f
            }, 100)
            
            handler.postDelayed({
                btnBackToMap.startAnimation(buttonAnim)
                btnBackToMap.alpha = 1f
            }, 200)
        }, 1200)
    }
    
    /**
     * Play result animations based on performance
     */
    private fun playResultAnimations(isPerfect: Boolean) {
        val handler = Handler(Looper.getMainLooper())
        
        // 1. Show character celebration (0ms)
        handler.postDelayed({
            AnimationManager.playOnce(lottieCharacterCelebrate, hideOnComplete = true)
        }, 0)
        
        // 2. Show coin collect animation (500ms)
        handler.postDelayed({
            AnimationManager.playOnce(lottieCoinCollect, hideOnComplete = true)
        }, 500)
        
        // 3. If perfect or unlocking new level, show badge unlock (1000ms)
        if (isPerfect) {
            handler.postDelayed({
                AnimationManager.playOnce(lottieBadgeUnlock, hideOnComplete = true)
            }, 1000)
        }
        
        // 4. If next level unlocked, show level up animation (1500ms)
        if (levelId < 15) {
            handler.postDelayed({
                AnimationManager.playOnce(lottieLevelUp, hideOnComplete = true)
            }, 1500)
        }
    }
    
    private fun setupClickListeners() {
        btnNextLevel.setOnClickListener {
            SoundManager.playButtonClick()
            // Navigate to next level
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(Constants.EXTRA_LEVEL_ID, levelId + 1)
                val nextTitle = Constants.LEVEL_TITLES[levelId + 1] ?: "Level ${levelId + 1}"
                val nextBadge = Constants.BADGE_NAMES[levelId + 1] ?: ""
                val nextIcon = Constants.BADGE_ICONS[levelId + 1] ?: ""
                putExtra(Constants.EXTRA_LEVEL_TITLE, nextTitle)
                putExtra(Constants.EXTRA_BADGE_NAME, nextBadge)
                putExtra(Constants.EXTRA_BADGE_ICON, nextIcon)
            }
            startActivity(intent)
            finish()
        }
        
        btnRetry.setOnClickListener {
            SoundManager.playButtonClick()
            // Retry same level
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(Constants.EXTRA_LEVEL_ID, levelId)
                putExtra(Constants.EXTRA_LEVEL_TITLE, levelTitle)
                putExtra(Constants.EXTRA_BADGE_NAME, badgeName)
                putExtra(Constants.EXTRA_BADGE_ICON, badgeIcon)
            }
            startActivity(intent)
            finish()
        }
        
        btnBackToMap.setOnClickListener {
            SoundManager.playButtonClick()
            // Navigate back to quest map
            val intent = Intent(this, QuestMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent going back to quiz
        val intent = Intent(this, QuestMapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}
