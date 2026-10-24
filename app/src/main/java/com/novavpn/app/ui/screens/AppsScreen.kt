package com.novavpn.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.novavpn.app.ui.components.SectionHeader
import com.novavpn.app.ui.theme.*
import com.novavpn.app.viewmodel.MainViewModel

@Composable
fun AppsScreen(vm: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        SectionHeader(title = "Обход приложений")

        // Mode switches
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NovaSurfaceHigh,
            border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Глобальный режим", style = MaterialTheme.typography.titleMedium, color = NovaText)
                        Text(
                            "Все приложения через VPN",
                            style = MaterialTheme.typography.labelMedium,
                            color = NovaTextMuted
                        )
                    }
                    Switch(
                        checked = vm.globalProxyMode,
                        onCheckedChange = { vm.setGlobalProxyMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NovaAccent,
                            checkedTrackColor = NovaAccent.copy(alpha = 0.35f)
                        )
                    )
                }
                if (!vm.globalProxyMode) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = NovaBorder)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Инвертировать список", style = MaterialTheme.typography.titleMedium, color = NovaText)
                            Text(
                                if (vm.invertAppProxy)
                                    "Выбранные приложения обходят VPN"
                                else
                                    "Только выбранные идут через VPN",
                                style = MaterialTheme.typography.labelMedium,
                                color = NovaTextMuted
                            )
                        }
                        Switch(
                            checked = vm.invertAppProxy,
                            onCheckedChange = { vm.setInvertAppProxy(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NovaPrimary,
                                checkedTrackColor = NovaPrimary.copy(alpha = 0.35f)
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (!vm.globalProxyMode) {
            Text(
                "Список приложений",
                style = MaterialTheme.typography.titleMedium,
                color = NovaText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(vm.appProxyList, key = { it.packageName }) { app ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NovaSurfaceHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(app.appName, style = MaterialTheme.typography.bodyLarge, color = NovaText)
                                if (app.isSystem) {
                                    Text("Системное", style = MaterialTheme.typography.labelMedium, color = NovaTextMuted)
                                }
                            }
                            Switch(
                                checked = app.proxyEnabled,
                                onCheckedChange = { vm.toggleAppProxy(app.packageName) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NovaAccent,
                                    checkedTrackColor = NovaAccent.copy(alpha = 0.35f)
                                )
                            )
                        }
                    }
                }
            }
        } else {
            Spacer(Modifier.height(24.dp))
            Text(
                "Глобальный режим активен — весь трафик идёт через VPN.\nОтключите, чтобы выбрать приложения вручную.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )
        }
    }
}
