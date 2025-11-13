package com.lokixcz.optilearn.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private val editor: SharedPreferences.Editor = prefs.edit()
    
    // First Launch
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(Constants.KEY_FIRST_LAUNCH, true)
    }
    
    fun setFirstLaunch(isFirst: Boolean) {
        editor.putBoolean(Constants.KEY_FIRST_LAUNCH, isFirst).apply()
    }
    
    // Sound Settings
    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(Constants.KEY_SOUND_ENABLED, true)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        editor.putBoolean(Constants.KEY_SOUND_ENABLED, enabled).apply()
    }
    
    // Music Settings
    fun isMusicEnabled(): Boolean {
        return prefs.getBoolean(Constants.KEY_MUSIC_ENABLED, true)
    }
    
    fun setMusicEnabled(enabled: Boolean) {
        editor.putBoolean(Constants.KEY_MUSIC_ENABLED, enabled).apply()
    }
    
    // User ID
    fun getUserId(): Int {
        return prefs.getInt(Constants.KEY_USER_ID, 1)
    }
    
    fun setUserId(userId: Int) {
        editor.putInt(Constants.KEY_USER_ID, userId).apply()
    }
    
    // Clear all preferences
    fun clearAll() {
        editor.clear().apply()
    }
}
