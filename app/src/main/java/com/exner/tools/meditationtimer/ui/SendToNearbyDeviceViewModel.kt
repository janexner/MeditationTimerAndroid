package com.exner.tools.meditationtimer.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class ProcessStateConstants {
    AWAITING_PERMISSIONS,
    PERMISSIONS_DENIED,
    AWAITING_DISCOVERY,
    DISCOVERED,
    CONNECTED,
    RECEIVING,
    DISCONNECTED,
    ERROR
}
data class ProcessState(
    val currentState: ProcessStateConstants = ProcessStateConstants.AWAITING_PERMISSIONS
)

@HiltViewModel
class SendToNearbyDeviceViewModel @Inject constructor(
    repository: MeditationTimerDataRepository
): ViewModel() {

    private val _processStateFlow = MutableStateFlow(ProcessState())
    val processStateFlow: StateFlow<ProcessState> = _processStateFlow.asStateFlow()

    fun setCurrentState(newState: ProcessStateConstants) {
        _processStateFlow.value = ProcessState(newState)
    }
}