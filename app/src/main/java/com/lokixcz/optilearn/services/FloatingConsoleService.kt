package com.lokixcz.optilearn.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.adapters.ConsoleLogAdapter
import com.lokixcz.optilearn.utils.ConsoleLog
import com.lokixcz.optilearn.utils.DebugLogger
import com.lokixcz.optilearn.utils.DeveloperPreferences

/**
 * Floating Console Service
 * Displays a system-wide overlay console for debugging
 */
class FloatingConsoleService : Service() {

    private lateinit var windowManager: WindowManager
    private var minimizedView: View? = null
    private var expandedView: View? = null
    private var isExpanded = false
    
    private lateinit var consoleLogAdapter: ConsoleLogAdapter
    private val logListener: (ConsoleLog) -> Unit = { log ->
        consoleLogAdapter.addLog(log)
        // Auto-scroll to bottom
        expandedView?.findViewById<RecyclerView>(R.id.rvConsoleLogs)?.let { recyclerView ->
            recyclerView.post {
                recyclerView.smoothScrollToPosition(consoleLogAdapter.itemCount - 1)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        // Start as foreground service to prevent crashes on Android 8.0+
        startForegroundWithNotification()
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        DebugLogger.info("FloatingConsole service started")
        DebugLogger.addListener(logListener)
        
        createMinimizedView()
        showMinimized()
    }
    
    /**
     * Start service in foreground with a persistent notification
     */
    private fun startForegroundWithNotification() {
        val channelId = "developer_console_channel"
        
        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Developer Console",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Floating debug console is running"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🛠️ Developer Console")
            .setContentText("Debug console is running")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        
        // Start foreground
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Create the minimized floating button
     */
    private fun createMinimizedView() {
        // Wrap context with Material theme
        val themedContext = ContextThemeWrapper(this, R.style.Theme_OptiLearn)
        
        minimizedView = LayoutInflater.from(themedContext).inflate(
            R.layout.floating_console_minimized,
            null
        )

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.END
        layoutParams.x = 16
        layoutParams.y = 100

        // Make it draggable
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        minimizedView?.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (initialTouchX - event.rawX).toInt()
                    layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(minimizedView, layoutParams)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // If it's a click (not drag), expand console
                    if (Math.abs(event.rawX - initialTouchX) < 10 &&
                        Math.abs(event.rawY - initialTouchY) < 10
                    ) {
                        showExpanded()
                    }
                    true
                }
                else -> false
            }
        }

        minimizedView?.tag = layoutParams
    }

    /**
     * Create the expanded console view
     */
    private fun createExpandedView() {
        // Wrap context with Material theme
        val themedContext = ContextThemeWrapper(this, R.style.Theme_OptiLearn)
        
        expandedView = LayoutInflater.from(themedContext).inflate(
            R.layout.floating_console_expanded,
            null
        )

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP
        layoutParams.x = 0
        layoutParams.y = 50

        // Setup RecyclerView
        val rvLogs = expandedView?.findViewById<RecyclerView>(R.id.rvConsoleLogs)
        consoleLogAdapter = ConsoleLogAdapter()
        rvLogs?.apply {
            layoutManager = LinearLayoutManager(this@FloatingConsoleService)
            adapter = consoleLogAdapter
        }

        // Load existing logs
        consoleLogAdapter.setLogs(DebugLogger.getAllLogs())

        // Setup buttons
        expandedView?.findViewById<MaterialButton>(R.id.btnMinimize)?.setOnClickListener {
            showMinimized()
        }

        expandedView?.findViewById<MaterialButton>(R.id.btnClose)?.setOnClickListener {
            stopSelf()
        }

        expandedView?.findViewById<MaterialButton>(R.id.btnClearLogs)?.setOnClickListener {
            DebugLogger.clearLogs()
            consoleLogAdapter.clearLogs()
            Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show()
        }

        expandedView?.findViewById<MaterialButton>(R.id.btnCopyLogs)?.setOnClickListener {
            copyLogsToClipboard()
        }

        expandedView?.findViewById<MaterialButton>(R.id.btnHelp)?.setOnClickListener {
            showHelp()
        }

        // Setup command input
        val etCommand = expandedView?.findViewById<EditText>(R.id.etCommand)
        val btnSend = expandedView?.findViewById<MaterialButton>(R.id.btnSendCommand)

        btnSend?.setOnClickListener {
            val command = etCommand?.text.toString().trim()
            if (command.isNotEmpty()) {
                executeCommand(command)
                etCommand?.text?.clear()
            }
        }

        // Send on Enter key
        etCommand?.setOnEditorActionListener { _, _, _ ->
            btnSend?.performClick()
            true
        }

        expandedView?.tag = layoutParams
    }

    /**
     * Show minimized console
     */
    private fun showMinimized() {
        if (isExpanded) {
            expandedView?.let { windowManager.removeView(it) }
            isExpanded = false
        }

        if (minimizedView?.parent == null) {
            windowManager.addView(minimizedView, minimizedView?.tag as WindowManager.LayoutParams)
        }

        DeveloperPreferences.setConsoleVisible(this, false)
    }

    /**
     * Show expanded console
     */
    private fun showExpanded() {
        if (expandedView == null) {
            createExpandedView()
        }

        minimizedView?.let { windowManager.removeView(it) }

        if (expandedView?.parent == null) {
            windowManager.addView(expandedView, expandedView?.tag as WindowManager.LayoutParams)
        }

        isExpanded = true
        DeveloperPreferences.setConsoleVisible(this, true)
    }

    /**
     * Execute a debug command
     */
    private fun executeCommand(command: String) {
        DebugLogger.command(command)

        when {
            command.equals("help", ignoreCase = true) -> {
                showHelp()
            }
            command.equals("clear", ignoreCase = true) -> {
                DebugLogger.clearLogs()
                consoleLogAdapter.clearLogs()
                DebugLogger.info("Logs cleared")
            }
            command.startsWith("echo ", ignoreCase = true) -> {
                val message = command.substring(5)
                DebugLogger.info(message)
            }
            command.equals("test", ignoreCase = true) -> {
                DebugLogger.debug("Debug test message")
                DebugLogger.info("Info test message")
                DebugLogger.warning("Warning test message")
                DebugLogger.error("Error test message")
            }
            else -> {
                DebugLogger.error("Unknown command: $command. Type 'help' for available commands.")
            }
        }
    }

    /**
     * Show help information
     */
    private fun showHelp() {
        DebugLogger.info("Available commands:")
        DebugLogger.info("  help - Show this help message")
        DebugLogger.info("  clear - Clear all logs")
        DebugLogger.info("  echo <message> - Echo a message")
        DebugLogger.info("  test - Generate test log messages")
    }
    
    /**
     * Copy all logs to clipboard
     */
    private fun copyLogsToClipboard() {
        val logs = DebugLogger.getAllLogs()
        
        if (logs.isEmpty()) {
            Toast.makeText(this, "No logs to copy", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Format logs as text
        val logText = buildString {
            appendLine("OptiLearn Debug Console Logs")
            appendLine("═══════════════════════════════════════")
            appendLine("Timestamp: ${System.currentTimeMillis()}")
            appendLine("Total Logs: ${logs.size}")
            appendLine("═══════════════════════════════════════")
            appendLine()
            
            logs.forEach { log ->
                appendLine("[${log.getFormattedTime()}] [${log.level.tag}] ${log.message}")
            }
            
            appendLine()
            appendLine("═══════════════════════════════════════")
            appendLine("End of logs")
        }
        
        // Copy to clipboard
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("OptiLearn Debug Logs", logText)
        clipboard.setPrimaryClip(clip)
        
        Toast.makeText(this, "📋 ${logs.size} logs copied to clipboard!", Toast.LENGTH_SHORT).show()
        DebugLogger.info("Logs copied to clipboard (${logs.size} entries)")
    }

    override fun onDestroy() {
        super.onDestroy()
        
        DebugLogger.removeListener(logListener)
        DebugLogger.info("FloatingConsole service stopped")

        minimizedView?.let {
            if (it.parent != null) {
                windowManager.removeView(it)
            }
        }

        expandedView?.let {
            if (it.parent != null) {
                windowManager.removeView(it)
            }
        }

        DeveloperPreferences.setConsoleVisible(this, false)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        
        /**
         * Start the floating console service
         */
        fun start(context: Context) {
            val intent = Intent(context, FloatingConsoleService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * Stop the floating console service
         */
        fun stop(context: Context) {
            val intent = Intent(context, FloatingConsoleService::class.java)
            context.stopService(intent)
        }
    }
}
