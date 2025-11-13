package com.lokixcz.optilearn.managers

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.lokixcz.optilearn.R

/**
 * Singleton class to manage all sound effects and background music in the app.
 * Handles looping music, sound effects playback, and user preferences.
 */
object SoundManager {
    private const val TAG = "SoundManager"
    private const val PREFS_NAME = "OptiLearnPrefs"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_MUSIC_ENABLED = "music_enabled"

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private var context: Context? = null
    private var prefs: SharedPreferences? = null

    // Sound IDs
    private var soundCorrect: Int = 0
    private var soundWrong: Int = 0
    private var soundButtonClick: Int = 0
    private var soundLevelComplete: Int = 0
    private var soundBadgeUnlock: Int = 0
    private var soundCoinCollect: Int = 0
    private var soundStreak: Int = 0
    private var soundLevelUnlock: Int = 0
    private var soundHintUse: Int = 0

    // Settings
    private var soundEnabled: Boolean = true
    private var musicEnabled: Boolean = true
    private var isInitialized: Boolean = false

    /**
     * Initialize the SoundManager with application context
     */
    fun init(context: Context) {
        if (isInitialized) return

        this.context = context.applicationContext
        this.prefs = this.context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load preferences
        soundEnabled = prefs?.getBoolean(KEY_SOUND_ENABLED, true) ?: true
        musicEnabled = prefs?.getBoolean(KEY_MUSIC_ENABLED, true) ?: true

        // Initialize SoundPool for sound effects
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sound effects (using placeholder resource IDs - will create later)
        // For now, we'll use confetti_animation as placeholder for all sounds
        loadSounds()

        isInitialized = true
        Log.d(TAG, "SoundManager initialized")
    }

    private fun loadSounds() {
        // Note: These will use the confetti animation JSON as placeholder until actual sound files are added
        // In actual implementation, replace with proper .mp3 or .ogg files
        try {
            soundCorrect = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundWrong = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundButtonClick = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundLevelComplete = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundBadgeUnlock = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundCoinCollect = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundStreak = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundLevelUnlock = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            soundHintUse = soundPool?.load(context, R.raw.confetti_animation, 1) ?: 0
            Log.d(TAG, "Sounds loaded (using placeholders)")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading sounds: ${e.message}")
        }
    }

    /**
     * Play a sound effect
     */
    private fun playSound(soundId: Int, volume: Float = 1.0f) {
        if (!soundEnabled || !isInitialized || soundId == 0) return

        try {
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound: ${e.message}")
        }
    }

    // Public methods for specific sound effects
    fun playCorrectSound() = playSound(soundCorrect)
    fun playWrongSound() = playSound(soundWrong)
    fun playButtonClick() = playSound(soundButtonClick, 0.5f)
    fun playLevelComplete() = playSound(soundLevelComplete)
    fun playBadgeUnlock() = playSound(soundBadgeUnlock)
    fun playCoinCollect() = playSound(soundCoinCollect, 0.7f)
    fun playStreakSound() = playSound(soundStreak)
    fun playLevelUnlock() = playSound(soundLevelUnlock)
    fun playHintUse() = playSound(soundHintUse)

    /**
     * Start background music with looping
     */
    fun startBackgroundMusic(rawResId: Int) {
        if (!musicEnabled || !isInitialized) return

        try {
            stopBackgroundMusic()

            mediaPlayer = MediaPlayer.create(context, rawResId)?.apply {
                isLooping = true
                setVolume(0.3f, 0.3f) // Lower volume for background music
                start()
                Log.d(TAG, "Background music started")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting background music: ${e.message}")
        }
    }

    /**
     * Stop background music
     */
    fun stopBackgroundMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                mediaPlayer = null
                Log.d(TAG, "Background music stopped")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping background music: ${e.message}")
        }
    }

    /**
     * Pause background music
     */
    fun pauseBackgroundMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    Log.d(TAG, "Background music paused")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing background music: ${e.message}")
        }
    }

    /**
     * Resume background music
     */
    fun resumeBackgroundMusic() {
        if (!musicEnabled || !isInitialized) return

        try {
            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                    Log.d(TAG, "Background music resumed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming background music: ${e.message}")
        }
    }

    /**
     * Enable/disable sound effects
     */
    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
        prefs?.edit()?.putBoolean(KEY_SOUND_ENABLED, enabled)?.apply()
        Log.d(TAG, "Sound effects ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Enable/disable background music
     */
    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        prefs?.edit()?.putBoolean(KEY_MUSIC_ENABLED, enabled)?.apply()

        if (!enabled) {
            stopBackgroundMusic()
        }
        Log.d(TAG, "Background music ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Get current sound enabled state
     */
    fun isSoundEnabled(): Boolean = soundEnabled

    /**
     * Get current music enabled state
     */
    fun isMusicEnabled(): Boolean = musicEnabled

    /**
     * Release all resources
     */
    fun release() {
        try {
            stopBackgroundMusic()
            soundPool?.release()
            soundPool = null
            isInitialized = false
            Log.d(TAG, "SoundManager released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundManager: ${e.message}")
        }
    }
}
