package com.novavpn.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.novavpn.app.data.Subscription
import com.novavpn.app.ui.components.SectionHeader
import com.novavpn.app.ui.theme.*
import com.novavpn.app.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SubscriptionsScreen(vm: MainViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        SectionHeader(title = "Подписки") {
            Row {
                IconButton(onClick = { vm.refreshSubscriptions() }) {
                    Icon(Icons.Default.Refresh, "Обновить все", tint = NovaPrimary)
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, "Добавить", tint = NovaAccent)
                }
            }
        }

        Text(
            "Автообновление при запуске: ${if (vm.autoUpdateOnLaunch) "вкл" else "выкл"}",
            style = MaterialTheme.typography.labelMedium,
            color = NovaTextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(vm.subscriptions, key = { it.id }) { sub ->
                SubscriptionCard(
                    sub = sub,
                    onToggleAuto = { vm.toggleSubAutoUpdate(sub.id) },
                    onDelete = { vm.removeSubscription(sub.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddSubscriptionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, url ->
                vm.addSubscription(name, url)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SubscriptionCard(
    sub: Subscription,
    onToggleAuto: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFmt = remember { SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NovaSurfaceHigh,
        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(sub.name, style = MaterialTheme.typography.titleMedium, color = NovaText)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${sub.serversCount} серверов · ${dateFmt.format(Date(sub.lastUpdated))}",
                        style = MaterialTheme.typography.labelMedium,
                        color = NovaTextMuted
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Удалить", tint = NovaDanger.copy(alpha = 0.8f))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                sub.url.take(48) + if (sub.url.length > 48) "…" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted.copy(alpha = 0.7f),
                maxLines = 1
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Update, null, tint = if (sub.autoUpdate) NovaAccent else NovaTextMuted, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Автообновление каждые ${sub.updateIntervalHours} ч",
                    style = MaterialTheme.typography.labelMedium,
                    color = NovaTextMuted,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = sub.autoUpdate,
                    onCheckedChange = { onToggleAuto() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NovaAccent,
                        checkedTrackColor = NovaAccent.copy(alpha = 0.35f)
                    )
                )
            }
        }
    }
}

@Composable
private fun AddSubscriptionDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая подписка") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL или vless://…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (url.isNotBlank()) onAdd(name, url) },
                enabled = url.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        containerColor = NovaSurface
    )
}
