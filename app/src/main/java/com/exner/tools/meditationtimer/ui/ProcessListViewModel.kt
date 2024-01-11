package com.exner.tools.meditationtimer.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataIdAndName
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
class ProcessListUiState(
    private val categoryId: Long,
    private val categoryName: String,
    private val processList: List<MeditationTimerProcess>,
    private val idsAndNamesList: List<MeditationTimerDataIdAndName>
) {
    fun getCategoryId(): Long {
        return categoryId
    }

    fun getCategoryName(): String {
        return categoryName
    }

    fun updateCategory(newCategoryId: Long, newCategoryName: String) {

    }

    fun getListOfCategoryIdsAndNames(): List<MeditationTimerDataIdAndName> {
        return idsAndNamesList
    }

    fun getProcessList(): List<MeditationTimerProcess> {
        return processList
    }

    fun getProcessByIndex(index: Int): MeditationTimerProcess {
        return processList[index]
    }

    fun copy(
        newCategoryId: Long = categoryId,
        newCategoryName: String = categoryName,
        newProcessList: List<MeditationTimerProcess> = processList,
        newIdsAndNamesList: List<MeditationTimerDataIdAndName> = idsAndNamesList
    ) = ProcessListUiState(newCategoryId, newCategoryName, newProcessList, newIdsAndNamesList)

}

@HiltViewModel
class ProcessListViewModel @Inject constructor(
    private val repository: MeditationTimerDataRepository
): ViewModel() {

    private var _uiStateFlow: Flow<ProcessListUiState> = flow {
        emit(ProcessListUiState(
            -1L,
            "All",
            emptyList(),
            emptyList()
        ))
    }
    val uiState: StateFlow<ProcessListUiState> = _uiStateFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProcessListUiState(
            -1L,
            "All",
            emptyList(),
            emptyList()
        )
    )

    init {
        selectNewCategoryId(-1L)
    }

    fun selectNewCategoryId(newCategoryId: Long) {
        var processList: List<MeditationTimerProcess>
        viewModelScope.launch {
            if (newCategoryId == -1L) {
                processList = repository.allProcesses.last()
            } else {
                processList = repository.getAllProcessesForCategory(categoryId = newCategoryId).last()
            }
            _uiStateFlow = flow {
                emit(ProcessListUiState(
                    uiState.value.getCategoryId(),
                    uiState.value.getCategoryName(),
                    processList,
                    uiState.value.getListOfCategoryIdsAndNames()
                ))
            }
        }
    }

}