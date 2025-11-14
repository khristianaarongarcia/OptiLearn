package com.lokixcz.optilearn.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Console Log Entry
 */
data class ConsoleLog(
    val level: LogLevel,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class LogLevel(val tag: String, val color: String) {
        DEBUG("D", "#00BCD4"),
        INFO("I", "#4CAF50"),
        WARNING("W", "#FF9800"),
        ERROR("E", "#FF5252"),
        COMMAND(">", "#9C27B0")
    }
    
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

/**
 * Debug Logger Singleton
 * Captures logs and provides them to the floating console
 */
object DebugLogger {
    
    private val logs = mutableListOf<ConsoleLog>()
    private val maxLogs = 500 // Keep only last 500 logs
    private val listeners = mutableListOf<(ConsoleLog) -> Unit>()
    
    /**
     * Add a log entry
     */
    fun log(level: ConsoleLog.LogLevel, message: String) {
        val log = ConsoleLog(level, message)
        
        synchronized(logs) {
            logs.add(log)
            
            // Trim old logs if needed
            if (logs.size > maxLogs) {
                logs.removeAt(0)
            }
        }
        
        // Notify listeners
        notifyListeners(log)
    }
    
    /**
     * Convenience methods for different log levels
     */
    fun debug(message: String) = log(ConsoleLog.LogLevel.DEBUG, message)
    fun info(message: String) = log(ConsoleLog.LogLevel.INFO, message)
    fun warning(message: String) = log(ConsoleLog.LogLevel.WARNING, message)
    fun error(message: String) = log(ConsoleLog.LogLevel.ERROR, message)
    fun command(message: String) = log(ConsoleLog.LogLevel.COMMAND, message)
    
    /**
     * Get all logs
     */
    fun getAllLogs(): List<ConsoleLog> {
        synchronized(logs) {
            return logs.toList()
        }
    }
    
    /**
     * Clear all logs
     */
    fun clearLogs() {
        synchronized(logs) {
            logs.clear()
        }
    }
    
    /**
     * Register a listener for new logs
     */
    fun addListener(listener: (ConsoleLog) -> Unit) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }
    
    /**
     * Remove a listener
     */
    fun removeListener(listener: (ConsoleLog) -> Unit) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }
    
    /**
     * Notify all listeners of a new log
     */
    private fun notifyListeners(log: ConsoleLog) {
        synchronized(listeners) {
            listeners.forEach { it(log) }
        }
    }
}
