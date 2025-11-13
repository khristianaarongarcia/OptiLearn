package com.lokixcz.optilearn.managers

import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

/**
 * Helper class to manage Lottie animations with proper lifecycle handling
 */
object AnimationManager {

    /**
     * Play a one-shot animation and optionally hide the view when done
     */
    fun playOnce(
        animationView: LottieAnimationView,
        hideOnComplete: Boolean = true,
        onComplete: (() -> Unit)? = null
    ) {
        animationView.visibility = View.VISIBLE
        animationView.repeatCount = 0
        animationView.playAnimation()

        if (hideOnComplete || onComplete != null) {
            animationView.addAnimatorUpdateListener { animation ->
                if (animation.animatedFraction >= 1.0f) {
                    if (hideOnComplete) {
                        animationView.visibility = View.GONE
                    }
                    onComplete?.invoke()
                    animationView.removeAllUpdateListeners()
                }
            }
        }
    }

    /**
     * Play a looping animation
     */
    fun playLoop(animationView: LottieAnimationView) {
        animationView.visibility = View.VISIBLE
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.playAnimation()
    }

    /**
     * Stop and hide an animation
     */
    fun stop(animationView: LottieAnimationView, hide: Boolean = true) {
        animationView.cancelAnimation()
        if (hide) {
            animationView.visibility = View.GONE
        }
    }

    /**
     * Pause an animation
     */
    fun pause(animationView: LottieAnimationView) {
        animationView.pauseAnimation()
    }

    /**
     * Resume a paused animation
     */
    fun resume(animationView: LottieAnimationView) {
        if (animationView.visibility == View.VISIBLE) {
            animationView.resumeAnimation()
        }
    }

    /**
     * Play celebration animation with confetti
     */
    fun playCelebration(animationView: LottieAnimationView, onComplete: (() -> Unit)? = null) {
        playOnce(animationView, hideOnComplete = true, onComplete = onComplete)
    }

    /**
     * Play feedback animation (correct/wrong)
     */
    fun playFeedback(
        animationView: LottieAnimationView,
        isCorrect: Boolean,
        onComplete: (() -> Unit)? = null
    ) {
        playOnce(animationView, hideOnComplete = true, onComplete = onComplete)
    }

    /**
     * Show loading animation
     */
    fun showLoading(animationView: LottieAnimationView) {
        playLoop(animationView)
    }

    /**
     * Hide loading animation
     */
    fun hideLoading(animationView: LottieAnimationView) {
        stop(animationView, hide = true)
    }
}
