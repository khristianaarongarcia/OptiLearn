package com.lokixcz.optilearn

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.services.FloatingConsoleService
import com.lokixcz.optilearn.utils.DebugLogger
import com.lokixcz.optilearn.utils.DeveloperPreferences
import com.lokixcz.optilearn.viewmodel.GameViewModel

/**
 * Settings Activity
 * Allows users to control sound, music, and reset progress
 */
class SettingsActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var sliderSoundEffects: Slider
    private lateinit var sliderBackgroundMusic: Slider
    private lateinit var tvSoundVolume: TextView
    private lateinit var tvMusicVolume: TextView
    private lateinit var btnResetProgress: MaterialButton
    private lateinit var tvAppVersion: TextView
    private lateinit var btnToggleDeveloperMode: MaterialButton
    
    // Developer mode tap counter
    private var versionTapCount = 0
    private var lastTapTime = 0L
    private val TAP_TIMEOUT = 500L // Reset counter if taps are more than 500ms apart
    private val REQUIRED_TAPS = 10
    
    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        initializeViews()
        setupToolbar()
        loadCurrentSettings()
        setupListeners()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        sliderSoundEffects = findViewById(R.id.sliderSoundEffects)
        sliderBackgroundMusic = findViewById(R.id.sliderBackgroundMusic)
        tvSoundVolume = findViewById(R.id.tvSoundVolume)
        tvMusicVolume = findViewById(R.id.tvMusicVolume)
        btnResetProgress = findViewById(R.id.btnResetProgress)
        tvAppVersion = findViewById(R.id.tvAppVersion)
        btnToggleDeveloperMode = findViewById(R.id.btnToggleDeveloperMode)
        
        // Show toggle button if developer mode has been unlocked
        updateDeveloperModeUI()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadCurrentSettings() {
        // Load current volume settings from SoundManager
        val soundVolume = SoundManager.getSoundVolume()
        val musicVolume = SoundManager.getMusicVolume()
        
        sliderSoundEffects.value = soundVolume.toFloat()
        sliderBackgroundMusic.value = musicVolume.toFloat()
        
        tvSoundVolume.text = "$soundVolume%"
        tvMusicVolume.text = "$musicVolume%"
    }

    private fun setupListeners() {
        // Sound Effects Slider
        sliderSoundEffects.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val volumePercent = value.toInt()
                tvSoundVolume.text = "$volumePercent%"
                SoundManager.setSoundVolume(volumePercent)
                
                // Play a test sound if volume > 0
                if (volumePercent > 0) {
                    SoundManager.playButtonClick()
                }
            }
        }

        // Background Music Slider
        sliderBackgroundMusic.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val volumePercent = value.toInt()
                tvMusicVolume.text = "$volumePercent%"
                SoundManager.setMusicVolume(volumePercent)
                
                if (volumePercent > 0) {
                    // Resume music if it was stopped
                    SoundManager.resumeBackgroundMusic()
                } else {
                    // Stop music if volume is 0
                    SoundManager.stopBackgroundMusic()
                }
            }
        }

        // Reset Progress Button
        btnResetProgress.setOnClickListener {
            showResetConfirmationDialog()
        }
        
        // Developer Mode - Hidden tap counter on version text
        tvAppVersion.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            
            // Reset counter if taps are too far apart
            if (currentTime - lastTapTime > TAP_TIMEOUT) {
                versionTapCount = 0
            }
            
            lastTapTime = currentTime
            versionTapCount++
            
            // Show progress feedback
            when {
                versionTapCount >= REQUIRED_TAPS -> {
                    // Developer mode unlocked!
                    DeveloperPreferences.setDeveloperModeUnlocked(this)
                    DeveloperPreferences.setDeveloperModeEnabled(this, true)
                    showDeveloperModeDialog()
                    updateDeveloperModeUI()
                    versionTapCount = 0 // Reset counter
                }
                versionTapCount >= REQUIRED_TAPS - 3 -> {
                    // Show hint when close
                    Toast.makeText(
                        this,
                        "🔓 ${REQUIRED_TAPS - versionTapCount} more tap${if (REQUIRED_TAPS - versionTapCount > 1) "s" else ""}...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        
        // Developer Mode Toggle Button
        btnToggleDeveloperMode.setOnClickListener {
            val currentlyEnabled = DeveloperPreferences.isDeveloperModeEnabled(this)
            val newState = !currentlyEnabled
            
            DeveloperPreferences.setDeveloperModeEnabled(this, newState)
            updateDeveloperModeUI()
            
            if (newState) {
                // Enabling - launch console
                Toast.makeText(this, "🛠️ Developer Mode Enabled", Toast.LENGTH_SHORT).show()
                checkOverlayPermissionAndLaunchConsole()
            } else {
                // Disabling - stop console
                Toast.makeText(this, "Developer Mode Disabled", Toast.LENGTH_SHORT).show()
                FloatingConsoleService.stop(this)
            }
            
            SoundManager.playButtonClick()
        }
    }

    /**
     * Update developer mode UI (toggle button visibility and text)
     */
    private fun updateDeveloperModeUI() {
        val isUnlocked = DeveloperPreferences.isDeveloperModeUnlocked(this)
        val isEnabled = DeveloperPreferences.isDeveloperModeEnabled(this)
        
        if (isUnlocked) {
            btnToggleDeveloperMode.visibility = android.view.View.VISIBLE
            btnToggleDeveloperMode.text = if (isEnabled) {
                "🛠️ Developer Mode: ON"
            } else {
                "🛠️ Developer Mode: OFF"
            }
        } else {
            btnToggleDeveloperMode.visibility = android.view.View.GONE
        }
    }
    
    /**
     * Show confirmation dialog before resetting progress
     */
    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Reset All Progress?")
            .setMessage("This will permanently delete:\n\n" +
                    "• All completed levels\n" +
                    "• All earned badges\n" +
                    "• All scores and OptiHints\n" +
                    "• All progress data\n\n" +
                    "This action cannot be undone!\n\n" +
                    "Are you sure you want to continue?")
            .setPositiveButton("Reset Everything") { dialog, _ ->
                resetProgress()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                SoundManager.playButtonClick()
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(true)
            .show()
    }
    
    /**
     * Show developer mode activation dialog
     */
    private fun showDeveloperModeDialog() {
        SoundManager.playButtonClick()
        
        AlertDialog.Builder(this)
            .setTitle("🛠️ Developer Mode Activated")
            .setMessage("You now have access to:\n\n" +
                    "• Floating debug console\n" +
                    "• Real-time logs\n" +
                    "• Command execution\n\n" +
                    "The console will appear on your screen and can be minimized or closed at any time.")
            .setPositiveButton("Open Console") { dialog, _ ->
                SoundManager.playButtonClick()
                dialog.dismiss()
                checkOverlayPermissionAndLaunchConsole()
            }
            .setNegativeButton("Later") { dialog, _ ->
                SoundManager.playButtonClick()
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    
    /**
     * Check if overlay permission is granted and launch console
     */
    private fun checkOverlayPermissionAndLaunchConsole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // Request overlay permission
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("To display the floating console, OptiLearn needs permission to draw over other apps.\n\n" +
                            "This will only be used for the debug console.")
                    .setPositiveButton("Grant Permission") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(this, "Console requires overlay permission", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            } else {
                // Permission already granted, launch console
                launchConsole()
            }
        } else {
            // No permission needed for older Android versions
            launchConsole()
        }
    }
    
    /**
     * Launch the floating console service
     */
    private fun launchConsole() {
        DebugLogger.info("Launching floating console...")
        FloatingConsoleService.start(this)
        Toast.makeText(this, "🛠️ Console opened", Toast.LENGTH_SHORT).show()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    launchConsole()
                } else {
                    Toast.makeText(this, "Permission denied. Console cannot be displayed.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Reset all user progress
     */
    private fun resetProgress() {
        // Play sound
        SoundManager.playButtonClick()
        
        // Reset progress in database
        viewModel.resetAllProgress()
        
        // Show success dialog
        AlertDialog.Builder(this)
            .setTitle("✅ Progress Reset Complete")
            .setMessage("All progress has been reset successfully!\n\n" +
                    "You can now start your OptiLearn journey from the beginning.")
            .setPositiveButton("OK") { dialog, _ ->
                SoundManager.playButtonClick()
                dialog.dismiss()
                finish() // Return to main menu
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        SoundManager.startBackgroundMusic(R.raw.music_menu)
        // Refresh settings in case they were changed elsewhere
        loadCurrentSettings()
    }

    override fun onPause() {
        super.onPause()
        SoundManager.pauseBackgroundMusic()
    }
}
