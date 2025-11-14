package com.lokixcz.optilearn

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.services.FloatingConsoleService
import com.lokixcz.optilearn.utils.DeveloperPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var btnPlay: ImageButton
    private lateinit var btnTrophyRoom: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var imgLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnPlay = findViewById(R.id.btnPlay)
        btnTrophyRoom = findViewById(R.id.btnTrophyRoom)
        btnSettings = findViewById(R.id.btnSettings)
        imgLogo = findViewById(R.id.imgLogo)
        
        // Add animations to buttons
        setupButtonAnimations(btnPlay)
        setupButtonAnimations(btnTrophyRoom)
        setupButtonAnimations(btnSettings)
        
        // Start logo pulsing animation
        val logoPulse = AnimationUtils.loadAnimation(this, R.anim.logo_pulse)
        imgLogo.startAnimation(logoPulse)
        
        // Animate buttons entrance with staggered delay
        val fadeInUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_up)
        btnPlay.alpha = 0f
        btnTrophyRoom.alpha = 0f
        btnSettings.alpha = 0f
        
        btnPlay.postDelayed({ 
            btnPlay.startAnimation(fadeInUp)
            btnPlay.alpha = 1f
        }, 200)
        
        btnTrophyRoom.postDelayed({ 
            btnTrophyRoom.startAnimation(fadeInUp)
            btnTrophyRoom.alpha = 1f
        }, 400)
        
        btnSettings.postDelayed({ 
            btnSettings.startAnimation(fadeInUp)
            btnSettings.alpha = 1f
        }, 600)
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

    /**
     * Setup button animations for hover and click effects
     */
    private fun setupButtonAnimations(button: android.view.View) {
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

    override fun onResume() {
        super.onResume()
        SoundManager.startBackgroundMusic(R.raw.music_menu)
        
        // Auto-launch developer console if enabled
        if (DeveloperPreferences.isDeveloperModeEnabled(this)) {
            launchConsoleIfPermitted()
        }
    }
    
    /**
     * Launch console if overlay permission is granted
     */
    private fun launchConsoleIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                FloatingConsoleService.start(this)
            }
        } else {
            FloatingConsoleService.start(this)
        }
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
