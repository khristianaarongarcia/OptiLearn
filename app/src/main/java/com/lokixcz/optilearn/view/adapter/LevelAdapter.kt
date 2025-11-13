package com.lokixcz.optilearn.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.model.Level

class LevelAdapter(
    private var levels: List<Level>,
    private val onLevelClick: (Level) -> Unit
) : RecyclerView.Adapter<LevelAdapter.LevelViewHolder>() {

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val viewHighlightRing: View = itemView.findViewById(R.id.viewHighlightRing)
        val tvLevelNumber: TextView = itemView.findViewById(R.id.tvLevelNumber)
        val tvBadgeIcon: TextView = itemView.findViewById(R.id.tvBadgeIcon)
        val ivLockIcon: ImageView = itemView.findViewById(R.id.ivLockIcon)
        val tvCompletedMark: TextView = itemView.findViewById(R.id.tvCompletedMark)
        val tvLevelTitle: TextView = itemView.findViewById(R.id.tvLevelTitle)
        val tvHighScore: TextView = itemView.findViewById(R.id.tvHighScore)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position]
        val context = holder.itemView.context
        
        holder.tvLevelNumber.text = level.levelId.toString()
        holder.tvBadgeIcon.text = level.badgeIcon
        holder.tvLevelTitle.text = level.title
        
        // Reset all visual states
        holder.viewHighlightRing.visibility = View.GONE
        holder.ivLockIcon.visibility = View.GONE
        holder.tvCompletedMark.visibility = View.GONE
        holder.tvHighScore.visibility = View.GONE
        holder.tvStatus.visibility = View.VISIBLE
        
        // Update visual state based on level status
        when {
            level.isCompleted -> {
                // Completed level - show check mark and high score
                holder.tvCompletedMark.visibility = View.VISIBLE
                holder.tvHighScore.text = "★ ${level.highScore}%"
                holder.tvHighScore.visibility = View.VISIBLE
                holder.tvStatus.text = "REPLAY"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_completed)
                holder.cardView.alpha = 1.0f
                holder.cardView.isEnabled = true
            }
            level.isUnlocked -> {
                // Current/unlocked level - highlight with ring
                holder.viewHighlightRing.visibility = View.VISIBLE
                holder.tvStatus.text = "PLAY"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_unlocked)
                holder.cardView.alpha = 1.0f
                holder.cardView.isEnabled = true
            }
            else -> {
                // Locked level - show lock icon
                holder.ivLockIcon.visibility = View.VISIBLE
                holder.tvBadgeIcon.alpha = 0.3f
                holder.tvStatus.text = "LOCKED"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_locked)
                holder.cardView.alpha = 0.6f
                holder.cardView.isEnabled = false
            }
        }
        
        // Reset badge icon alpha for non-locked levels
        if (level.isUnlocked || level.isCompleted) {
            holder.tvBadgeIcon.alpha = 1.0f
        }
        
        // Set click listener
        holder.itemView.setOnClickListener {
            if (level.isUnlocked || level.isCompleted) {
                onLevelClick(level)
            }
        }
    }

    override fun getItemCount(): Int = levels.size

    fun updateLevels(newLevels: List<Level>) {
        // Reverse the levels so Level 1 is at bottom, Level 15 at top
        levels = newLevels.reversed()
        notifyDataSetChanged()
    }
}
