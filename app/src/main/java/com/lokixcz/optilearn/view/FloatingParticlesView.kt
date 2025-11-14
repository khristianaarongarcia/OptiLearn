package com.lokixcz.optilearn.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.random.Random

class FloatingParticlesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particleCount = 30
    private var animator: ValueAnimator? = null

    data class Particle(
        var x: Float,
        var y: Float,
        val radius: Float,
        val speed: Float,
        val alpha: Int,
        val color: Int
    )

    init {
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initParticles()
        startAnimation()
    }

    private fun initParticles() {
        particles.clear()
        val colors = intArrayOf(
            0xFFFFFFFF.toInt(), // White
            0xFFB2EBF2.toInt(), // Light cyan
            0xFF80DEEA.toInt(), // Soft cyan
            0xFF4DD0E1.toInt()  // Sky blue
        )

        repeat(particleCount) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    radius = Random.nextFloat() * 4f + 2f,
                    speed = Random.nextFloat() * 2f + 0.5f,
                    alpha = Random.nextInt(30, 100),
                    color = colors[Random.nextInt(colors.size)]
                )
            )
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = Long.MAX_VALUE
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    private fun updateParticles() {
        particles.forEach { particle ->
            particle.y -= particle.speed
            
            // Reset particle when it goes off screen
            if (particle.y + particle.radius < 0) {
                particle.y = height.toFloat() + particle.radius
                particle.x = Random.nextFloat() * width
            }
            
            // Add subtle horizontal drift
            particle.x += (Random.nextFloat() - 0.5f) * 0.5f
            
            // Keep within bounds
            if (particle.x < 0) particle.x = width.toFloat()
            if (particle.x > width) particle.x = 0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        particles.forEach { particle ->
            paint.color = particle.color
            paint.alpha = particle.alpha
            canvas.drawCircle(particle.x, particle.y, particle.radius, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
