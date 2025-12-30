package com.iatrading.mobile.data.repository

import com.iatrading.mobile.data.api.TradingApi
import com.iatrading.mobile.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class TradingRepository @Inject constructor(
    private val api: TradingApi
) {

    suspend fun getTickers(): Result<List<Ticker>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTickers()
            if (response.isSuccessful) {
                Result.Success(response.body()?.tickers ?: emptyList())
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }

    suspend fun addTicker(ticker: String, name: String?): Result<Ticker> = withContext(Dispatchers.IO) {
        try {
            val response = api.addTicker(TickerCreate(ticker.uppercase(), name))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response")
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }

    suspend fun removeTicker(ticker: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.removeTicker(ticker)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }

    suspend fun getTicker(ticker: String): Result<Ticker> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTicker(ticker)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Ticker not found")
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }

    suspend fun getTickerNews(
        ticker: String,
        status: String? = null
    ): Result<NewsListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTickerNews(ticker, status)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response")
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }

    suspend fun refreshTicker(ticker: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.triggerFetch(ticker)
            if (response.isSuccessful) {
                Result.Success(response.body()?.get("message") ?: "Refresh triggered")
            } else {
                Result.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }

    suspend fun getApiStatus(): Result<ApiStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getApiStatus()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response")
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}", e)
        }
    }
}
