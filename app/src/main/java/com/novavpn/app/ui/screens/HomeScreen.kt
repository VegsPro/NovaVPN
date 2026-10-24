package com.novavpn.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.novavpn.app.data.ConnectionState
import com.novavpn.app.ui.components.*
import com.novavpn.app.ui.theme.*
import com.novavpn.app.viewmodel.MainViewModel

@Composable
fun HomeScreen(vm: MainViewModel) {
    val state = vm.connectionState
    val server = vm.selectedServer
    val stats = vm.stats
    val isConnected = state == ConnectionState.CONNECTED
    val isConnecting = state == ConnectionState.CONNECTING || state == ConnectionState.DISCONNECTING

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status label
        Text(
            text = when (state) {
                ConnectionState.CONNECTED -> "Подключено"
                ConnectionState.CONNECTING -> "Подключение…"
                ConnectionState.DISCONNECTING -> "Отключение…"
                else -> "Отключено"
            },
            style = MaterialTheme.typography.titleMedium,
            color = if (isConnected) NovaAccent else NovaTextMuted
        )

        Spacer(Modifier.height(20.dp))

        PulsingConnectButton(
            isConnected = isConnected,
            isConnecting = isConnecting,
            onClick = { vm.toggleConnection() }
        )

        Spacer(Modifier.height(24.dp))

        // Current server card
        if (server != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                color = NovaSurfaceHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isConnected) NovaAccent.copy(alpha = 0.35f) else NovaBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(server.flag, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(server.name, style = MaterialTheme.typography.titleMedium, color = NovaText)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ProtocolBadge(server.protocol)
                            Spacer(Modifier.width(8.dp))
                            LatencyChip(server.latencyMs)
                        }
                    }
                    if (isConnected) {
                        Icon(Icons.Outlined.Speed, null, tint = NovaAccent)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats
        if (isConnected) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("↑ Отдано", vm.formatBytes(stats.uploadBytes), Modifier.weight(1f))
                StatCard("↓ Получено", vm.formatBytes(stats.downloadBytes), Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Скорость ↑", stats.currentSpeedUp, Modifier.weight(1f))
                StatCard("Скорость ↓", stats.currentSpeedDown, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            StatCard("Время", vm.formatDuration(stats.durationSec), Modifier.fillMaxWidth())
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = NovaSurfaceHigh
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Нажмите кнопку, чтобы подключиться",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NovaTextMuted
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Выберите сервер на вкладке «Серверы»",
                        style = MaterialTheme.typography.labelMedium,
                        color = NovaTextMuted.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Quick actions
        SectionHeader(title = "Быстрые действия") {
            IconButton(onClick = { vm.refreshSubscriptions() }) {
                Icon(Icons.Default.Refresh, "Обновить", tint = NovaPrimary)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickChip("Авто-обновление", vm.autoUpdateOnLaunch) {
                vm.updateAutoUpdateOnLaunch(!vm.autoUpdateOnLaunch)
            }
            QuickChip("Избранные", vm.showOnlyFavorites) {
                vm.toggleFavoritesFilter()
            }
        }
    }
}

@Composable
private fun QuickChip(label: String, active: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = active,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NovaPrimary.copy(alpha = 0.25f),
            selectedLabelColor = NovaPrimarySoft
        )
    )
}
