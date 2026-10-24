package com.novavpn.app.data

import androidx.compose.runtime.Immutable

enum class Protocol(val displayName: String, val colorHex: Long) {
    VLESS("VLESS", 0xFF7C5CFF),
    VLESS_REALITY("VLESS Reality", 0xFF9B82FF),
    VMESS("VMess", 0xFF00B4D8),
    TROJAN("Trojan", 0xFFFF5C7A),
    SHADOWSOCKS("Shadowsocks", 0xFFFFB020),
    HYSTERIA2("Hysteria2", 0xFF00E5A8),
    TUIC("TUIC", 0xFF4CC9F0),
    SOCKS("SOCKS", 0xFF8B95A8)
}

@Immutable
data class ServerNode(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val protocol: Protocol,
    val latencyMs: Int? = null,
    val isFavorite: Boolean = false,
    val isSelected: Boolean = false,
    val trafficUsed: String? = null,
    val address: String = ""
)

@Immutable
data class Subscription(
    val id: String,
    val name: String,
    val url: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val autoUpdate: Boolean = true,
    val updateIntervalHours: Int = 12,
    val serversCount: Int = 0,
    val isEnabled: Boolean = true
)

@Immutable
data class AppProxyItem(
    val packageName: String,
    val appName: String,
    val isSystem: Boolean = false,
    val proxyEnabled: Boolean = true // true = goes through VPN
)

@Immutable
data class ConnectionStats(
    val uploadBytes: Long = 0,
    val downloadBytes: Long = 0,
    val durationSec: Long = 0,
    val currentSpeedUp: String = "0 B/s",
    val currentSpeedDown: String = "0 B/s"
)

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING
}
