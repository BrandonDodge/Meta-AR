package com.hudlink.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hudlink.core.data.repository.HealthRepository
import com.hudlink.core.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _logEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val logEntries: StateFlow<List<LogEntry>> = _logEntries.asStateFlow()

    private val _isCollecting = MutableStateFlow(false)
    val isCollecting: StateFlow<Boolean> = _isCollecting.asStateFlow()

    private val maxLogEntries = 100
    private val collectionJobs = mutableListOf<Job>()

    fun startCollecting() {
        if (_isCollecting.value) return
        _isCollecting.value = true

        // Collect heart rate data
        collectionJobs += viewModelScope.launch {
            healthRepository.observeHeartRate().collect { data ->
                addLogEntry(LogEntry.fromHeartRate(data))
            }
        }

        // Collect GPS data
        collectionJobs += viewModelScope.launch {
            locationRepository.observeGps().collect { data ->
                addLogEntry(LogEntry.fromGps(data))
            }
        }

        // Collect heading data
        collectionJobs += viewModelScope.launch {
            locationRepository.observeHeading().collect { data ->
                addLogEntry(LogEntry.fromHeading(data))
            }
        }
    }

    fun stopCollecting() {
        _isCollecting.value = false
        collectionJobs.forEach { it.cancel() }
        collectionJobs.clear()
    }

    fun clearLogs() {
        _logEntries.value = emptyList()
    }

    private fun addLogEntry(entry: LogEntry) {
        _logEntries.value = (_logEntries.value + entry).takeLast(maxLogEntries)
    }
}
