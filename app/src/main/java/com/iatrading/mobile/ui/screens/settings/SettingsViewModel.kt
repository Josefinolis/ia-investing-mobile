package com.iatrading.mobile.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iatrading.mobile.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val currentUrl: String = "",
    val inputUrl: String = "",
    val defaultUrl: String = "",
    val isCustomUrlSet: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val defaultUrl = settingsRepository.getDefaultBaseUrl()
            _uiState.value = _uiState.value.copy(defaultUrl = defaultUrl)

            // Combine both flows to update UI state
            combine(
                settingsRepository.apiBaseUrl,
                settingsRepository.isCustomUrlSet
            ) { url, isCustom ->
                _uiState.value = _uiState.value.copy(
                    currentUrl = url,
                    inputUrl = if (isCustom) url else "",
                    isCustomUrlSet = isCustom
                )
            }.collect {}
        }
    }

    fun updateInputUrl(url: String) {
        _uiState.value = _uiState.value.copy(
            inputUrl = url,
            error = null,
            saveSuccess = false
        )
    }

    fun saveUrl() {
        val url = _uiState.value.inputUrl.trim()

        // Validate URL format
        if (url.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "URL cannot be empty"
            )
            return
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            _uiState.value = _uiState.value.copy(
                error = "URL must start with http:// or https://"
            )
            return
        }

        // Remove trailing slashes
        val cleanedUrl = url.trimEnd('/')

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                settingsRepository.setApiBaseUrl(cleanedUrl)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save: ${e.message}"
                )
            }
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                settingsRepository.resetToDefaultUrl()
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    inputUrl = "",
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to reset: ${e.message}"
                )
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
