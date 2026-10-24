package com.novavpn.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.novavpn.app.ui.components.SectionHeader
import com.novavpn.app.ui.components.ServerCard
import com.novavpn.app.ui.theme.*
import com.novavpn.app.viewmodel.MainViewModel

@Composable
fun ServersScreen(vm: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        SectionHeader(title = "Серверы (${vm.filteredServers.size})") {
            IconButton(onClick = { vm.toggleFavoritesFilter() }) {
                Icon(
                    Icons.Outlined.Star,
                    contentDescription = "Избранные",
                    tint = if (vm.showOnlyFavorites) NovaWarning else NovaTextMuted
                )
            }
        }

        OutlinedTextField(
            value = vm.searchQuery,
            onValueChange = { vm.updateSearch(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск по стране, протоколу…") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NovaPrimary,
                unfocusedBorderColor = NovaBorder,
                focusedContainerColor = NovaSurfaceHigh,
                unfocusedContainerColor = NovaSurfaceHigh
            )
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(vm.filteredServers, key = { it.id }) { server ->
                ServerCard(
                    server = server,
                    onClick = { vm.selectServer(server) },
                    onFavorite = { vm.toggleFavorite(server.id) }
                )
            }
        }
    }
}
