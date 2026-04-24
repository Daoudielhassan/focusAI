package com.focus.mob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focus.mob.data.SessionRecord
import com.focus.mob.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(private val repository: SessionRepository) : ViewModel() {

    // ─── StateFlow replaces LiveData ──────────────────────────────
    private val _totalMinutes = MutableStateFlow(0)
    val totalMinutes: StateFlow<Int> = _totalMinutes.asStateFlow()

    private val _sessions = MutableStateFlow<List<SessionRecord>>(emptyList())
    val sessions: StateFlow<List<SessionRecord>> = _sessions.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _todayMinutes = MutableStateFlow(0)
    val todayMinutes: StateFlow<Int> = _todayMinutes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ─── Load stats from Room ─────────────────────────────────────
    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _totalMinutes.value = repository.getTotalFocusTime()
                val all = repository.getAllSessions()
                _sessions.value = all
                _sessionCount.value = all.size
                _todayMinutes.value = repository.getTodayFocusTime(getStartOfDayMillis())
                Timber.d("Stats loaded: ${all.size} sessions, ${_totalMinutes.value}min total, ${_todayMinutes.value}min today")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load stats")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ─── Save a completed session ─────────────────────────────────
    fun saveSession(minutes: Int, mood: String) {
        if (minutes <= 0) {
            Timber.w("Ignored session save — duration is 0")
            return
        }
        viewModelScope.launch {
            val record = SessionRecord(
                durationMinutes = minutes,
                timestamp = System.currentTimeMillis(),
                moodFeedback = mood,
                goal = "Focus Session",
                ambiance = "Lumina Radio"
            )
            repository.insertSession(record)
            Timber.i("Session saved: ${minutes}min, mood=$mood")
            loadStats()
        }
    }

    // ─── Reset all data ───────────────────────────────────────────
    fun resetData() {
        viewModelScope.launch {
            repository.deleteAllSessions()
            Timber.w("All sessions deleted")
            loadStats()
        }
    }

    private fun getStartOfDayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
