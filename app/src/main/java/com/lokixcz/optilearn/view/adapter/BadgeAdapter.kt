package com.lokixcz.optilearn.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.model.Level
import com.lokixcz.optilearn.utils.Constants

class BadgeAdapter(
    private var badges: List<Level>
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardBadge: MaterialCardView = itemView.findViewById(R.id.cardBadge)
        val tvBadgeIcon: TextView = itemView.findViewById(R.id.tvBadgeIcon)
        val ivLockIcon: ImageView = itemView.findViewById(R.id.ivLockIcon)
        val tvBadgeName: TextView = itemView.findViewById(R.id.tvBadgeName)
        val tvLevelNumber: TextView = itemView.findViewById(R.id.tvLevelNumber)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val layoutHighScore: LinearLayout = itemView.findViewById(R.id.layoutHighScore)
        val tvHighScore: TextView = itemView.findViewById(R.id.tvHighScore)
        val tvPerfectBadge: TextView = itemView.findViewById(R.id.tvPerfectBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        
        // Set badge icon from constants
        holder.tvBadgeIcon.text = Constants.BADGE_ICONS[badge.levelId] ?: "🏆"
        
        // Set level name and number
        holder.tvBadgeName.text = badge.title
        holder.tvLevelNumber.text = "Level ${badge.levelId}"
        
        // Handle badge state (completed, unlocked, or locked)
        when {
            badge.isCompleted -> {
                // Badge earned - full color, show stats
                holder.cardBadge.alpha = 1.0f
                holder.ivLockIcon.visibility = View.GONE
                holder.tvStatus.visibility = View.VISIBLE
                holder.tvStatus.text = "COMPLETED"
                holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                
                // Show high score
                holder.layoutHighScore.visibility = View.VISIBLE
                holder.tvHighScore.text = "${badge.highScore}%"
                
                // Show perfect score indicator if applicable
                if (badge.highScore == 100) {
                    holder.tvPerfectBadge.visibility = View.VISIBLE
                } else {
                    holder.tvPerfectBadge.visibility = View.GONE
                }
            }
            badge.isUnlocked && !badge.isCompleted -> {
                // Badge available but not earned yet
                holder.cardBadge.alpha = 0.8f
                holder.ivLockIcon.visibility = View.GONE
                holder.tvStatus.visibility = View.VISIBLE
                holder.tvStatus.text = "UNLOCKED"
                holder.tvStatus.setBackgroundColor(Color.parseColor("#2196F3")) // Blue
                holder.layoutHighScore.visibility = View.GONE
                holder.tvPerfectBadge.visibility = View.GONE
            }
            else -> {
                // Badge locked - grayed out
                holder.cardBadge.alpha = 0.5f
                holder.ivLockIcon.visibility = View.VISIBLE
                holder.tvStatus.visibility = View.VISIBLE
                holder.tvStatus.text = "LOCKED"
                holder.tvStatus.setBackgroundColor(Color.parseColor("#9E9E9E")) // Gray
                holder.layoutHighScore.visibility = View.GONE
                holder.tvPerfectBadge.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = badges.size

    fun updateBadges(newBadges: List<Level>) {
        badges = newBadges
        notifyDataSetChanged()
    }
}
