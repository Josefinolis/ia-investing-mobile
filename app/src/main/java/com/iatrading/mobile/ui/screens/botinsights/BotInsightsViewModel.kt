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
        val allBots: List<BotStatusDetail>,
        val selectedBot: BotStatusDetail?,
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
            android.util.Log.d("BotInsightsViewModel", "Starting data load...")
            _uiState.value = BotInsightsUiState.Loading

            try {
                // Load all data in parallel
                var allBots: List<BotStatusDetail> = emptyList()
                var selectedBot: BotStatusDetail? = null
                var performance: BotPerformance? = null
                var trades: List<BotTrade> = emptyList()
                var equity: List<EquityDataPoint> = emptyList()

                // Load all bot statuses (includes configuration for each bot)
                botRepository.getAllBotStatus().collect { result ->
                    result.onSuccess {
                        android.util.Log.d("BotInsightsViewModel", "Got ${it.bots.size} bots from repository")
                        allBots = it.bots
                        // Select first bot by default
                        selectedBot = it.bots.firstOrNull()
                        android.util.Log.d("BotInsightsViewModel", "Selected bot: ${selectedBot?.sessionId}, hasConfig: ${selectedBot?.config != null}")
                    }
                    .onFailure {
                        android.util.Log.e("BotInsightsViewModel", "Failed to load bot status", it)
                    }
                }

                // Load performance
                botRepository.getPerformance().collect { result ->
                    result.onSuccess {
                        android.util.Log.d("BotInsightsViewModel", "Got performance data")
                        performance = it
                    }
                    .onFailure {
                        android.util.Log.e("BotInsightsViewModel", "Failed to load performance", it)
                    }
                }

                // Load recent trades
                botRepository.getTrades(limit = 20).collect { result ->
                    result.onSuccess {
                        android.util.Log.d("BotInsightsViewModel", "Got ${it.trades.size} trades")
                        trades = it.trades
                    }
                    .onFailure {
                        android.util.Log.e("BotInsightsViewModel", "Failed to load trades", it)
                    }
                }

                // Load equity curve
                botRepository.getEquityCurve(interval = "daily").collect { result ->
                    result.onSuccess {
                        android.util.Log.d("BotInsightsViewModel", "Got ${it.dataPoints.size} equity points")
                        equity = it.dataPoints
                    }
                    .onFailure {
                        android.util.Log.e("BotInsightsViewModel", "Failed to load equity curve", it)
                    }
                }

                android.util.Log.d("BotInsightsViewModel", "Setting UI state to Success with ${allBots.size} bots")
                _uiState.value = BotInsightsUiState.Success(
                    allBots = allBots,
                    selectedBot = selectedBot,
                    performance = performance,
                    recentTrades = trades,
                    equityData = equity
                )

            } catch (e: Exception) {
                android.util.Log.e("BotInsightsViewModel", "Exception in loadData", e)
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
