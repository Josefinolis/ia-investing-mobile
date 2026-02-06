package com.iatrading.mobile.data.api

import com.iatrading.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API interface for IA Trading Bot endpoints
 */
interface BotApi {

    @GET("api/bot/config")
    suspend fun getBotConfig(): Response<BotConfig>

    @GET("api/bot/trades")
    suspend fun getTrades(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("symbol") symbol: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("is_paper") isPaper: Boolean = true
    ): Response<BotTradesResponse>

    @GET("api/bot/performance")
    suspend fun getPerformance(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("is_paper") isPaper: Boolean = true
    ): Response<BotPerformance>

    @GET("api/bot/equity")
    suspend fun getEquityCurve(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("interval") interval: String = "daily",
        @Query("is_paper") isPaper: Boolean = true
    ): Response<BotEquityResponse>

    @GET("api/bot/status")
    suspend fun getBotStatus(): Response<BotStatus>
}
