package com.lokixcz.optilearn.managers

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import androidx.annotation.RawRes
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
    private const val KEY_SOUND_VOLUME = "sound_volume"
    private const val KEY_MUSIC_VOLUME = "music_volume"

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private var context: Context? = null
    private var prefs: SharedPreferences? = null
    private var currentMusicResId: Int? = null
    private var lastMusicResId: Int? = null

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
    private var soundVolume: Float = 1.0f // 0.0 to 1.0
    private var musicVolume: Float = 0.3f // 0.0 to 1.0
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
        soundVolume = prefs?.getFloat(KEY_SOUND_VOLUME, 1.0f) ?: 1.0f
        musicVolume = prefs?.getFloat(KEY_MUSIC_VOLUME, 0.3f) ?: 0.3f

        // Initialize SoundPool for sound effects
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds()

        isInitialized = true
        Log.d(TAG, "SoundManager initialized")
    }

    private fun loadSounds() {
        soundCorrect = loadSound(R.raw.sfx_correct)
        soundWrong = loadSound(R.raw.sfx_wrong)
        soundButtonClick = loadSound(R.raw.sfx_button_click)
        soundLevelComplete = loadSound(R.raw.sfx_level_complete)
        soundBadgeUnlock = loadSound(R.raw.sfx_badge_unlock)
        soundCoinCollect = loadSound(R.raw.sfx_coin_collect)
        soundStreak = loadSound(R.raw.sfx_streak)
        soundLevelUnlock = loadSound(R.raw.sfx_level_unlock)
        soundHintUse = loadSound(R.raw.sfx_hint_use).takeIf { it != 0 }
            ?: loadSound(R.raw.sfx_coin_collect)
        Log.d(TAG, "Sound effects loaded")
    }

    private fun loadSound(@RawRes resId: Int): Int {
        val pool = soundPool ?: return 0
        val ctx = context ?: return 0
        return try {
            val soundId = pool.load(ctx, resId, 1)
            if (soundId == 0) {
                Log.w(TAG, "SoundPool returned 0 for resId=$resId")
            }
            soundId
        } catch (e: Exception) {
            Log.e(TAG, "Error loading sound resId=$resId: ${e.message}")
            0
        }
    }

    /**
     * Play a sound effect
     */
    private fun playSound(soundId: Int, volume: Float = 1.0f) {
        if (!soundEnabled || !isInitialized || soundId == 0) return

        try {
            val finalVolume = volume * soundVolume
            soundPool?.play(soundId, finalVolume, finalVolume, 1, 0, 1.0f)
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
            // If the requested track is already playing, just ensure it's running
            if (currentMusicResId == rawResId && mediaPlayer?.isPlaying == true) {
                return
            }

            // If the same track is paused, resume it instead of recreating the player
            if (currentMusicResId == rawResId && mediaPlayer != null) {
                resumeBackgroundMusic()
                return
            }

            stopBackgroundMusic()

            val ctx = context ?: return
            mediaPlayer = MediaPlayer.create(ctx, rawResId)?.apply {
                isLooping = true
                setVolume(musicVolume, musicVolume)
                start()
                currentMusicResId = rawResId
                lastMusicResId = rawResId
                Log.d(TAG, "Background music started (resId=$rawResId)")
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
                currentMusicResId = null
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
            } ?: lastMusicResId?.let { lastRes ->
                startBackgroundMusic(lastRes)
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
     * Set sound effects volume (0-100)
     */
    fun setSoundVolume(volumePercent: Int) {
        soundVolume = (volumePercent.coerceIn(0, 100) / 100f)
        prefs?.edit()?.putFloat(KEY_SOUND_VOLUME, soundVolume)?.apply()
        soundEnabled = volumePercent > 0
        prefs?.edit()?.putBoolean(KEY_SOUND_ENABLED, soundEnabled)?.apply()
        Log.d(TAG, "Sound volume set to $volumePercent% (${soundVolume}f)")
    }

    /**
     * Set background music volume (0-100)
     */
    fun setMusicVolume(volumePercent: Int) {
        musicVolume = (volumePercent.coerceIn(0, 100) / 100f)
        prefs?.edit()?.putFloat(KEY_MUSIC_VOLUME, musicVolume)?.apply()
        musicEnabled = volumePercent > 0
        prefs?.edit()?.putBoolean(KEY_MUSIC_ENABLED, musicEnabled)?.apply()
        
        // Update current playing music volume
        try {
            mediaPlayer?.setVolume(musicVolume, musicVolume)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting music volume: ${e.message}")
        }
        
        Log.d(TAG, "Music volume set to $volumePercent% (${musicVolume}f)")
    }

    /**
     * Get current sound volume (0-100)
     */
    fun getSoundVolume(): Int = (soundVolume * 100).toInt()

    /**
     * Get current music volume (0-100)
     */
    fun getMusicVolume(): Int = (musicVolume * 100).toInt()

    /**
     * Release all resources
     */
    fun release() {
        try {
            stopBackgroundMusic()
            soundPool?.release()
            soundPool = null
            currentMusicResId = null
            lastMusicResId = null
            isInitialized = false
            Log.d(TAG, "SoundManager released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundManager: ${e.message}")
        }
    }
}
