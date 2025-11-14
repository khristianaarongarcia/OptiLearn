package com.lokixcz.optilearn.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Developer Mode Preferences Manager
 * Handles storing and retrieving developer mode settings
 */
object DeveloperPreferences {
    
    private const val PREFS_NAME = "optilearn_developer_prefs"
    private const val KEY_DEVELOPER_MODE_ENABLED = "developer_mode_enabled"
    private const val KEY_DEVELOPER_MODE_UNLOCKED = "developer_mode_unlocked"
    private const val KEY_CONSOLE_VISIBLE = "console_visible"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Check if developer mode is enabled
     */
    fun isDeveloperModeEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_DEVELOPER_MODE_ENABLED, false)
    }
    
    /**
     * Enable or disable developer mode
     */
    fun setDeveloperModeEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit()
            .putBoolean(KEY_DEVELOPER_MODE_ENABLED, enabled)
            .apply()
    }
    
    /**
     * Check if console is currently visible
     */
    fun isConsoleVisible(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_CONSOLE_VISIBLE, false)
    }
    
    /**
     * Set console visibility state
     */
    fun setConsoleVisible(context: Context, visible: Boolean) {
        getPreferences(context).edit()
            .putBoolean(KEY_CONSOLE_VISIBLE, visible)
            .apply()
    }
    
    /**
     * Check if developer mode has ever been unlocked (user tapped 10 times)
     * This determines if the toggle button should be visible
     */
    fun isDeveloperModeUnlocked(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_DEVELOPER_MODE_UNLOCKED, false)
    }
    
    /**
     * Mark developer mode as unlocked (show toggle button forever)
     */
    fun setDeveloperModeUnlocked(context: Context) {
        getPreferences(context).edit()
            .putBoolean(KEY_DEVELOPER_MODE_UNLOCKED, true)
            .apply()
    }
}
