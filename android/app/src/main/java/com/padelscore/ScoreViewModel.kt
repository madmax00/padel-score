package com.padelscore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScoreViewModel : ViewModel() {
    private val _match = MutableStateFlow(PadelMatch())
    val match: StateFlow<PadelMatch> = _match.asStateFlow()

    fun addMyPoint() {
        _match.value = ScoreEngine.addPoint(_match.value, isMyPoint = true)
    }

    fun addOppPoint() {
        _match.value = ScoreEngine.addPoint(_match.value, isMyPoint = false)
    }

    fun undo() {
        _match.value = ScoreEngine.undo(_match.value)
    }

    fun reset() {
        _match.value = PadelMatch()
    }

    // Called from SAPService when watch sends a command
    fun handleWatchCommand(action: String) {
        when (action) {
            "point_me" -> addMyPoint()
            "point_opp" -> addOppPoint()
            "undo" -> undo()
        }
    }
}
