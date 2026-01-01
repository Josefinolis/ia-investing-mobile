package com.iatrading.mobile.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iatrading.mobile.R
import com.iatrading.mobile.ui.components.ApiStatusBanner
import com.iatrading.mobile.ui.components.NewsItemCard
import com.iatrading.mobile.ui.components.SentimentBadge
import com.iatrading.mobile.ui.theme.SentimentColors
import com.iatrading.mobile.ui.theme.SignalColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerDetailScreen(
    tickerSymbol: String,
    onBackClick: () -> Unit,
    viewModel: TickerDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(tickerSymbol) {
        viewModel.loadTicker(tickerSymbol)
    }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(tickerSymbol) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Fetch News button
                    IconButton(
                        onClick = { viewModel.fetchNews(tickerSymbol) },
                        enabled = !uiState.isFetching && !uiState.isAnalyzing
                    ) {
                        if (uiState.isFetching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Download, contentDescription = "Fetch News")
                        }
                    }
                    // Analyze button
                    IconButton(
                        onClick = { viewModel.analyzeNews(tickerSymbol) },
                        enabled = !uiState.isFetching && !uiState.isAnalyzing && uiState.pendingCount > 0
                    ) {
                        if (uiState.isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = "Analyze")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.error ?: "Error")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadTicker(tickerSymbol) }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // API Status Banner
                        if (uiState.apiStatus != null) {
                            item {
                                ApiStatusBanner(apiStatus = uiState.apiStatus)
                            }
                        }

                        // Sentiment Summary Card
                        uiState.ticker?.sentiment?.let { sentiment ->
                            item {
                                SentimentSummaryCard(
                                    tickerName = uiState.ticker?.name,
                                    sentiment = sentiment
                                )
                            }
                        }

                        // Stats
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = { },
                                    label = { Text("All (${uiState.news.size})") }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { },
                                    label = { Text("Analyzed (${uiState.analyzedCount})") }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { },
                                    label = { Text("Pending (${uiState.pendingCount})") }
                                )
                            }
                        }

                        // News List
                        if (uiState.news.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_news),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 32.dp)
                                )
                            }
                        } else {
                            items(uiState.news) { newsItem ->
                                NewsItemCard(news = newsItem)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SentimentSummaryCard(
    tickerName: String?,
    sentiment: com.iatrading.mobile.data.model.TickerSentiment
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            tickerName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sentiment Score",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format("%.2f", sentiment.normalizedScore),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SentimentColors.fromScore(sentiment.normalizedScore)
                    )
                    sentiment.sentimentLabel?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = SentimentColors.fromLabel(it)
                        )
                    }
                }

                sentiment.signal?.let { signal ->
                    SentimentBadge(
                        signal = signal,
                        color = SignalColors.fromSignal(signal)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn("Positive", sentiment.positiveCount, SentimentColors.Positive)
                StatColumn("Neutral", sentiment.neutralCount, SentimentColors.Neutral)
                StatColumn("Negative", sentiment.negativeCount, SentimentColors.Negative)
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
