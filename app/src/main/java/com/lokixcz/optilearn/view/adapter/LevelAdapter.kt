package com.lokixcz.optilearn.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.model.Level
import com.lokixcz.optilearn.utils.DebugLogger

class LevelAdapter(
    private var levels: List<Level>,
    private val onLevelClick: (Level) -> Unit
) : RecyclerView.Adapter<LevelAdapter.LevelViewHolder>() {

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val viewHighlightRing: View = itemView.findViewById(R.id.viewHighlightRing)
        val tvLevelNumber: TextView = itemView.findViewById(R.id.tvLevelNumber)
        val ivBadgeIcon: ImageView = itemView.findViewById(R.id.ivBadgeIcon)
        val tvBadgeIcon: TextView = itemView.findViewById(R.id.tvBadgeIcon)
        val ivLockIcon: ImageView = itemView.findViewById(R.id.ivLockIcon)
        val tvCompletedMark: TextView = itemView.findViewById(R.id.tvCompletedMark)
        val tvLevelTitle: TextView = itemView.findViewById(R.id.tvLevelTitle)
        val tvHighScore: TextView = itemView.findViewById(R.id.tvHighScore)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val viewProgressLine: View = itemView.findViewById(R.id.viewProgressLine)
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
        
        // Set icon - try to load PNG, fall back to emoji if icon is an emoji
        if (level.badgeIcon.length <= 2) {
            // It's an emoji (fallback for missing icons)
            holder.ivBadgeIcon.visibility = View.GONE
            holder.tvBadgeIcon.visibility = View.VISIBLE
            holder.tvBadgeIcon.text = level.badgeIcon
        } else {
            // It's a drawable resource name
            holder.tvBadgeIcon.visibility = View.GONE
            holder.ivBadgeIcon.visibility = View.VISIBLE
            val drawableId = context.resources.getIdentifier(
                level.badgeIcon,
                "drawable",
                context.packageName
            )
            if (drawableId != 0) {
                holder.ivBadgeIcon.setImageResource(drawableId)
            } else {
                // Fallback if drawable not found
                holder.ivBadgeIcon.setImageResource(R.drawable.optics_explorer)
            }
        }
        
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
                holder.ivBadgeIcon.alpha = 0.3f
                holder.tvStatus.text = "LOCKED"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_locked)
                holder.cardView.alpha = 0.6f
                holder.cardView.isEnabled = false
            }
        }
        
        // Reset badge icon alpha for non-locked levels
        if (level.isUnlocked || level.isCompleted) {
            holder.tvBadgeIcon.alpha = 1.0f
            holder.ivBadgeIcon.alpha = 1.0f
        }
        
        // Handle progress line (connection to previous level)
        // First level (position 0 after reversal = Level 15) has no line above it
        DebugLogger.debug("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        DebugLogger.debug("Quest Map Line Debug - Position $position")
        DebugLogger.debug("Level: ${level.title} (ID: ${level.levelId})")
        DebugLogger.debug("Status: Completed=${level.isCompleted}, Unlocked=${level.isUnlocked}")
        
        if (position == 0) {
            holder.viewProgressLine.visibility = View.GONE
            DebugLogger.debug("Line Action: HIDDEN (first level, no line above)")
        } else {
            holder.viewProgressLine.visibility = View.VISIBLE
            DebugLogger.debug("Line Action: VISIBLE")
            
            // Check previous level's status (position - 1)
            val previousLevel = levels[position - 1]
            DebugLogger.debug("Previous Level: ${previousLevel.title} (ID: ${previousLevel.levelId})")
            DebugLogger.debug("Previous Status: Completed=${previousLevel.isCompleted}, Unlocked=${previousLevel.isUnlocked}")
            
            when {
                // Current level is completed - show completed line
                level.isCompleted -> {
                    holder.viewProgressLine.setBackgroundResource(R.drawable.progress_line_completed)
                    holder.viewProgressLine.clearAnimation()
                    DebugLogger.info("Line Style: COMPLETED (cyan gradient) - Current level is completed")
                }
                // Current level is unlocked (active) - show pulsing line
                level.isUnlocked -> {
                    holder.viewProgressLine.setBackgroundResource(R.drawable.progress_line_active)
                    val pulseAnimation = android.view.animation.AnimationUtils.loadAnimation(
                        context, R.anim.progress_line_pulse
                    )
                    holder.viewProgressLine.startAnimation(pulseAnimation)
                    DebugLogger.info("Line Style: ACTIVE (pulsing cyan) - Current level is unlocked")
                }
                else -> {
                    // Future/locked connection - show gray line
                    holder.viewProgressLine.setBackgroundResource(R.drawable.progress_line_locked)
                    holder.viewProgressLine.clearAnimation()
                    DebugLogger.info("Line Style: LOCKED (gray) - Current level is locked")
                }
            }
            
            // Additional view state debugging
            DebugLogger.debug("View Details:")
            DebugLogger.debug("  - Line visibility: ${if (holder.viewProgressLine.visibility == View.VISIBLE) "VISIBLE" else "GONE"}")
            DebugLogger.debug("  - Line width: ${holder.viewProgressLine.width}dp")
            DebugLogger.debug("  - Line height: ${holder.viewProgressLine.height}dp")
            DebugLogger.debug("  - Line translationZ: ${holder.viewProgressLine.translationZ}")
            DebugLogger.debug("  - Parent clipChildren: ${(holder.itemView.parent as? ViewGroup)?.clipChildren}")
        }
        DebugLogger.debug("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
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
