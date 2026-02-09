package com.iatrading.mobile.data.model

import com.google.gson.annotations.SerializedName

/**
 * Ticker with sentiment data
 */
data class Ticker(
    val id: Int,
    val ticker: String,
    val name: String?,
    @SerializedName("added_at") val addedAt: String,
    @SerializedName("is_active") val isActive: Boolean,
    val sentiment: TickerSentiment?
)

/**
 * Aggregated sentiment for a ticker
 */
data class TickerSentiment(
    val ticker: String,
    val score: Double,
    @SerializedName("normalized_score") val normalizedScore: Double,
    @SerializedName("sentiment_label") val sentimentLabel: String?,
    val signal: String?,
    val confidence: Double,
    @SerializedName("positive_count") val positiveCount: Int,
    @SerializedName("negative_count") val negativeCount: Int,
    @SerializedName("neutral_count") val neutralCount: Int,
    @SerializedName("total_analyzed") val totalAnalyzed: Int,
    @SerializedName("total_pending") val totalPending: Int,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * News item with analysis
 */
data class NewsItem(
    val id: Int,
    val ticker: String,
    val title: String,
    val summary: String,
    @SerializedName("published_date") val publishedDate: String?,
    val source: String?,
    val url: String?,
    @SerializedName("relevance_score") val relevanceScore: Double?,
    val status: String,
    val sentiment: String?,
    val justification: String?,
    @SerializedName("fetched_at") val fetchedAt: String,
    @SerializedName("analyzed_at") val analyzedAt: String?
)

/**
 * Request to create a ticker
 */
data class TickerCreate(
    val ticker: String,
    val name: String? = null
)

/**
 * Response for list of tickers
 */
data class TickerListResponse(
    val tickers: List<Ticker>,
    val count: Int
)

/**
 * Response for list of news
 */
data class NewsListResponse(
    val news: List<NewsItem>,
    val count: Int,
    @SerializedName("pending_count") val pendingCount: Int,
    @SerializedName("analyzed_count") val analyzedCount: Int
)

/**
 * Status of a single API service
 */
data class ApiServiceStatus(
    val available: Boolean,
    @SerializedName("cooldown_until") val cooldownUntil: String?,
    val message: String?
)

/**
 * Overall API status response
 */
data class ApiStatusResponse(
    val gemini: ApiServiceStatus,
    @SerializedName("alpha_vantage") val alphaVantage: ApiServiceStatus
)

// === Bot Models ===

/**
 * Bot configuration detail (embedded in status)
 */
data class BotConfigDetail(
    val broker: String? = null,
    @SerializedName("trading_mode") val tradingMode: String? = null,
    val interval: String? = null,
    @SerializedName("ml_model") val mlModel: String? = null,
    @SerializedName("confidence_threshold") val confidenceThreshold: String? = null,
    @SerializedName("risk_config") val riskConfig: BotRiskConfig? = null,
    @SerializedName("initial_balance") val initialBalance: String? = null
)

/**
 * Bot risk configuration (String values for precision)
 */
data class BotRiskConfig(
    @SerializedName("max_position_pct") val maxPositionPct: String? = null,
    @SerializedName("max_daily_loss_pct") val maxDailyLossPct: String? = null,
    @SerializedName("stop_loss_pct") val stopLossPct: String? = null
)

/**
 * Trade record from bot
 */
data class BotTrade(
    val id: Int,
    val symbol: String,
    val side: String, // BUY or SELL
    val quantity: String,
    val price: String,
    val timestamp: String,
    val pnl: String?,
    @SerializedName("pnl_percent") val pnlPercent: String?,
    @SerializedName("is_paper") val isPaper: Boolean,
    val strategy: String?,
    val notes: String?
)

/**
 * Trade list response
 */
data class BotTradesResponse(
    val trades: List<BotTrade>,
    val total: Int,
    val page: Int,
    @SerializedName("page_size") val pageSize: Int
)

/**
 * Performance metrics
 */
data class BotPerformance(
    @SerializedName("total_return") val totalReturn: String,
    @SerializedName("return_percent") val returnPercent: String,
    @SerializedName("total_trades") val totalTrades: Int,
    @SerializedName("winning_trades") val winningTrades: Int,
    @SerializedName("losing_trades") val losingTrades: Int,
    @SerializedName("win_rate") val winRate: String,
    @SerializedName("profit_factor") val profitFactor: String,
    @SerializedName("max_drawdown") val maxDrawdown: String,
    @SerializedName("sharpe_ratio") val sharpeRatio: String,
    @SerializedName("average_win") val averageWin: String,
    @SerializedName("average_loss") val averageLoss: String,
    @SerializedName("start_balance") val startBalance: String,
    @SerializedName("current_balance") val currentBalance: String,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?
)

/**
 * Equity curve data point
 */
data class EquityDataPoint(
    val timestamp: String,
    val balance: String,
    val equity: String,
    @SerializedName("daily_pnl") val dailyPnl: String?,
    @SerializedName("open_positions") val openPositions: Int
)

/**
 * Equity curve response
 */
data class BotEquityResponse(
    @SerializedName("data_points") val dataPoints: List<EquityDataPoint>,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    val interval: String
)

/**
 * Bot status detail (single bot in multi-bot response)
 */
data class BotStatusDetail(
    @SerializedName("session_id") val sessionId: String,
    val symbol: String? = null,
    val strategy: String? = null,
    @SerializedName("is_running") val isRunning: Boolean,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("uptime_seconds") val uptimeSeconds: Long? = null,
    @SerializedName("kill_switch_active") val killSwitchActive: Boolean = false,
    @SerializedName("last_signal_time") val lastSignalTime: String? = null,
    @SerializedName("last_signal_type") val lastSignalType: String? = null,
    @SerializedName("last_heartbeat") val lastHeartbeat: String,
    val config: BotConfigDetail? = null
)

/**
 * Multi-bot status response (all bots)
 */
data class MultiBotStatusResponse(
    val bots: List<BotStatusDetail>,
    val total: Int,
    val timestamp: String
)

/**
 * Legacy bot status (kept for backwards compatibility)
 * Use first bot from MultiBotStatusResponse for single-bot scenarios
 */
@Deprecated("Use MultiBotStatusResponse and BotStatusDetail instead")
data class BotStatus(
    @SerializedName("is_running") val isRunning: Boolean,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("uptime_seconds") val uptimeSeconds: Long?,
    @SerializedName("kill_switch_active") val killSwitchActive: Boolean,
    @SerializedName("last_signal_time") val lastSignalTime: String?,
    @SerializedName("last_signal_type") val lastSignalType: String?,
    @SerializedName("current_positions") val currentPositions: List<BotPosition>,
    val account: BotAccount?
)

data class BotPosition(
    val symbol: String,
    val quantity: String,
    @SerializedName("entry_price") val entryPrice: String,
    @SerializedName("current_price") val currentPrice: String,
    @SerializedName("unrealized_pnl") val unrealizedPnl: String,
    @SerializedName("unrealized_pnl_percent") val unrealizedPnlPercent: String,
    @SerializedName("opened_at") val openedAt: String
)

data class BotAccount(
    val balance: String,
    val equity: String,
    @SerializedName("buying_power") val buyingPower: String?
)
