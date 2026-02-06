package com.iatrading.mobile.di

import android.content.Context
import com.iatrading.mobile.BuildConfig
import com.iatrading.mobile.data.api.BotApi
import com.iatrading.mobile.data.api.TradingApi
import com.iatrading.mobile.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        settingsRepository: SettingsRepository
    ): Retrofit {
        // Read the base URL from DataStore synchronously during initialization
        // This is acceptable in the DI graph initialization as it happens once at app start
        val baseUrl = runBlocking {
            settingsRepository.apiBaseUrl.first()
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl + "/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTradingApi(retrofit: Retrofit): TradingApi {
        return retrofit.create(TradingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBotApi(okHttpClient: OkHttpClient): BotApi {
        // Use the BOT_API_URL from BuildConfig
        val botBaseUrl = BuildConfig.BOT_API_URL

        val botRetrofit = Retrofit.Builder()
            .baseUrl(botBaseUrl + "/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return botRetrofit.create(BotApi::class.java)
    }
}
