package com.lokixcz.optilearn

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.viewmodel.GameViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var tvCompletedLevels: TextView
    private lateinit var tvOptiHints: TextView
    private lateinit var tvTotalScore: TextView
    private lateinit var btnPlay: MaterialButton
    private lateinit var btnTrophyRoom: MaterialButton
    private lateinit var btnSettings: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        
        // Initialize SoundManager
        SoundManager.init(applicationContext)
        
        initializeViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initializeViews() {
        tvCompletedLevels = findViewById(R.id.tvCompletedLevels)
        tvOptiHints = findViewById(R.id.tvOptiHints)
        tvTotalScore = findViewById(R.id.tvTotalScore)
        btnPlay = findViewById(R.id.btnPlay)
        btnTrophyRoom = findViewById(R.id.btnTrophyRoom)
        btnSettings = findViewById(R.id.btnSettings)
    }

    private fun setupObservers() {
        // Observe user progress
        viewModel.userProgress.observe(this) { progress ->
            progress?.let {
                tvCompletedLevels.text = "${it.completedLevels}/15"
                tvOptiHints.text = it.optiHints.toString()
                tvTotalScore.text = it.totalScore.toString()
            }
        }
    }

    private fun setupClickListeners() {
        // Play button - Navigate to Quest Map
        btnPlay.setOnClickListener {
            SoundManager.playButtonClick()
            val intent = Intent(this, QuestMapActivity::class.java)
            startActivity(intent)
        }

        // Trophy Room button
        btnTrophyRoom.setOnClickListener {
            SoundManager.playButtonClick()
            val intent = Intent(this, TrophyRoomActivity::class.java)
            startActivity(intent)
        }

        // Settings button
        btnSettings.setOnClickListener {
            SoundManager.playButtonClick()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload user progress when returning to main menu
        viewModel.loadUserProgress()
        
        // Start background music (using confetti_animation as placeholder)
        // Note: Replace with actual music_menu.mp3 file when available
        SoundManager.startBackgroundMusic(R.raw.confetti_animation)
    }
    
    override fun onPause() {
        super.onPause()
        // Pause background music when activity is not visible
        SoundManager.pauseBackgroundMusic()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up sound resources if app is closing
        if (isFinishing) {
            SoundManager.stopBackgroundMusic()
        }
    }
}
