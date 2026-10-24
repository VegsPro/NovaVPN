package com.novavpn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.novavpn.app.ui.screens.*
import com.novavpn.app.ui.theme.NovaVPNTheme
import com.novavpn.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovaVPNTheme {
                NovaVPNApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Главная", Icons.Filled.Home)
    data object Servers : Screen("servers", "Серверы", Icons.Outlined.Dns)
    data object Subscriptions : Screen("subs", "Подписки", Icons.Outlined.Subscriptions)
    data object Apps : Screen("apps", "Приложения", Icons.Outlined.Apps)
    data object Settings : Screen("settings", "Настройки", Icons.Filled.Settings)
}

@Composable
fun NovaVPNApp(vm: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Servers,
        Screen.Subscriptions,
        Screen.Apps,
        Screen.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = com.novavpn.app.ui.theme.NovaBg,
        bottomBar = {
            NavigationBar(
                containerColor = com.novavpn.app.ui.theme.NovaSurface,
                contentColor = com.novavpn.app.ui.theme.NovaText
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = com.novavpn.app.ui.theme.NovaPrimary,
                            selectedTextColor = com.novavpn.app.ui.theme.NovaPrimarySoft,
                            indicatorColor = com.novavpn.app.ui.theme.NovaPrimary.copy(alpha = 0.18f),
                            unselectedIconColor = com.novavpn.app.ui.theme.NovaTextMuted,
                            unselectedTextColor = com.novavpn.app.ui.theme.NovaTextMuted
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen(vm) }
            composable(Screen.Servers.route) { ServersScreen(vm) }
            composable(Screen.Subscriptions.route) { SubscriptionsScreen(vm) }
            composable(Screen.Apps.route) { AppsScreen(vm) }
            composable(Screen.Settings.route) { SettingsScreen(vm) }
        }
    }
}
