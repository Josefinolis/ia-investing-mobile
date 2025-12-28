package com.iatrading.mobile.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isRefreshing: Boolean = false
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
        }
    }

    fun refreshNews(symbol: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)

            when (repository.refreshTicker(symbol)) {
                is Result.Success -> {
                    // Reload data after refresh
                    loadTicker(symbol)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
                }
                is Result.Loading -> { }
            }
        }
    }
}
