package com.iatrading.mobile.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iatrading.mobile.data.model.ApiStatusResponse
import com.iatrading.mobile.data.model.NewsItem
import com.iatrading.mobile.data.model.Ticker
import com.iatrading.mobile.data.repository.Result
import com.iatrading.mobile.data.repository.TradingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TickerDetailUiState(
    val isLoading: Boolean = true,
    val ticker: Ticker? = null,
    val news: List<NewsItem> = emptyList(),
    val pendingCount: Int = 0,
    val analyzedCount: Int = 0,
    val error: String? = null,
    val isFetching: Boolean = false,
    val isAnalyzing: Boolean = false,
    val apiStatus: ApiStatusResponse? = null,
    val actionMessage: String? = null
)

@HiltViewModel
class TickerDetailViewModel @Inject constructor(
    private val repository: TradingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TickerDetailUiState())
    val uiState: StateFlow<TickerDetailUiState> = _uiState.asStateFlow()

    fun loadTicker(symbol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load ticker details
            when (val result = repository.getTicker(symbol)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(ticker = result.data)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    return@launch
                }
                is Result.Loading -> { }
            }

            // Load news
            when (val result = repository.getTickerNews(symbol)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        news = result.data.news,
                        pendingCount = result.data.pendingCount,
                        analyzedCount = result.data.analyzedCount
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

            // Load API status
            loadApiStatus()
        }
    }

    fun loadApiStatus() {
        viewModelScope.launch {
            when (val result = repository.getApiStatus()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(apiStatus = result.data)
                }
                is Result.Error -> {
                    // Silently fail - don't show errors for status checks
                    _uiState.value = _uiState.value.copy(apiStatus = null)
                }
                is Result.Loading -> { }
            }
        }
    }

    fun fetchNews(symbol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFetching = true, actionMessage = null)

            when (val result = repository.refreshTicker(symbol)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isFetching = false,
                        actionMessage = result.data
                    )
                    // Reload data after fetch
                    loadTicker(symbol)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isFetching = false,
                        actionMessage = result.message
                    )
                }
                is Result.Loading -> { }
            }
        }
    }

    fun analyzeNews(symbol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, actionMessage = null)

            when (val result = repository.analyzeTicker(symbol)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        actionMessage = result.data
                    )
                    // Reload data after analysis
                    loadTicker(symbol)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        actionMessage = result.message
                    )
                }
                is Result.Loading -> { }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }
}
