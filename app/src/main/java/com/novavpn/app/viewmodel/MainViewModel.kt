package com.novavpn.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novavpn.app.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    var connectionState by mutableStateOf(ConnectionState.DISCONNECTED)
        private set

    var selectedServer by mutableStateOf<ServerNode?>(null)
        private set

    var stats by mutableStateOf(ConnectionStats())
        private set

    var subscriptions by mutableStateOf(listOf(
        Subscription(
            id = "sub1",
            name = "Основная подписка",
            url = "https://example.com/sub",
            serversCount = 12,
            autoUpdate = true,
            updateIntervalHours = 6
        ),
        Subscription(
            id = "sub2",
            name = "Резерв (Hysteria)",
            url = "https://backup.example.com/hy2",
            serversCount = 5,
            autoUpdate = true,
            updateIntervalHours = 12
        )
    ))
        private set

    var servers by mutableStateOf(sampleServers())
        private set

    var appProxyList by mutableStateOf(sampleApps())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var showOnlyFavorites by mutableStateOf(false)
        private set

    var autoUpdateOnLaunch by mutableStateOf(true)
        private set

    var globalProxyMode by mutableStateOf(true) // true = all apps, false = selected only
        private set

    var invertAppProxy by mutableStateOf(false) // true = selected bypass VPN
        private set

    val filteredServers: List<ServerNode>
        get() {
            var list = servers
            if (showOnlyFavorites) list = list.filter { it.isFavorite }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                list = list.filter {
                    it.name.lowercase().contains(q) ||
                    it.country.lowercase().contains(q) ||
                    it.protocol.displayName.lowercase().contains(q)
                }
            }
            return list
        }

    init {
        selectedServer = servers.firstOrNull { it.isSelected } ?: servers.firstOrNull()
        if (autoUpdateOnLaunch) {
            viewModelScope.launch {
                delay(800)
                refreshSubscriptions(silent = true)
            }
        }
    }

    fun toggleConnection() {
        when (connectionState) {
            ConnectionState.DISCONNECTED -> connect()
            ConnectionState.CONNECTED -> disconnect()
            else -> {}
        }
    }

    private fun connect() {
        val server = selectedServer ?: return
        connectionState = ConnectionState.CONNECTING
        viewModelScope.launch {
            delay(1400)
            connectionState = ConnectionState.CONNECTED
            startStatsSimulation()
        }
    }

    private fun disconnect() {
        connectionState = ConnectionState.DISCONNECTING
        viewModelScope.launch {
            delay(600)
            connectionState = ConnectionState.DISCONNECTED
            stats = ConnectionStats()
        }
    }

    private fun startStatsSimulation() {
        viewModelScope.launch {
            var up = 0L
            var down = 0L
            var sec = 0L
            while (connectionState == ConnectionState.CONNECTED) {
                delay(1000)
                sec++
                val upDelta = (80_000..450_000).random().toLong()
                val downDelta = (200_000..1_800_000).random().toLong()
                up += upDelta
                down += downDelta
                stats = ConnectionStats(
                    uploadBytes = up,
                    downloadBytes = down,
                    durationSec = sec,
                    currentSpeedUp = formatSpeed(upDelta),
                    currentSpeedDown = formatSpeed(downDelta)
                )
            }
        }
    }

    fun selectServer(server: ServerNode) {
        servers = servers.map { it.copy(isSelected = it.id == server.id) }
        selectedServer = server.copy(isSelected = true)
    }

    fun toggleFavorite(serverId: String) {
        servers = servers.map {
            if (it.id == serverId) it.copy(isFavorite = !it.isFavorite) else it
        }
        if (selectedServer?.id == serverId) {
            selectedServer = selectedServer?.copy(isFavorite = !(selectedServer?.isFavorite ?: false))
        }
    }

    fun updateSearch(query: String) {
        searchQuery = query
    }

    fun toggleFavoritesFilter() {
        showOnlyFavorites = !showOnlyFavorites
    }

    fun addSubscription(name: String, url: String) {
        val newSub = Subscription(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Подписка ${subscriptions.size + 1}" },
            url = url,
            serversCount = (3..15).random(),
            autoUpdate = true
        )
        subscriptions = subscriptions + newSub
        // Simulate adding servers
        val newServers = (1..newSub.serversCount).map { i ->
            ServerNode(
                id = UUID.randomUUID().toString(),
                name = "${newSub.name} #${i}",
                country = listOf("🇩🇪 Германия", "🇳🇱 Нидерланды", "🇫🇮 Финляндия", "🇺🇸 США", "🇸🇬 Сингапур").random(),
                flag = listOf("🇩🇪", "🇳🇱", "🇫🇮", "🇺🇸", "🇸🇬").random(),
                protocol = Protocol.entries.random(),
                latencyMs = (20..180).random()
            )
        }
        servers = servers + newServers
    }

    fun removeSubscription(id: String) {
        subscriptions = subscriptions.filter { it.id != id }
    }

    fun toggleSubAutoUpdate(id: String) {
        subscriptions = subscriptions.map {
            if (it.id == id) it.copy(autoUpdate = !it.autoUpdate) else it
        }
    }

    fun refreshSubscriptions(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) delay(600)
            subscriptions = subscriptions.map {
                it.copy(
                    lastUpdated = System.currentTimeMillis(),
                    serversCount = (it.serversCount + (-1..2).random()).coerceAtLeast(1)
                )
            }
            // Refresh latencies
            servers = servers.map {
                it.copy(latencyMs = (15..220).random())
            }
        }
    }

    fun toggleAppProxy(packageName: String) {
        appProxyList = appProxyList.map {
            if (it.packageName == packageName) it.copy(proxyEnabled = !it.proxyEnabled) else it
        }
    }

    fun setGlobalProxyMode(enabled: Boolean) {
        globalProxyMode = enabled
    }

    fun setInvertAppProxy(invert: Boolean) {
        invertAppProxy = invert
    }

    fun setAutoUpdateOnLaunch(enabled: Boolean) {
        autoUpdateOnLaunch = enabled
    }

    private fun formatSpeed(bytesPerSec: Long): String {
        return when {
            bytesPerSec >= 1_000_000 -> "%.1f MB/s".format(bytesPerSec / 1_000_000.0)
            bytesPerSec >= 1_000 -> "%.0f KB/s".format(bytesPerSec / 1_000.0)
            else -> "$bytesPerSec B/s"
        }
    }

    fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.2f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            bytes >= 1024 -> "%.0f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
    }

    fun formatDuration(sec: Long): String {
        val h = sec / 3600
        val m = (sec % 3600) / 60
        val s = sec % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    }

    private fun sampleServers(): List<ServerNode> = listOf(
        ServerNode("1", "Frankfurt Premium", "🇩🇪 Германия", "🇩🇪", Protocol.VLESS_REALITY, 28, true, true),
        ServerNode("2", "Amsterdam Ultra", "🇳🇱 Нидерланды", "🇳🇱", Protocol.HYSTERIA2, 35, true),
        ServerNode("3", "Helsinki Fast", "🇫🇮 Финляндия", "🇫🇮", Protocol.VLESS, 42),
        ServerNode("4", "New York", "🇺🇸 США", "🇺🇸", Protocol.TROJAN, 98),
        ServerNode("5", "Singapore SG", "🇸🇬 Сингапур", "🇸🇬", Protocol.VMESS, 120),
        ServerNode("6", "Tokyo JP", "🇯🇵 Япония", "🇯🇵", Protocol.SHADOWSOCKS, 145),
        ServerNode("7", "London UK", "🇬🇧 Великобритания", "🇬🇧", Protocol.VLESS_REALITY, 55),
        ServerNode("8", "Paris FR", "🇫🇷 Франция", "🇫🇷", Protocol.HYSTERIA2, 48),
        ServerNode("9", "Warsaw PL", "🇵🇱 Польша", "🇵🇱", Protocol.VLESS, 38),
        ServerNode("10", "Stockholm SE", "🇸🇪 Швеция", "🇸🇪", Protocol.TUIC, 52),
        ServerNode("11", "Istanbul TR", "🇹🇷 Турция", "🇹🇷", Protocol.TROJAN, 67),
        ServerNode("12", "Dubai AE", "🇦🇪 ОАЭ", "🇦🇪", Protocol.VLESS_REALITY, 89)
    )

    private fun sampleApps(): List<AppProxyItem> = listOf(
        AppProxyItem("com.android.chrome", "Chrome", false, true),
        AppProxyItem("org.telegram.messenger", "Telegram", false, true),
        AppProxyItem("com.whatsapp", "WhatsApp", false, false),
        AppProxyItem("com.instagram.android", "Instagram", false, true),
        AppProxyItem("com.google.android.youtube", "YouTube", false, true),
        AppProxyItem("ru.yandex.browser", "Яндекс", false, false),
        AppProxyItem("com.vkontakte.android", "ВКонтакте", false, false),
        AppProxyItem("ru.sberbankmobile", "Сбербанк", false, false),
        AppProxyItem("com.spotify.music", "Spotify", false, true),
        AppProxyItem("com.discord", "Discord", false, true),
        AppProxyItem("com.twitter.android", "X (Twitter)", false, true),
        AppProxyItem("com.android.vending", "Play Маркет", true, false)
    )
}
