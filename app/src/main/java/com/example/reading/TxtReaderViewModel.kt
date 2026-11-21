package com.example.reading

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.InputStream
import java.util.concurrent.Executors

data class TxtUiState(
    val isLoading: Boolean = false,
    val isReading: Boolean = false,
    val content: String? = null,
    val errorMessage: String? = null
)

class TxtReaderViewModel(application: Application) : AndroidViewModel(application) {

    // Используем LiveData вместо StateFlow
    private val _uiState = MutableLiveData(TxtUiState())
    val uiState: LiveData<TxtUiState> get() = _uiState

    // Пул потоков для фоновых задач
    private val executor = Executors.newSingleThreadExecutor()

    fun readFile(uri: Uri) {
        _uiState.value = TxtUiState(isLoading = true)

        // Отправляем задачу в фоновый поток
        executor.execute {
            try {
                val text = readTextFromUri(uri)

                // Возвращаемся в главный поток для обновления UI
                // postValue делает это безопасно
                if (text.isBlank()) {
                    _uiState.postValue(TxtUiState(errorMessage = "Файл пустой"))
                } else {
                    _uiState.postValue(TxtUiState(isReading = true, content = text))
                }
            } catch (e: Exception) {
                _uiState.postValue(TxtUiState(errorMessage = "Ошибка: ${e.message}"))
            }
        }
    }

    fun closeFile() {
        _uiState.value = TxtUiState()
    }

    private fun readTextFromUri(uri: Uri): String {
        val inputStream: InputStream = getApplication<Application>().contentResolver
            .openInputStream(uri) ?: throw Exception("Не удалось открыть файл")
        try {
            return inputStream.bufferedReader().readText()
        } finally {
            inputStream.close()
        }
    }
}