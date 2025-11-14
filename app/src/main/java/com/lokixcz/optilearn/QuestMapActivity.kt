package com.lokixcz.optilearn

import android.content.Intent
import android.os.Bundle
import android.view.View
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
            // Navigate to Quiz Activity
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(Constants.EXTRA_LEVEL_ID, level.levelId)
                putExtra(Constants.EXTRA_LEVEL_TITLE, level.title)
                putExtra(Constants.EXTRA_BADGE_NAME, level.badgeName)
                putExtra(Constants.EXTRA_BADGE_ICON, level.badgeIcon)
            }
            startActivity(intent)
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

    override fun onPause() {
        super.onPause()
        SoundManager.pauseBackgroundMusic()
    }
}
