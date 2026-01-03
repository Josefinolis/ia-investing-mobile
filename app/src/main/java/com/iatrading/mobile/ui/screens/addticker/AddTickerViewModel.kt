package com.iatrading.mobile.ui.screens.addticker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iatrading.mobile.data.repository.Result
import com.iatrading.mobile.data.repository.TradingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTickerUiState(
    val ticker: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AddTickerViewModel @Inject constructor(
    private val repository: TradingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTickerUiState())
    val uiState: StateFlow<AddTickerUiState> = _uiState.asStateFlow()

    fun updateTicker(ticker: String) {
        _uiState.value = _uiState.value.copy(
            ticker = ticker.uppercase().filter { it.isLetterOrDigit() },
            error = null
        )
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun addTicker() {
        val ticker = _uiState.value.ticker.trim()
        val name = _uiState.value.name.trim().ifEmpty { null }

        if (ticker.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Ticker symbol is required")
            return
        }

        if (ticker.length < 1 || ticker.length > 10) {
            _uiState.value = _uiState.value.copy(error = "Ticker must be 1-10 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = repository.addTicker(ticker, name)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> { }
            }
        }
    }
}
