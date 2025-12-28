package com.iatrading.mobile.ui.theme

import androidx.compose.ui.graphics.Color

// Primary colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Sentiment colors
object SentimentColors {
    val HighlyPositive = Color(0xFF2E7D32)  // Dark Green
    val Positive = Color(0xFF4CAF50)         // Green
    val Neutral = Color(0xFFFFC107)          // Amber
    val Negative = Color(0xFFFF9800)         // Orange
    val HighlyNegative = Color(0xFFD32F2F)   // Red

    fun fromScore(score: Double): Color {
        return when {
            score >= 0.5 -> HighlyPositive
            score >= 0.2 -> Positive
            score >= -0.2 -> Neutral
            score >= -0.5 -> Negative
            else -> HighlyNegative
        }
    }

    fun fromLabel(label: String?): Color {
        return when (label) {
            "Highly Positive" -> HighlyPositive
            "Positive" -> Positive
            "Neutral" -> Neutral
            "Negative" -> Negative
            "Highly Negative" -> HighlyNegative
            else -> Neutral
        }
    }
}

// Signal colors
object SignalColors {
    val StrongBuy = Color(0xFF1B5E20)
    val Buy = Color(0xFF4CAF50)
    val Hold = Color(0xFFFFC107)
    val Sell = Color(0xFFFF9800)
    val StrongSell = Color(0xFFB71C1C)

    fun fromSignal(signal: String?): Color {
        return when (signal) {
            "STRONG BUY" -> StrongBuy
            "BUY" -> Buy
            "HOLD" -> Hold
            "SELL" -> Sell
            "STRONG SELL" -> StrongSell
            else -> Hold
        }
    }
}
