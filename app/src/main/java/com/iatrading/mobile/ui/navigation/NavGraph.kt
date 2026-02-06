package com.iatrading.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iatrading.mobile.ui.screens.addticker.AddTickerScreen
import com.iatrading.mobile.ui.screens.botinsights.BotInsightsScreen
import com.iatrading.mobile.ui.screens.dashboard.DashboardScreen
import com.iatrading.mobile.ui.screens.detail.TickerDetailScreen
import com.iatrading.mobile.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TickerDetail : Screen("ticker/{symbol}") {
        fun createRoute(symbol: String) = "ticker/$symbol"
    }
    object AddTicker : Screen("add_ticker")
    object Settings : Screen("settings")
    object BotInsights : Screen("bot_insights")
}

@Composable
fun TradingNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onTickerClick = { ticker ->
                    navController.navigate(Screen.TickerDetail.createRoute(ticker))
                },
                onAddClick = {
                    navController.navigate(Screen.AddTicker.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onBotInsightsClick = {
                    navController.navigate(Screen.BotInsights.route)
                }
            )
        }

        composable(
            route = Screen.TickerDetail.route,
            arguments = listOf(
                navArgument("symbol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
            TickerDetailScreen(
                tickerSymbol = symbol,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AddTicker.route) {
            AddTickerScreen(
                onTickerAdded = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.BotInsights.route) {
            BotInsightsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
