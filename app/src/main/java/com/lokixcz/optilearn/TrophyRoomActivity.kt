package com.lokixcz.optilearn

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.lokixcz.optilearn.model.Level
import com.lokixcz.optilearn.view.adapter.BadgeAdapter
import com.lokixcz.optilearn.viewmodel.GameViewModel

class TrophyRoomActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvBadgesEarned: TextView
    private lateinit var tvCompletionPercentage: TextView
    private lateinit var tvPerfectScores: TextView
    private lateinit var tvOptiHints: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnShowAll: MaterialButton
    private lateinit var btnShowEarned: MaterialButton
    private lateinit var btnShowLocked: MaterialButton
    private lateinit var rvBadges: RecyclerView
    
    private lateinit var badgeAdapter: BadgeAdapter
    private var allBadges: List<Level> = emptyList()
    private var currentFilter: BadgeFilter = BadgeFilter.ALL

    enum class BadgeFilter {
        ALL, EARNED, LOCKED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trophy_room)
        
        initializeViews()
        setupToolbar()
        setupRecyclerView()
        setupFilterButtons()
        observeData()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        tvBadgesEarned = findViewById(R.id.tvBadgesEarned)
        tvCompletionPercentage = findViewById(R.id.tvCompletionPercentage)
        tvPerfectScores = findViewById(R.id.tvPerfectScores)
        tvOptiHints = findViewById(R.id.tvOptiHints)
        progressBar = findViewById(R.id.progressBar)
        btnShowAll = findViewById(R.id.btnShowAll)
        btnShowEarned = findViewById(R.id.btnShowEarned)
        btnShowLocked = findViewById(R.id.btnShowLocked)
        rvBadges = findViewById(R.id.rvBadges)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        badgeAdapter = BadgeAdapter(emptyList())
        rvBadges.apply {
            layoutManager = GridLayoutManager(this@TrophyRoomActivity, 2)
            adapter = badgeAdapter
        }
    }

    private fun setupFilterButtons() {
        // Set initial state - All Badges selected
        updateFilterButtonStates(BadgeFilter.ALL)

        btnShowAll.setOnClickListener {
            currentFilter = BadgeFilter.ALL
            updateFilterButtonStates(BadgeFilter.ALL)
            filterBadges()
        }

        btnShowEarned.setOnClickListener {
            currentFilter = BadgeFilter.EARNED
            updateFilterButtonStates(BadgeFilter.EARNED)
            filterBadges()
        }

        btnShowLocked.setOnClickListener {
            currentFilter = BadgeFilter.LOCKED
            updateFilterButtonStates(BadgeFilter.LOCKED)
            filterBadges()
        }
    }

    private fun updateFilterButtonStates(selectedFilter: BadgeFilter) {
        // Reset all buttons to outlined style
        btnShowAll.setBackgroundColor(getColor(android.R.color.transparent))
        btnShowEarned.setBackgroundColor(getColor(android.R.color.transparent))
        btnShowLocked.setBackgroundColor(getColor(android.R.color.transparent))

        // Highlight selected button
        when (selectedFilter) {
            BadgeFilter.ALL -> {
                btnShowAll.setBackgroundColor(getColor(R.color.primary))
                btnShowAll.setTextColor(getColor(R.color.white))
                btnShowEarned.setTextColor(getColor(R.color.primary))
                btnShowLocked.setTextColor(getColor(R.color.primary))
            }
            BadgeFilter.EARNED -> {
                btnShowEarned.setBackgroundColor(getColor(R.color.primary))
                btnShowEarned.setTextColor(getColor(R.color.white))
                btnShowAll.setTextColor(getColor(R.color.primary))
                btnShowLocked.setTextColor(getColor(R.color.primary))
            }
            BadgeFilter.LOCKED -> {
                btnShowLocked.setBackgroundColor(getColor(R.color.primary))
                btnShowLocked.setTextColor(getColor(R.color.white))
                btnShowAll.setTextColor(getColor(R.color.primary))
                btnShowEarned.setTextColor(getColor(R.color.primary))
            }
        }
    }

    private fun observeData() {
        // Observe all levels (badge data)
        viewModel.levels.observe(this) { levels ->
            allBadges = levels
            filterBadges()
            updateStatistics(levels)
        }
        
        // Observe user progress for OptiHints
        viewModel.userProgress.observe(this) { progress ->
            progress?.let {
                tvOptiHints.text = it.optiHints.toString()
            }
        }
        
        // Load user progress
        viewModel.loadUserProgress()
    }

    private fun filterBadges() {
        val filteredBadges = when (currentFilter) {
            BadgeFilter.ALL -> allBadges
            BadgeFilter.EARNED -> allBadges.filter { it.isCompleted }
            BadgeFilter.LOCKED -> allBadges.filter { !it.isCompleted }
        }
        badgeAdapter.updateBadges(filteredBadges)
    }

    private fun updateStatistics(levels: List<Level>) {
        val earnedBadges = levels.count { it.isCompleted }
        val totalBadges = levels.size
        val completionPercentage = if (totalBadges > 0) {
            (earnedBadges * 100) / totalBadges
        } else {
            0
        }
        
        // Count perfect scores (100% completion)
        val perfectScores = levels.count { it.highScore == 100 }

        // Update UI
        tvBadgesEarned.text = earnedBadges.toString()
        tvCompletionPercentage.text = "$completionPercentage%"
        tvPerfectScores.text = perfectScores.toString()
        progressBar.progress = earnedBadges
        progressBar.max = totalBadges
    }
}
