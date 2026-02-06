package com.iatrading.mobile.ui.screens.botinsights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iatrading.mobile.data.model.*
import com.iatrading.mobile.data.repository.BotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BotInsightsUiState {
    object Loading : BotInsightsUiState
    data class Success(
        val config: BotConfig?,
        val status: BotStatus?,
        val performance: BotPerformance?,
        val recentTrades: List<BotTrade>,
        val equityData: List<EquityDataPoint>
    ) : BotInsightsUiState
    data class Error(val message: String) : BotInsightsUiState
}

@HiltViewModel
class BotInsightsViewModel @Inject constructor(
    private val botRepository: BotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BotInsightsUiState>(BotInsightsUiState.Loading)
    val uiState: StateFlow<BotInsightsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadData()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadData()
            _isRefreshing.value = false
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = BotInsightsUiState.Loading

            try {
                // Load all data in parallel
                var config: BotConfig? = null
                var status: BotStatus? = null
                var performance: BotPerformance? = null
                var trades: List<BotTrade> = emptyList()
                var equity: List<EquityDataPoint> = emptyList()

                // Load config
                botRepository.getBotConfig().collect { result ->
                    result.onSuccess { config = it }
                        .onFailure { /* Log error but continue */ }
                }

                // Load status
                botRepository.getBotStatus().collect { result ->
                    result.onSuccess { status = it }
                        .onFailure { /* Log error but continue */ }
                }

                // Load performance
                botRepository.getPerformance().collect { result ->
                    result.onSuccess { performance = it }
                        .onFailure { /* Log error but continue */ }
                }

                // Load recent trades
                botRepository.getTrades(limit = 20).collect { result ->
                    result.onSuccess { trades = it.trades }
                        .onFailure { /* Log error but continue */ }
                }

                // Load equity curve
                botRepository.getEquityCurve(interval = "daily").collect { result ->
                    result.onSuccess { equity = it.dataPoints }
                        .onFailure { /* Log error but continue */ }
                }

                _uiState.value = BotInsightsUiState.Success(
                    config = config,
                    status = status,
                    performance = performance,
                    recentTrades = trades,
                    equityData = equity
                )

            } catch (e: Exception) {
                _uiState.value = BotInsightsUiState.Error(
                    message = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun loadMoreTrades() {
        // TODO: Implement pagination
    }
}
