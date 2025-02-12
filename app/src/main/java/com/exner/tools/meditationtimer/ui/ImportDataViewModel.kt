package com.exner.tools.meditationtimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.exner.tools.meditationtimer.data.persistence.tools.RootData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ImportStateConstants {
    IDLE,
    FILE_SELECTED,
    FILE_ANALYSED,
    IMPORT_FINISHED,
    ERROR
}

data class ImportState(
    val state: ImportStateConstants = ImportStateConstants.IDLE
)

@HiltViewModel
class ImportDataViewModel @Inject constructor(
    val repository: MeditationTimerDataRepository
) : ViewModel() {

    private val _importStateFlow = MutableStateFlow(ImportState())
    val importStateFlow: StateFlow<ImportState> = _importStateFlow

    private var _file: MutableStateFlow<PlatformFile?> = MutableStateFlow(null)
    val file: StateFlow<PlatformFile?> = _file

    private val _override: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val override: StateFlow<Boolean> = _override

    private val _listOfProcessesInFile: MutableStateFlow<List<MeditationTimerProcess>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfProcessesInFile: StateFlow<List<MeditationTimerProcess>> = _listOfProcessesInFile

    private val _listOfOldProcesses: MutableStateFlow<List<MeditationTimerProcess>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfOldProcesses: StateFlow<List<MeditationTimerProcess>> = _listOfOldProcesses
    private val _listOfNewProcesses: MutableStateFlow<List<MeditationTimerProcess>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfNewProcesses: StateFlow<List<MeditationTimerProcess>> = _listOfNewProcesses
    private val _listOfClashingProcesses: MutableStateFlow<List<MeditationTimerProcess>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfClashingProcesses: StateFlow<List<MeditationTimerProcess>> = _listOfClashingProcesses

    private val _listOfCategoriesInFile: MutableStateFlow<List<MeditationTimerProcessCategory>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfCategoriesInFile: StateFlow<List<MeditationTimerProcessCategory>> =
        _listOfCategoriesInFile

    private val _listOfOldCategories: MutableStateFlow<List<MeditationTimerProcessCategory>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfOldCategories: StateFlow<List<MeditationTimerProcessCategory>> = _listOfOldCategories
    private val _listOfNewCategories: MutableStateFlow<List<MeditationTimerProcessCategory>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfNewCategories: StateFlow<List<MeditationTimerProcessCategory>> = _listOfNewCategories
    private val _listOfClashingCategories: MutableStateFlow<List<MeditationTimerProcessCategory>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfClashingCategories: StateFlow<List<MeditationTimerProcessCategory>> =
        _listOfClashingCategories

    private val _errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _highestUidInProcessDB: MutableStateFlow<Long> = MutableStateFlow(-1)
    val highestUidInProcessDB: StateFlow<Long> = _highestUidInProcessDB
    private val _highestUidInCategoryDB: MutableStateFlow<Long> = MutableStateFlow(-1)
    val highestUidInCategoryDB: StateFlow<Long> = _highestUidInCategoryDB

    fun setOverride(override: Boolean) {
        _override.value = override
    }

    fun commitImport(
        successCallback: () -> Unit
    ) {
        if (listOfNewProcesses.value.isNotEmpty() && listOfNewCategories.value.isNotEmpty()) {
            viewModelScope.launch {
                if (override.value) {
                    repository.deleteAllCategories()
                    if (listOfCategoriesInFile.value.isNotEmpty()) {
                        listOfCategoriesInFile.value.forEach { category ->
                            repository.insertCategory(category)
                        }
                    }
                    repository.deleteAllProcesses()
                    if (listOfProcessesInFile.value.isNotEmpty()) {
                        listOfProcessesInFile.value.forEach { process ->
                            repository.insert(process)
                        }
                    }
                } else {
                    listOfNewCategories.value.forEach { category ->
                        repository.insertCategory(category)
                    }
                    listOfNewProcesses.value.forEach { process ->
                        repository.insert(process)
                    }
                }
                _importStateFlow.value = ImportState(ImportStateConstants.IMPORT_FINISHED)
                successCallback()
            }
        }
    }

    fun setFile(file: PlatformFile?) {
        if (file != null) {
            _file.value = file
            _importStateFlow.value = ImportState(ImportStateConstants.FILE_SELECTED)
            analyseFile(file)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun analyseFile(file: PlatformFile) {
        viewModelScope.launch {
            try {
                val fileContent = file.readBytes().toString(Charsets.UTF_8)
                Log.d("ImportDataVM", "File content: '$fileContent'")
                val moshi = Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
                val jsonAdapter: JsonAdapter<RootData> = moshi.adapter<RootData>()
                val data: RootData? = jsonAdapter.fromJson(fileContent)
                if (data != null) {
                    // processes
                    val newProcesses: List<MeditationTimerProcess> = data.processes
                    // compare with existing
                    _listOfProcessesInFile.value = newProcesses
                    val oldProcesses = repository.getAllProcesses()
                    val oldUids: MutableList<Long> = mutableListOf()
                    oldProcesses.forEach { oldProcess ->
                        oldUids.add(oldProcess.uid)
                        if (oldProcess.uid > highestUidInProcessDB.value) {
                            _highestUidInProcessDB.value = oldProcess.uid
                        }
                    }
                    _listOfOldProcesses.value = emptyList()
                    _listOfClashingProcesses.value = emptyList()
                    _listOfNewProcesses.value = emptyList()
                    newProcesses.forEach { newProcess ->
                        if (oldUids.contains(newProcess.uid)) {
                            // is it the same?
                            if (newProcess == repository.loadProcessById(newProcess.uid)) {
                                // it is the same. No need to import
                                val temp = listOfOldProcesses.value.toMutableList()
                                temp.add(newProcess)
                                _listOfOldProcesses.value = temp
                            } else {
                                val temp = listOfClashingProcesses.value.toMutableList()
                                temp.add(newProcess)
                                _listOfClashingProcesses.value = temp
                            }
                        } else {
                            val temp = listOfNewProcesses.value.toMutableList()
                            temp.add(newProcess)
                            _listOfNewProcesses.value = temp
                        }
                    }
                    // categories
                    val newCategories: List<MeditationTimerProcessCategory> = data.categories
                    // compare with existing
                    _listOfCategoriesInFile.value = newCategories
                    val oldCategories = repository.getAllCategories()
                    val oldCategoryUids: MutableList<Long> = mutableListOf()
                    oldCategories.forEach { oldCategory ->
                        oldCategoryUids.add(oldCategory.uid)
                        if (oldCategory.uid > highestUidInCategoryDB.value) {
                            _highestUidInCategoryDB.value = oldCategory.uid
                        }
                    }
                    _listOfOldCategories.value = emptyList()
                    _listOfClashingCategories.value = emptyList()
                    _listOfNewCategories.value = emptyList()
                    newCategories.forEach { newCategory ->
                        if (oldUids.contains(newCategory.uid)) {
                            // is it the same?
                            if (newCategory == repository.getCategoryById(newCategory.uid)) {
                                // it is the same. No need to import
                                val temp = listOfOldCategories.value.toMutableList()
                                temp.add(newCategory)
                                _listOfOldCategories.value = temp
                            } else {
                                val temp = listOfClashingCategories.value.toMutableList()
                                temp.add(newCategory)
                                _listOfClashingCategories.value = temp
                            }
                        } else {
                            val temp = listOfNewCategories.value.toMutableList()
                            temp.add(newCategory)
                            _listOfNewCategories.value = temp
                        }
                    }
                }
                // done
                _importStateFlow.value = ImportState(ImportStateConstants.FILE_ANALYSED)
            } catch (exception: Exception) {
                Log.d("ImportDataVM", "Exception: ${exception.message}")
                _errorMessage.value = exception.message.toString()
                _importStateFlow.value = ImportState(ImportStateConstants.ERROR)
            }
        }
    }
}