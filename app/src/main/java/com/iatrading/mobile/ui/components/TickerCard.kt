package com.iatrading.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iatrading.mobile.data.model.Ticker
import com.iatrading.mobile.ui.theme.SentimentColors
import com.iatrading.mobile.ui.theme.SignalColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerCard(
    ticker: Ticker,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = ticker.ticker,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    ticker.name?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            ticker.sentiment?.let { sentiment ->
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sentiment Score
                    Column {
                        Text(
                            text = "Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val trendIcon = when {
                                sentiment.normalizedScore > 0.1 -> Icons.Default.TrendingUp
                                sentiment.normalizedScore < -0.1 -> Icons.Default.TrendingDown
                                else -> Icons.Default.TrendingFlat
                            }
                            Icon(
                                imageVector = trendIcon,
                                contentDescription = null,
                                tint = SentimentColors.fromScore(sentiment.normalizedScore),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.2f", sentiment.normalizedScore),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SentimentColors.fromScore(sentiment.normalizedScore)
                            )
                        }
                    }

                    // Signal Badge
                    sentiment.signal?.let { signal ->
                        SentimentBadge(
                            signal = signal,
                            color = SignalColors.fromSignal(signal)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Analyzed",
                        value = sentiment.totalAnalyzed.toString()
                    )
                    StatItem(
                        label = "Pending",
                        value = sentiment.totalPending.toString()
                    )
                    StatItem(
                        label = "Confidence",
                        value = "${(sentiment.confidence * 100).toInt()}%"
                    )
                }
            } ?: run {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No sentiment data yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
