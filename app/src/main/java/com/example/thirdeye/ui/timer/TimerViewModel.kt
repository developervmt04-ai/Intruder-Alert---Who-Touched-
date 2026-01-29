package com.example.thirdeye.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val totalMillis = 20 * 60 * 1000L
    private val _millisLeft = MutableStateFlow(totalMillis)
    val millisLeft = _millisLeft.asStateFlow()

    private var jobRunning = false

    init {
        startTimer()
    }

    private fun startTimer() {
        if (jobRunning) return
        jobRunning = true

        viewModelScope.launch {
            while (_millisLeft.value > 0) {
                delay(10)
                _millisLeft.value -= 10
            }
            jobRunning = false
        }
    }

    fun resetTimer() {
        _millisLeft.value = totalMillis
        jobRunning = false
        startTimer()
    }
}
