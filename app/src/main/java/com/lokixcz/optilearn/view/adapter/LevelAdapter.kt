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
        val viewBranchLine: View = itemView.findViewById(R.id.viewBranchLine)
        val levelContentContainer: View = itemView.findViewById(R.id.levelContentContainer)
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
            holder.ivBadgeIcon.visibility = View.GONE
            holder.tvBadgeIcon.visibility = View.VISIBLE
            holder.tvBadgeIcon.text = level.badgeIcon
        } else {
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
                holder.tvCompletedMark.visibility = View.VISIBLE
                holder.tvHighScore.text = "★ ${level.highScore}%"
                holder.tvHighScore.visibility = View.VISIBLE
                holder.tvStatus.text = "REPLAY"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_completed)
                holder.cardView.alpha = 1.0f
                holder.cardView.isEnabled = true
            }
            level.isUnlocked -> {
                holder.viewHighlightRing.visibility = View.VISIBLE
                holder.tvStatus.text = "PLAY"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_unlocked)
                holder.cardView.alpha = 1.0f
                holder.cardView.isEnabled = true
            }
            else -> {
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
        
        // ═══════════════════════════════════════════════════════════════
        // IMPROVED ZIGZAG LAYOUT WITH PROPER BRANCHING
        // ═══════════════════════════════════════════════════════════════
        
        setupZigzagLayout(holder, level, position, context)
        setupProgressLines(holder, level, position, context)
        
        // Set click listener
        holder.itemView.setOnClickListener {
            if (level.isUnlocked || level.isCompleted) {
                onLevelClick(level)
            }
        }
    }

    private fun setupZigzagLayout(
        holder: LevelViewHolder, 
        level: Level, 
        position: Int, 
        context: Context
    ) {
        val density = context.resources.displayMetrics.density
        val screenWidth = context.resources.displayMetrics.widthPixels
        
        // Configuration - based on item_level.xml
        val sideMargin = (16 * density).toInt() // 16dp from screen edge
        val circleContainerSize = (88 * density).toInt() // 88dp circle container (outer)
        val circleRadius = circleContainerSize / 2 // 44dp radius
        val branchHeight = (4 * density).toInt() // 4dp branch line height
        
        // Calculate positions
        val centerX = screenWidth / 2
        val isFirstLevel = level.levelId == 1
        val isRightSide = level.levelId % 2 == 0 // Even levels go right
        
        // Position the level content container
        val containerParams = holder.levelContentContainer.layoutParams as android.widget.FrameLayout.LayoutParams
        
        if (isFirstLevel) {
            // Level 1: Center aligned, no branch
            containerParams.gravity = android.view.Gravity.CENTER
            containerParams.marginStart = 0
            containerParams.marginEnd = 0
            holder.viewBranchLine.visibility = View.GONE
            
            DebugLogger.debug("Level ${level.levelId}: CENTER (no branch)")
        } else {
            // Level 2+: Alternate left/right with branch
            if (isRightSide) {
                // Right side positioning
                containerParams.gravity = android.view.Gravity.END or android.view.Gravity.CENTER_VERTICAL
                containerParams.marginEnd = sideMargin
                containerParams.marginStart = 0
            } else {
                // Left side positioning
                containerParams.gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
                containerParams.marginStart = sideMargin
                containerParams.marginEnd = 0
            }
            
            // Setup horizontal branch line
            setupBranchLine(holder, isRightSide, screenWidth, sideMargin, circleContainerSize, branchHeight, level)
            
            DebugLogger.debug("Level ${level.levelId}: ${if (isRightSide) "RIGHT" else "LEFT"}")
        }
        
        holder.levelContentContainer.layoutParams = containerParams
    }

    private fun setupBranchLine(
        holder: LevelViewHolder,
        isRightSide: Boolean,
        screenWidth: Int,
        sideMargin: Int,
        circleContainerSize: Int,
        branchHeight: Int,
        level: Level
    ) {
        holder.viewBranchLine.visibility = View.VISIBLE
        val branchParams = holder.viewBranchLine.layoutParams as android.widget.FrameLayout.LayoutParams
        
        val centerX = screenWidth / 2
        val circleRadius = circleContainerSize / 2
        
        // Calculate branch width correctly:
        val branchWidth = if (isRightSide) {
            (screenWidth - sideMargin - circleContainerSize) - centerX
        } else {
            centerX - (sideMargin + circleContainerSize)
        }
        
        // Add 3px overlap to ensure connection at center
        val branchWidthWithOverlap = branchWidth + 3
        
        branchParams.width = branchWidthWithOverlap
        branchParams.height = branchHeight
        branchParams.gravity = android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.CENTER_HORIZONTAL
        branchParams.marginStart = 0
        branchParams.marginEnd = 0
        
        // Position using translationX to shift from center horizontally
        if (isRightSide) {
            holder.viewBranchLine.translationX = (branchWidthWithOverlap / 2).toFloat() - 1.5f
        } else {
            holder.viewBranchLine.translationX = -(branchWidthWithOverlap / 2).toFloat() + 1.5f
        }
        
        holder.viewBranchLine.layoutParams = branchParams
        
        // Align branch vertically with circle center BEFORE first layout using ViewTreeObserver
        // This prevents the flicker of incorrect positioning
        holder.levelContentContainer.viewTreeObserver.addOnPreDrawListener(
            object : android.view.ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    holder.levelContentContainer.viewTreeObserver.removeOnPreDrawListener(this)
                    
                    val circleTop = holder.cardView.top
                    val circleHeight = holder.cardView.height
                    val circleCenter = circleTop + (circleHeight / 2)
                    
                    val containerTop = holder.levelContentContainer.top
                    val itemCenter = holder.itemView.height / 2
                    
                    // Calculate offset: where circle center is relative to item center
                    val offset = (containerTop + circleCenter) - itemCenter
                    holder.viewBranchLine.translationY = offset.toFloat()
                    
                    DebugLogger.debug("  Branch vertical align: offset=${offset}px to match circle center")
                    
                    return true
                }
            }
        )
        
        DebugLogger.debug("  Branch: width=${branchWidth}px + 3px overlap = ${branchWidthWithOverlap}px, translationX=${holder.viewBranchLine.translationX}")
    }

    private fun setupProgressLines(
        holder: LevelViewHolder,
        level: Level,
        position: Int,
        context: Context
    ) {
        DebugLogger.debug("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        DebugLogger.debug("Progress Line Setup - Position $position")
        DebugLogger.debug("Level: ${level.title} (ID: ${level.levelId})")
        
        // First item (bottom-most after reversal) has no line above it
        if (position == 0) {
            holder.viewProgressLine.visibility = View.GONE
            DebugLogger.debug("Action: HIDDEN (first level)")
        } else {
            holder.viewProgressLine.visibility = View.VISIBLE
            
            // Determine line style based on current level's state
            val (lineDrawable, shouldAnimate) = when {
                level.isCompleted -> {
                    Pair(R.drawable.progress_line_completed, false)
                }
                level.isUnlocked -> {
                    Pair(R.drawable.progress_line_active, true)
                }
                else -> {
                    Pair(R.drawable.progress_line_locked, false)
                }
            }
            
            // Apply style to both vertical and horizontal lines
            holder.viewProgressLine.setBackgroundResource(lineDrawable)
            if (holder.viewBranchLine.visibility == View.VISIBLE) {
                holder.viewBranchLine.setBackgroundResource(lineDrawable)
            }
            
            // Handle animation - start both at the same time to sync
            if (shouldAnimate) {
                // Clear any existing animations first
                holder.viewProgressLine.clearAnimation()
                holder.viewBranchLine.clearAnimation()
                
                // Load and start glow animations together (no scaling, just opacity)
                val verticalGlow = android.view.animation.AnimationUtils.loadAnimation(
                    context, R.anim.progress_line_glow
                )
                
                holder.viewProgressLine.startAnimation(verticalGlow)
                
                // Start branch animation immediately after (synchronized)
                if (holder.viewBranchLine.visibility == View.VISIBLE) {
                    val branchGlow = android.view.animation.AnimationUtils.loadAnimation(
                        context, R.anim.progress_line_glow
                    )
                    holder.viewBranchLine.startAnimation(branchGlow)
                }
                
                DebugLogger.debug("Action: GLOWING (active level)")
            } else {
                holder.viewProgressLine.clearAnimation()
                holder.viewBranchLine.clearAnimation()
                DebugLogger.debug("Action: STATIC (${if (level.isCompleted) "completed" else "locked"})")
            }
        }
        DebugLogger.debug("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    override fun getItemCount(): Int = levels.size

    fun updateLevels(newLevels: List<Level>) {
        // Reverse so Level 1 is at bottom, highest level at top
        levels = newLevels.reversed()
        notifyDataSetChanged()
    }
}