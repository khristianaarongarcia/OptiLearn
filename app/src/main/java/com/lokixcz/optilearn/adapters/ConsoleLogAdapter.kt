package com.lokixcz.optilearn.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.utils.ConsoleLog

/**
 * Adapter for Console Log RecyclerView
 */
class ConsoleLogAdapter : RecyclerView.Adapter<ConsoleLogAdapter.LogViewHolder>() {

    private val logs = mutableListOf<ConsoleLog>()

    /**
     * Update logs and refresh
     */
    fun setLogs(newLogs: List<ConsoleLog>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }

    /**
     * Add a single log entry
     */
    fun addLog(log: ConsoleLog) {
        logs.add(log)
        notifyItemInserted(logs.size - 1)
    }

    /**
     * Clear all logs
     */
    fun clearLogs() {
        logs.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_console_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount(): Int = logs.size

    /**
     * ViewHolder for console log items
     */
    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLogLevel: TextView = itemView.findViewById(R.id.tvLogLevel)
        private val tvLogMessage: TextView = itemView.findViewById(R.id.tvLogMessage)

        fun bind(log: ConsoleLog) {
            tvLogLevel.text = log.level.tag
            tvLogLevel.setTextColor(Color.parseColor(log.level.color))
            
            val message = "[${log.getFormattedTime()}] ${log.message}"
            tvLogMessage.text = message
        }
    }
}
