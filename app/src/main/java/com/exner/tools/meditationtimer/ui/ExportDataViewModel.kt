package com.exner.tools.meditationtimer.ui

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcess
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerProcessCategory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportDataViewModel @Inject constructor(
    val repository: MeditationTimerDataRepository
) : ViewModel() {

    val allProcesses = repository.observeProcesses
    val allCategories = repository.observeCategories

    @OptIn(ExperimentalStdlibApi::class)
    fun commitExport(context: Context) {
        viewModelScope.launch {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapterProcesses: JsonAdapter<List<MeditationTimerProcess>> =
                moshi.adapter<List<MeditationTimerProcess>>()
            val processes = repository.getAllProcesses()
            val jsonProcesses: String = jsonAdapterProcesses.toJson(processes)
            val jsonAdapterCategories: JsonAdapter<List<MeditationTimerProcessCategory>> =
                moshi.adapter<List<MeditationTimerProcessCategory>>()
            val categories = repository.getAllCategories()
            val jsonCategories: String = jsonAdapterCategories.toJson(categories)
            val json = "[{\"categories\": $jsonCategories},{\"processes\": $jsonProcesses]"
            // now save
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "meditation-timer-export")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri).use { stream ->
                    stream?.write(json.encodeToByteArray())
                }
            }
        }
    }
}