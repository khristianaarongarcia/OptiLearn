package com.lokixcz.optilearn

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lokixcz.optilearn.managers.SoundManager
import com.lokixcz.optilearn.viewmodel.GameViewModel

/**
 * Settings Activity
 * Allows users to control sound, music, and reset progress
 */
class SettingsActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var switchSoundEffects: SwitchMaterial
    private lateinit var switchBackgroundMusic: SwitchMaterial
    private lateinit var btnResetProgress: MaterialButton

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
        switchSoundEffects = findViewById(R.id.switchSoundEffects)
        switchBackgroundMusic = findViewById(R.id.switchBackgroundMusic)
        btnResetProgress = findViewById(R.id.btnResetProgress)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadCurrentSettings() {
        // Load current settings from SoundManager
        switchSoundEffects.isChecked = SoundManager.isSoundEnabled()
        switchBackgroundMusic.isChecked = SoundManager.isMusicEnabled()
    }

    private fun setupListeners() {
        // Sound Effects Switch
        switchSoundEffects.setOnCheckedChangeListener { _, isChecked ->
            SoundManager.setSoundEnabled(isChecked)
            
            // Play a test sound if enabled
            if (isChecked) {
                SoundManager.playButtonClick()
            }
        }

        // Background Music Switch
        switchBackgroundMusic.setOnCheckedChangeListener { _, isChecked ->
            SoundManager.setMusicEnabled(isChecked)
            
            if (isChecked) {
                // Resume music if it was playing
                SoundManager.resumeBackgroundMusic()
            } else {
                // Stop music
                SoundManager.stopBackgroundMusic()
            }
        }

        // Reset Progress Button
        btnResetProgress.setOnClickListener {
            showResetConfirmationDialog()
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
        // Refresh settings in case they were changed elsewhere
        loadCurrentSettings()
    }
}
