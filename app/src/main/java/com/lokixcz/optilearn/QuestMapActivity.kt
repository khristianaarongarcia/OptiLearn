package com.lokixcz.optilearn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.utils.Constants
import com.lokixcz.optilearn.utils.DebugLogger
import com.lokixcz.optilearn.view.adapter.LevelAdapter
import com.lokixcz.optilearn.viewmodel.GameViewModel

class QuestMapActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutProgressIndicator: LinearLayout
    private lateinit var tvProgressPercentage: TextView
    private lateinit var tvLevelsCompleted: TextView
    private lateinit var tvOptiHintsCount: TextView
    private lateinit var tvBadgesEarned: TextView
    private lateinit var recyclerViewLevels: RecyclerView
    private lateinit var progressLoading: ProgressBar
    private lateinit var btnBackToMainMenu: TextView
    
    // Overlay views
    private lateinit var loadingOverlay: FrameLayout
    private lateinit var ivTransitionLogo: ImageView
    private lateinit var tvLoadingText: TextView
    
    private val handler = Handler(Looper.getMainLooper())
    private var isDataLoaded = false
    
    private lateinit var levelAdapter: LevelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_map)
        
        initializeViews()
        setupRecyclerView()
        setupObservers()
        setupToolbar()
        
        // Load data
        viewModel.loadAllLevels()
        viewModel.loadUserProgress()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        layoutProgressIndicator = findViewById(R.id.layoutProgressIndicator)
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage)
        tvLevelsCompleted = findViewById(R.id.tvLevelsCompleted)
        tvOptiHintsCount = findViewById(R.id.tvOptiHintsCount)
        tvBadgesEarned = findViewById(R.id.tvBadgesEarned)
        recyclerViewLevels = findViewById(R.id.recyclerViewLevels)
        progressLoading = findViewById(R.id.progressLoading)
        btnBackToMainMenu = findViewById(R.id.btnBackToMainMenu)
        
        // Overlay views
        loadingOverlay = findViewById(R.id.loadingOverlay)
        ivTransitionLogo = findViewById(R.id.ivTransitionLogo)
        tvLoadingText = findViewById(R.id.tvLoadingText)
        
        // Start overlay animation
        startOverlayAnimation()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Back to Main Menu button
        btnBackToMainMenu.setOnClickListener {
            SoundManager.playButtonClick()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        levelAdapter = LevelAdapter(emptyList()) { level ->
            // Navigate to Quiz Transition Activity
            val intent = Intent(this, QuizTransitionActivity::class.java).apply {
                putExtra(Constants.EXTRA_LEVEL_ID, level.levelId)
                putExtra(Constants.EXTRA_LEVEL_TITLE, level.title)
                putExtra(Constants.EXTRA_BADGE_NAME, level.badgeName)
                putExtra(Constants.EXTRA_BADGE_ICON, level.badgeIcon)
            }
            startActivity(intent)
            // Smooth fade transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        
        recyclerViewLevels.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@QuestMapActivity,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
            adapter = levelAdapter
        }
    }

    private fun setupObservers() {
        // Observe levels
        viewModel.levels.observe(this) { levels ->
            levels?.let {
                DebugLogger.info("═════════════════════════════════════════════")
                DebugLogger.info("Quest Map: Levels Loaded")
                DebugLogger.info("Total levels: ${it.size}")
                
                // Log each level's status
                it.forEachIndexed { index, level ->
                    DebugLogger.debug("Level ${level.levelId}: ${level.title}")
                    DebugLogger.debug("  - Completed: ${level.isCompleted}")
                    DebugLogger.debug("  - Unlocked: ${level.isUnlocked}")
                    DebugLogger.debug("  - Position in list: $index")
                }
                
                levelAdapter.updateLevels(it)
                
                // Update stats
                val completedLevels = it.count { level -> level.isCompleted }
                val totalLevels = it.size
                val badgesEarned = it.count { level -> level.isCompleted }
                
                DebugLogger.info("Stats: $completedLevels/$totalLevels completed, $badgesEarned badges")
                DebugLogger.info("═════════════════════════════════════════════")
                
                tvLevelsCompleted.text = "$completedLevels/$totalLevels"
                tvBadgesEarned.text = "🏆 $badgesEarned"
                
                // Scroll to the bottom (Level 1) after levels are loaded
                recyclerViewLevels.post {
                    recyclerViewLevels.scrollToPosition(it.size - 1)
                    DebugLogger.debug("Scrolled to position ${it.size - 1} (Level 1)")
                    
                    // Mark data as loaded
                    isDataLoaded = true
                    DebugLogger.info("Quest Map data fully loaded - ready to hide overlay")
                }
            }
        }
        
        // Observe user progress
        viewModel.userProgress.observe(this) { progress ->
            progress?.let {
                val percentage = it.getOverallProgress()
                progressBar.progress = percentage
                tvProgressPercentage.text = "$percentage%"
                tvOptiHintsCount.text = "💡 ${it.optiHints}"
                
                // Position the percentage indicator (text + arrow) at the end of the progress bar
                progressBar.post {
                    val progressWidth = progressBar.width
                    val progressPosition = (progressWidth * percentage) / 100f
                    val indicatorWidth = layoutProgressIndicator.width
                    
                    // Center the indicator on the progress position, but keep it within bounds
                    val xPosition = (progressPosition - indicatorWidth / 2f).coerceIn(
                        0f,
                        (progressWidth - indicatorWidth).toFloat()
                    )
                    
                    layoutProgressIndicator.translationX = xPosition
                }
            }
        }
        
        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        SoundManager.startBackgroundMusic(R.raw.music_menu)
        // Reload data when returning from quiz
        viewModel.loadAllLevels()
        viewModel.loadUserProgress()
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Check if we need to show transition overlay
        val showTransition = intent?.getBooleanExtra("SHOW_TRANSITION", false) ?: false
        if (showTransition) {
            DebugLogger.info("Showing transition overlay on return")
            // Reset the overlay and show animation
            loadingOverlay.visibility = View.VISIBLE
            loadingOverlay.alpha = 1f
            isDataLoaded = false
            startOverlayAnimation()
        }
    }

    override fun onPause() {
        super.onPause()
        SoundManager.pauseBackgroundMusic()
    }
    
    // ═══════════════════════════════════════════════════════════════
    // LOADING OVERLAY ANIMATION
    // ═══════════════════════════════════════════════════════════════
    
    private fun startOverlayAnimation() {
        DebugLogger.info("Starting overlay animation sequence")
        
        // Phase 1: Slide in from left to center
        ivTransitionLogo.translationX = -1000f
        ivTransitionLogo.alpha = 1f
        ivTransitionLogo.scaleX = 0.8f
        ivTransitionLogo.scaleY = 0.8f
        
        ivTransitionLogo.animate()
            .translationX(0f)
            .setDuration(500L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Phase 2: Enlarge effect
                enlargeLogo()
            }
            .start()
        
        // Fade in loading text
        tvLoadingText.alpha = 0f
        tvLoadingText.animate()
            .alpha(1f)
            .setDuration(500L)
            .setStartDelay(200L)
            .start()
    }
    
    private fun enlargeLogo() {
        DebugLogger.debug("Overlay: Enlarging logo")
        
        // Enlarge from 0.8 to 1.2 then settle to 1.0
        ivTransitionLogo.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                ivTransitionLogo.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200L)
                    .withEndAction {
                        // Phase 3: Wait for data to load
                        waitForDataThenHideOverlay()
                    }
                    .start()
            }
            .start()
    }
    
    private fun waitForDataThenHideOverlay() {
        DebugLogger.debug("Overlay: Waiting for Quest Map data to load...")
        
        // Check every 100ms if data is loaded
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isDataLoaded) {
                    DebugLogger.info("Overlay: Data loaded! Hiding overlay...")
                    // Add small delay to ensure RecyclerView is fully rendered
                    handler.postDelayed({
                        hideOverlay()
                    }, 300L)
                } else {
                    DebugLogger.debug("Overlay: Still waiting for data...")
                    handler.postDelayed(this, 100L)
                }
            }
        }, 100L)
    }
    
    private fun hideOverlay() {
        DebugLogger.info("Overlay: Animating slide out")
        
        // Fade out loading text
        tvLoadingText.animate()
            .alpha(0f)
            .setDuration(300L)
            .start()
        
        // Slide logo out to the right
        ivTransitionLogo.animate()
            .translationX(1000f)
            .alpha(0f)
            .setDuration(500L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Remove overlay completely
                loadingOverlay.visibility = View.GONE
                DebugLogger.info("Overlay: Hidden - Quest Map fully visible!")
            }
            .start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
