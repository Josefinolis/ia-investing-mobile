package com.iatrading.mobile.data.api

import com.iatrading.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface TradingApi {

    @GET("api/tickers")
    suspend fun getTickers(): Response<TickerListResponse>

    @POST("api/tickers")
    suspend fun addTicker(@Body ticker: TickerCreate): Response<Ticker>

    @DELETE("api/tickers/{ticker}")
    suspend fun removeTicker(@Path("ticker") ticker: String): Response<Unit>

    @GET("api/tickers/{ticker}")
    suspend fun getTicker(@Path("ticker") ticker: String): Response<Ticker>

    @GET("api/tickers/{ticker}/news")
    suspend fun getTickerNews(
        @Path("ticker") ticker: String,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<NewsListResponse>

    @GET("api/tickers/{ticker}/sentiment")
    suspend fun getTickerSentiment(
        @Path("ticker") ticker: String
    ): Response<TickerSentiment>

    @POST("api/tickers/{ticker}/fetch")
    suspend fun triggerFetch(
        @Path("ticker") ticker: String,
        @Query("hours") hours: Int = 24
    ): Response<Map<String, String>>
}
