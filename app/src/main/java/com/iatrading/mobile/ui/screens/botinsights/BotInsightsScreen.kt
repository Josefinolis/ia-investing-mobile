package com.iatrading.mobile.ui.screens.botinsights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iatrading.mobile.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotInsightsScreen(
    onBackClick: () -> Unit,
    viewModel: BotInsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Config", "Trades", "Performance")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bot Insights") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (uiState) {
                is BotInsightsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BotInsightsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                (uiState as BotInsightsUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is BotInsightsUiState.Success -> {
                    val successState = uiState as BotInsightsUiState.Success
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = { viewModel.refresh() }
                    ) {
                        when (selectedTab) {
                            0 -> OverviewTab(successState)
                            1 -> ConfigTab(successState.selectedBot?.config)
                            2 -> TradesTab(successState.recentTrades)
                            3 -> PerformanceTab(successState.performance, successState.equityData)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(state: BotInsightsUiState.Success) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bot Status Card
        item {
            state.selectedBot?.let { bot ->
                BotStatusDetailCard(bot)
            }
        }

        // All Bots Summary (if multiple bots)
        if (state.allBots.size > 1) {
            item {
                AllBotsCard(state.allBots)
            }
        }

        // Quick Stats
        item {
            state.performance?.let { perf ->
                QuickStatsCard(perf)
            }
        }

        // Recent Trades Header
        if (state.recentTrades.isNotEmpty()) {
            item {
                Text(
                    "Recent Trades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(state.recentTrades.take(5)) { trade ->
                TradeListItem(trade)
            }
        }
    }
}

@Composable
private fun ConfigTab(config: BotConfigDetail?) {
    if (config == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No configuration data available")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ConfigurationDetailCard(config)
        }
        config.riskConfig?.let { riskConfig ->
            item {
                RiskConfigDetailCard(riskConfig)
            }
        }
    }
}

@Composable
private fun TradesTab(trades: List<com.iatrading.mobile.data.model.BotTrade>) {
    if (trades.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No trades yet")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(trades) { trade ->
            TradeListItem(trade)
        }
    }
}

@Composable
private fun PerformanceTab(
    performance: com.iatrading.mobile.data.model.BotPerformance?,
    equityData: List<com.iatrading.mobile.data.model.EquityDataPoint>
) {
    if (performance == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No performance data available")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            PerformanceMetricsCard(performance)
        }
        if (equityData.isNotEmpty()) {
            item {
                EquityCurveCard(equityData)
            }
        }
    }
}

// Bot Status Cards
@Composable
private fun BotStatusDetailCard(bot: BotStatusDetail) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    if (bot.isRunning) Icons.Default.PlayArrow else Icons.Default.Stop,
                    contentDescription = null,
                    tint = if (bot.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    if (bot.isRunning) "Bot Running" else "Bot Stopped",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            bot.symbol?.let { symbol ->
                ConfigRow("Symbol", symbol)
            }
            bot.strategy?.let { strategy ->
                ConfigRow("Strategy", strategy)
            }

            if (bot.killSwitchActive) {
                Text(
                    "Kill Switch Active",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            bot.lastSignalType?.let { signalType ->
                ConfigRow("Last Signal", signalType)
            }

            bot.uptimeSeconds?.let { uptime ->
                val hours = uptime / 3600
                val minutes = (uptime % 3600) / 60
                ConfigRow("Uptime", "${hours}h ${minutes}m")
            }
        }
    }
}

@Composable
private fun AllBotsCard(bots: List<BotStatusDetail>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "All Bots (${bots.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Divider()
            bots.forEach { bot ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (bot.isRunning) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (bot.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(bot.symbol ?: bot.sessionId.take(8))
                    }
                    Text(
                        bot.strategy ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (bot != bots.last()) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickStatsCard(perf: com.iatrading.mobile.data.model.BotPerformance) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Quick Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Return", "${perf.returnPercent}%")
                StatItem("Win Rate", "${perf.winRate}%")
                StatItem("Trades", perf.totalTrades.toString())
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun TradeListItem(trade: com.iatrading.mobile.data.model.BotTrade) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (trade.side == "BUY") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (trade.side == "BUY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Column {
                    Text(trade.symbol, fontWeight = FontWeight.Bold)
                    Text("${trade.side} ${trade.quantity} @ ${trade.price}", style = MaterialTheme.typography.bodySmall)
                    Text(trade.timestamp.take(16), style = MaterialTheme.typography.bodySmall)
                }
            }
            trade.pnl?.let { pnl ->
                Text(
                    pnl,
                    fontWeight = FontWeight.Bold,
                    color = if (pnl.startsWith("-")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ConfigurationDetailCard(config: BotConfigDetail) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Bot Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider()
            config.broker?.let { ConfigRow("Broker", it) }
            config.tradingMode?.let { ConfigRow("Mode", it) }
            config.interval?.let { ConfigRow("Interval", it) }
            config.mlModel?.let { ConfigRow("ML Model", it) }
            config.confidenceThreshold?.let { ConfigRow("Threshold", it) }
            config.initialBalance?.let { ConfigRow("Initial Balance", "$${it}") }
        }
    }
}

@Composable
private fun RiskConfigDetailCard(riskConfig: BotRiskConfig) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Risk Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider()
            riskConfig.maxPositionPct?.let { ConfigRow("Max Position", "${it}%") }
            riskConfig.maxDailyLossPct?.let { ConfigRow("Max Daily Loss", "${it}%") }
            riskConfig.stopLossPct?.let { ConfigRow("Stop Loss", "${it}%") }
        }
    }
}

@Composable
private fun ConfigRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PerformanceMetricsCard(perf: com.iatrading.mobile.data.model.BotPerformance) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Performance Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider()
            ConfigRow("Total Return", "$${perf.totalReturn} (${perf.returnPercent}%)")
            ConfigRow("Total Trades", perf.totalTrades.toString())
            ConfigRow("Winning Trades", perf.winningTrades.toString())
            ConfigRow("Losing Trades", perf.losingTrades.toString())
            ConfigRow("Win Rate", "${perf.winRate}%")
            ConfigRow("Profit Factor", perf.profitFactor)
            ConfigRow("Max Drawdown", "${perf.maxDrawdown}%")
            ConfigRow("Sharpe Ratio", perf.sharpeRatio)
            ConfigRow("Avg Win", "$${perf.averageWin}")
            ConfigRow("Avg Loss", "$${perf.averageLoss}")
            Divider()
            ConfigRow("Start Balance", "$${perf.startBalance}")
            ConfigRow("Current Balance", "$${perf.currentBalance}")
        }
    }
}

@Composable
private fun EquityCurveCard(equityData: List<com.iatrading.mobile.data.model.EquityDataPoint>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Equity Curve", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder for equity curve chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Chart visualization: ${equityData.size} data points",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
