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
     * Get all bot statuses
     * Returns MultiBotStatusResponse with list of all bots and their configurations
     */
    fun getAllBotStatus(): Flow<kotlin.Result<MultiBotStatusResponse>> = flow {
        try {
            android.util.Log.d("BotRepository", "Fetching bot status from API...")
            val response = botApi.getBotStatus()
            android.util.Log.d("BotRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                android.util.Log.d("BotRepository", "Success! Got ${body.bots.size} bots")
                body.bots.forEachIndexed { index, bot ->
                    android.util.Log.d("BotRepository", "Bot $index: sessionId=${bot.sessionId}, symbol=${bot.symbol}, isRunning=${bot.isRunning}, hasConfig=${bot.config != null}")
                }
                emit(kotlin.Result.success(body))
            } else {
                val errorMsg = "Failed to fetch bot status: ${response.code()} - ${response.message()}"
                android.util.Log.e("BotRepository", errorMsg)
                android.util.Log.e("BotRepository", "Error body: ${response.errorBody()?.string()}")
                emit(kotlin.Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            android.util.Log.e("BotRepository", "Exception fetching bot status", e)
            emit(kotlin.Result.failure(e))
        }
    }

    /**
     * Get first bot status (for single-bot scenarios)
     * Returns the first bot from the multi-bot status response
     */
    fun getFirstBotStatus(): Flow<kotlin.Result<BotStatusDetail>> = flow {
        try {
            val response = botApi.getBotStatus()
            if (response.isSuccessful && response.body() != null) {
                val multiBotStatus = response.body()!!
                if (multiBotStatus.bots.isNotEmpty()) {
                    emit(kotlin.Result.success(multiBotStatus.bots.first()))
                } else {
                    emit(kotlin.Result.failure(Exception("No bots found in status response")))
                }
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch bot status: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(kotlin.Result.failure(e))
        }
    }

    /**
     * Get bot status by symbol
     * Returns the bot matching the given symbol
     */
    fun getBotStatusBySymbol(symbol: String): Flow<kotlin.Result<BotStatusDetail>> = flow {
        try {
            val response = botApi.getBotStatus()
            if (response.isSuccessful && response.body() != null) {
                val multiBotStatus = response.body()!!
                val bot = multiBotStatus.bots.find { it.symbol == symbol }
                if (bot != null) {
                    emit(kotlin.Result.success(bot))
                } else {
                    emit(kotlin.Result.failure(Exception("No bot found for symbol $symbol")))
                }
            } else {
                emit(kotlin.Result.failure(Exception("Failed to fetch bot status: ${response.message()}")))
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
            when {
                response.isSuccessful && response.body() != null -> {
                    emit(kotlin.Result.success(response.body()!!))
                }
                response.code() == 404 -> {
                    // 404 means no performance data yet - not a fatal error
                    android.util.Log.d("BotRepository", "Performance data not found (404) - continuing without it")
                    // Don't emit failure, let the ViewModel handle null performance
                }
                else -> {
                    emit(kotlin.Result.failure(Exception("Failed to fetch performance: ${response.message()}")))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("BotRepository", "Exception fetching performance", e)
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
}
