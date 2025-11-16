package com.lokixcz.optilearn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Transition screen shown before Quest Map loads
 * Animates logo: slide in from left → enlarge → slide out to right
 */
class QuestMapTransitionActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var tvLoading: TextView
    
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        private const val SLIDE_IN_DURATION = 500L
        private const val ENLARGE_DURATION = 400L
        private const val MINIMUM_DISPLAY_TIME = 1500L
        private const val SLIDE_OUT_DURATION = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_map_transition)

        ivLogo = findViewById(R.id.ivTransitionLogo)
        tvLoading = findViewById(R.id.tvLoadingText)

        // Start animations
        startTransitionSequence()
    }

    private fun startTransitionSequence() {
        // Phase 1: Slide in from left to center
        slideInFromLeft()
    }

    private fun slideInFromLeft() {
        // Start logo off-screen to the left
        ivLogo.translationX = -1000f
        ivLogo.alpha = 1f
        ivLogo.scaleX = 0.8f
        ivLogo.scaleY = 0.8f

        // Animate slide in
        ivLogo.animate()
            .translationX(0f)
            .setDuration(SLIDE_IN_DURATION)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Phase 2: Enlarge effect
                enlargeLogo()
            }
            .start()

        // Fade in loading text
        tvLoading.alpha = 0f
        tvLoading.animate()
            .alpha(1f)
            .setDuration(SLIDE_IN_DURATION)
            .setStartDelay(200L)
            .start()
    }

    private fun enlargeLogo() {
        // Enlarge from 0.8 to 1.2 then settle to 1.0
        ivLogo.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(ENLARGE_DURATION / 2)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                ivLogo.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(ENLARGE_DURATION / 2)
                    .withEndAction {
                        // Phase 3: Wait minimum time then slide out
                        waitAndSlideOut()
                    }
                    .start()
            }
            .start()
    }

    private fun waitAndSlideOut() {
        // Calculate remaining time to meet minimum display duration
        val elapsedTime = SLIDE_IN_DURATION + ENLARGE_DURATION
        val remainingTime = (MINIMUM_DISPLAY_TIME - elapsedTime).coerceAtLeast(0)

        handler.postDelayed({
            slideOutToRight()
        }, remainingTime)
    }

    private fun slideOutToRight() {
        // Fade out loading text
        tvLoading.animate()
            .alpha(0f)
            .setDuration(300L)
            .start()

        // Slide logo out to the right
        ivLogo.animate()
            .translationX(1000f)
            .alpha(0f)
            .setDuration(SLIDE_OUT_DURATION)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Launch Quest Map
                launchQuestMap()
            }
            .start()
    }

    private fun launchQuestMap() {
        val intent = Intent(this, QuestMapActivity::class.java)
        startActivity(intent)
        
        // Smooth transition without default animation
        overridePendingTransition(0, 0)
        
        // Finish this transition activity
        finish()
    }

    @Suppress("DEPRECATION", "MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Disable back button during transition
        // User should not be able to interrupt the animation
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
