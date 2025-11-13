package com.lokixcz.optilearn

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.lokixcz.optilearn.R
import com.lokixcz.optilearn.utils.Constants
import com.lokixcz.optilearn.view.adapter.LevelAdapter
import com.lokixcz.optilearn.viewmodel.GameViewModel

class QuestMapActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressPercentage: TextView
    private lateinit var recyclerViewLevels: RecyclerView
    private lateinit var progressLoading: ProgressBar
    
    private lateinit var levelAdapter: LevelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_map)
        
        initializeViews()
        setupRecyclerView()
        setupObservers()
        setupToolbar()
        
        // Load data
        viewModel.loadAllLevels()
        viewModel.loadUserProgress()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage)
        recyclerViewLevels = findViewById(R.id.recyclerViewLevels)
        progressLoading = findViewById(R.id.progressLoading)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        levelAdapter = LevelAdapter(emptyList()) { level ->
            // Navigate to Quiz Activity
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(Constants.EXTRA_LEVEL_ID, level.levelId)
                putExtra(Constants.EXTRA_LEVEL_TITLE, level.title)
                putExtra(Constants.EXTRA_BADGE_NAME, level.badgeName)
                putExtra(Constants.EXTRA_BADGE_ICON, level.badgeIcon)
            }
            startActivity(intent)
        }
        
        recyclerViewLevels.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@QuestMapActivity,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )
            adapter = levelAdapter
            // Add spacing between items
            addItemDecoration(VerticalSpaceItemDecoration(24))
        }
    }

    private fun setupObservers() {
        // Observe levels
        viewModel.levels.observe(this) { levels ->
            levels?.let {
                levelAdapter.updateLevels(it)
                // Scroll to the bottom (Level 1) after levels are loaded
                recyclerViewLevels.post {
                    recyclerViewLevels.scrollToPosition(it.size - 1)
                }
            }
        }
        
        // Observe user progress
        viewModel.userProgress.observe(this) { progress ->
            progress?.let {
                val percentage = it.getOverallProgress()
                progressBar.progress = percentage
                tvProgressPercentage.text = "$percentage%"
            }
        }
        
        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            progressLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data when returning from quiz
        viewModel.loadAllLevels()
        viewModel.loadUserProgress()
    }
}

/**
 * ItemDecoration to add vertical spacing between RecyclerView items
 */
class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Add space to top and bottom of each item
        outRect.top = verticalSpaceHeight / 2
        outRect.bottom = verticalSpaceHeight / 2
    }
}
