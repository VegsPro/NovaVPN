package com.novavpn.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.novavpn.app.ui.components.SectionHeader
import com.novavpn.app.ui.theme.*
import com.novavpn.app.viewmodel.MainViewModel

@Composable
fun SettingsScreen(vm: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        SectionHeader(title = "Настройки")

        SettingsGroup {
            SettingsSwitch(
                icon = Icons.Outlined.Update,
                title = "Автообновление подписок",
                subtitle = "При каждом запуске приложения",
                checked = vm.autoUpdateOnLaunch,
                onCheckedChange = { vm.setAutoUpdateOnLaunch(it) }
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Протоколы", style = MaterialTheme.typography.titleMedium, color = NovaText, modifier = Modifier.padding(bottom = 8.dp))

        SettingsGroup {
            ProtocolRow("VLESS / Reality", "Xray-core, Reality, Vision")
            ProtocolRow("VMess", "V2Ray совместимость")
            ProtocolRow("Trojan", "TLS + Trojan")
            ProtocolRow("Shadowsocks", "AEAD шифрование")
            ProtocolRow("Hysteria2", "UDP, высокая скорость")
            ProtocolRow("TUIC", "QUIC транспорт")
        }

        Spacer(Modifier.height(16.dp))
        Text("О приложении", style = MaterialTheme.typography.titleMedium, color = NovaText, modifier = Modifier.padding(bottom = 8.dp))

        SettingsGroup {
            SettingsItem(
                icon = Icons.Outlined.Info,
                title = "NovaVPN",
                subtitle = "Версия 1.0.0 · Улучшенный клиент на базе Xray"
            )
            SettingsItem(
                icon = Icons.Outlined.Security,
                title = "Конфиденциальность",
                subtitle = "Все данные только на устройстве"
            )
            SettingsItem(
                icon = Icons.Outlined.Code,
                title = "Ядро",
                subtitle = "Xray-core · современные транспорты"
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "NovaVPN — удобный клиент для ваших конфигов и подписок.\nПоддерживает VLESS Reality, Hysteria2, Trojan и другие протоколы.\nОбход приложений, автообновление и современный интерфейс.",
            style = MaterialTheme.typography.bodyMedium,
            color = NovaTextMuted
        )
    }
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NovaSurfaceHigh,
        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NovaPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = NovaText)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = NovaTextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NovaAccent,
                checkedTrackColor = NovaAccent.copy(alpha = 0.35f)
            )
        )
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = NovaPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = NovaText)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = NovaTextMuted)
        }
    }
}

@Composable
private fun ProtocolRow(name: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge, color = NovaText)
            Text(desc, style = MaterialTheme.typography.labelMedium, color = NovaTextMuted)
        }
        Text("✓", color = NovaAccent)
    }
}
