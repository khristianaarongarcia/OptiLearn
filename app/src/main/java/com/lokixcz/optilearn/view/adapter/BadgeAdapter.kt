package com.lokixcz.optilearn.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        val ivBadgeIcon: ImageView = itemView.findViewById(R.id.ivBadgeIcon)
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
        val context = holder.itemView.context
        
        // Set badge icon - try to load PNG, fall back to emoji if icon is an emoji
        val iconName = Constants.BADGE_ICONS[badge.levelId] ?: "🏆"
        if (iconName.length <= 2) {
            // It's an emoji (fallback for missing icons)
            holder.ivBadgeIcon.visibility = View.GONE
            holder.tvBadgeIcon.visibility = View.VISIBLE
            holder.tvBadgeIcon.text = iconName
        } else {
            // It's a drawable resource name
            holder.tvBadgeIcon.visibility = View.GONE
            holder.ivBadgeIcon.visibility = View.VISIBLE
            val drawableId = context.resources.getIdentifier(
                iconName,
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
        
        // Set level name and number
        holder.tvBadgeName.text = badge.title
        holder.tvLevelNumber.text = "Level ${badge.levelId}"
        
        // Handle badge state (completed, unlocked, or locked)
        when {
            badge.isCompleted -> {
                // Badge earned - full color, show stats, show icon
                holder.cardBadge.alpha = 1.0f
                holder.ivLockIcon.visibility = View.GONE
                holder.ivBadgeIcon.visibility = if (iconName.length > 2) View.VISIBLE else View.GONE
                holder.tvBadgeIcon.visibility = if (iconName.length <= 2) View.VISIBLE else View.GONE
                holder.tvStatus.visibility = View.VISIBLE
                holder.tvStatus.text = "COMPLETED"
                holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.correct_answer)) // Teal
                
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
                // Badge available but not earned yet - show icon
                holder.cardBadge.alpha = 0.8f
                holder.ivLockIcon.visibility = View.GONE
                holder.ivBadgeIcon.visibility = if (iconName.length > 2) View.VISIBLE else View.GONE
                holder.tvBadgeIcon.visibility = if (iconName.length <= 2) View.VISIBLE else View.GONE
                holder.tvStatus.visibility = View.VISIBLE
                holder.tvStatus.text = "UNLOCKED"
                holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.unlocked_level)) // Cyan
                holder.layoutHighScore.visibility = View.GONE
                holder.tvPerfectBadge.visibility = View.GONE
            }
            else -> {
                // Badge locked - hide icon, show only lock
                holder.cardBadge.alpha = 0.5f
                holder.ivLockIcon.visibility = View.VISIBLE
                holder.ivBadgeIcon.visibility = View.GONE
                holder.tvBadgeIcon.visibility = View.GONE
                holder.tvStatus.visibility = View.VISIBLE
                holder.tvStatus.text = "LOCKED"
                holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.locked_level)) // Gray
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
