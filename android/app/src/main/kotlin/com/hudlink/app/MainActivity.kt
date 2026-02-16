package com.hudlink.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hudlink.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val logAdapter = LogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // Auto-start collecting on launch
        viewModel.startCollecting()
    }

    private fun setupRecyclerView() {
        binding.recyclerLogs.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true // New items appear at bottom
            }
        }
    }

    private fun setupButtons() {
        binding.buttonToggle.setOnClickListener {
            if (viewModel.isCollecting.value) {
                viewModel.stopCollecting()
            } else {
                viewModel.startCollecting()
            }
        }

        binding.buttonClear.setOnClickListener {
            viewModel.clearLogs()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.logEntries.collect { entries ->
                        logAdapter.submitList(entries) {
                            // Scroll to bottom when new entries added
                            if (entries.isNotEmpty()) {
                                binding.recyclerLogs.scrollToPosition(entries.size - 1)
                            }
                        }
                    }
                }

                launch {
                    viewModel.isCollecting.collect { isCollecting ->
                        binding.buttonToggle.text = if (isCollecting) "Stop" else "Start"
                    }
                }
            }
        }
    }
}
