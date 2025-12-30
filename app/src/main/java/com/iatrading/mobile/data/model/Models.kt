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
