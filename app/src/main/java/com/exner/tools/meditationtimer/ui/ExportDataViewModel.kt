package com.exner.tools.meditationtimer.ui

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.meditationtimer.data.persistence.MeditationTimerDataRepository
import com.exner.tools.meditationtimer.data.persistence.tools.RootData
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
    fun commitExport(
        context: Context,
        successCallback: () -> Unit
    ) {
        viewModelScope.launch {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val processes = repository.getAllProcesses()
            val categories = repository.getAllCategories()
            val data = RootData(processes, categories)
            val jsonAdapter: JsonAdapter<RootData> = moshi.adapter<RootData>()
            val json = jsonAdapter.toJson(data)
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
            successCallback()
        }
    }
}