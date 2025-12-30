package com.iatrading.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iatrading.mobile.data.model.ApiStatusResponse
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@Composable
fun ApiStatusBanner(
    apiStatus: ApiStatusResponse?,
    modifier: Modifier = Modifier
) {
    if (apiStatus == null) return

    val warnings = mutableListOf<String>()

    // Check Gemini (AI analysis)
    if (!apiStatus.gemini.available && apiStatus.gemini.message != null) {
        val timeRemaining = apiStatus.gemini.cooldownUntil?.let {
            calculateTimeRemaining(it)
        }
        val message = if (timeRemaining != null) {
            "AI analysis temporarily unavailable (rate limit). Retrying in $timeRemaining."
        } else {
            "AI analysis temporarily unavailable (rate limit)."
        }
        warnings.add(message)
    }

    // Check Alpha Vantage (News retrieval)
    if (!apiStatus.alphaVantage.available && apiStatus.alphaVantage.message != null) {
        val timeRemaining = apiStatus.alphaVantage.cooldownUntil?.let {
            calculateTimeRemaining(it)
        }
        val message = if (timeRemaining != null) {
            "News retrieval temporarily unavailable (rate limit). Retrying in $timeRemaining."
        } else {
            "News retrieval temporarily unavailable (rate limit)."
        }
        warnings.add(message)
    }

    if (warnings.isEmpty()) return

    // Auto-refresh the remaining time every second
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            tick++
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            warnings.forEach { warning ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = warning,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                if (warnings.indexOf(warning) < warnings.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun calculateTimeRemaining(cooldownUntil: String): String? {
    return try {
        val cooldownTime = Instant.parse(cooldownUntil)
        val now = Instant.now()
        val duration = Duration.between(now, cooldownTime)

        if (duration.isNegative) {
            null
        } else {
            val seconds = duration.seconds
            when {
                seconds >= 3600 -> {
                    val hours = seconds / 3600
                    val minutes = (seconds % 3600) / 60
                    "${hours}h ${minutes}m"
                }
                seconds >= 60 -> {
                    val minutes = seconds / 60
                    val remainingSeconds = seconds % 60
                    "${minutes}m ${remainingSeconds}s"
                }
                else -> "${seconds}s"
            }
        }
    } catch (e: Exception) {
        null
    }
}
