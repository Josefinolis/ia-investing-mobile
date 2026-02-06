package com.iatrading.mobile.data.repository

import com.iatrading.mobile.data.api.BotApi
import com.iatrading.mobile.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for bot data
 */
@Singleton
class BotRepository @Inject constructor(
    private val botApi: BotApi
) {

    /**
     * Get bot configuration
     */
    fun getBotConfig(): Flow<kotlin.Result<BotConfig>> = flow {
        try {
            val response = botApi.getBotConfig()
            if (response.isSuccessful && response.body() != null) {
                emit(kotlin.Result.success(response.body()!!))
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch bot config: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(kotlin.Result.failure(e))
        }
    }

    /**
     * Get trade history
     */
    fun getTrades(
        limit: Int = 50,
        offset: Int = 0,
        symbol: String? = null,
        from: String? = null,
        to: String? = null,
        isPaper: Boolean = true
    ): Flow<kotlin.Result<BotTradesResponse>> = flow {
        try {
            val response = botApi.getTrades(limit, offset, symbol, from, to, isPaper)
            if (response.isSuccessful && response.body() != null) {
                emit(kotlin.Result.success(response.body()!!))
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch trades: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(kotlin.Result.failure(e))
        }
    }

    /**
     * Get performance metrics
     */
    fun getPerformance(
        from: String? = null,
        to: String? = null,
        isPaper: Boolean = true
    ): Flow<kotlin.Result<BotPerformance>> = flow {
        try {
            val response = botApi.getPerformance(from, to, isPaper)
            if (response.isSuccessful && response.body() != null) {
                emit(kotlin.Result.success(response.body()!!))
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch performance: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(kotlin.Result.failure(e))
        }
    }

    /**
     * Get equity curve
     */
    fun getEquityCurve(
        from: String? = null,
        to: String? = null,
        interval: String = "daily",
        isPaper: Boolean = true
    ): Flow<kotlin.Result<BotEquityResponse>> = flow {
        try {
            val response = botApi.getEquityCurve(from, to, interval, isPaper)
            if (response.isSuccessful && response.body() != null) {
                emit(kotlin.Result.success(response.body()!!))
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch equity curve: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(kotlin.Result.failure(e))
        }
    }

    /**
     * Get bot status
     */
    fun getBotStatus(): Flow<kotlin.Result<BotStatus>> = flow {
        try {
            val response = botApi.getBotStatus()
            if (response.isSuccessful && response.body() != null) {
                emit(kotlin.Result.success(response.body()!!))
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch bot status: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(kotlin.Result.failure(e))
        }
    }
}
