package com.novavpn.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novavpn.app.data.Protocol
import com.novavpn.app.data.ServerNode
import com.novavpn.app.ui.theme.*

@Composable
fun ProtocolBadge(protocol: Protocol) {
    val color = Color(protocol.colorHex)
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.18f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Text(
            text = protocol.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun LatencyChip(ms: Int?) {
    if (ms == null) return
    val color = when {
        ms < 50 -> NovaAccent
        ms < 100 -> NovaWarning
        else -> NovaDanger
    }
    Text(
        text = "${ms} ms",
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun ServerCard(
    server: ServerNode,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (server.isSelected) NovaPrimary.copy(alpha = 0.12f) else NovaSurfaceHigh
    val borderColor = if (server.isSelected) NovaPrimary.copy(alpha = 0.5f) else NovaBorder

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = bg,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = server.flag, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = NovaText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProtocolBadge(server.protocol)
                    Spacer(Modifier.width(8.dp))
                    LatencyChip(server.latencyMs)
                }
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Избранное",
                    tint = if (server.isFavorite) NovaWarning else NovaTextMuted
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = NovaSurfaceHigh,
        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = NovaTextMuted)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = NovaText, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PulsingConnectButton(
    isConnected: Boolean,
    isConnecting: Boolean,
    onClick: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected || isConnecting) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = if (isConnected) 0.55f else 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val ringColor = when {
        isConnected -> NovaAccent
        isConnecting -> NovaPrimarySoft
        else -> NovaDisconnected
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(168.dp)
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(168.dp)
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ringColor.copy(alpha = glowAlpha), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        // Button
        Surface(
            onClick = onClick,
            modifier = Modifier.size(132.dp),
            shape = CircleShape,
            color = if (isConnected) NovaAccent.copy(alpha = 0.15f) else NovaSurfaceHigh,
            border = androidx.compose.foundation.BorderStroke(
                3.dp,
                brush = Brush.linearGradient(
                    listOf(ringColor, ringColor.copy(alpha = 0.4f))
                )
            ),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = NovaPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = if (isConnected) "ON" else "OFF",
                        style = MaterialTheme.typography.displayLarge,
                        color = if (isConnected) NovaAccent else NovaTextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = NovaText,
            modifier = Modifier.weight(1f)
        )
        action?.invoke()
    }
}
