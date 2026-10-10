package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    CHART("Chart", Icons.Filled.Stars, Icons.Outlined.Stars),
    ASK("Ask", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    LIBRARY("Library", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun JyotishaBottomNavBar(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(CelestialBgElevated)
            .border(1.dp, CelestialBorder, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .testTag("bottom_nav_bar"),
        containerColor = CelestialBgElevated,
        tonalElevation = 0.dp
    ) {
        AppDestination.entries.forEach { destination ->
            val isSelected = currentDestination == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        fontSize = 11.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CelestialGold,
                    selectedTextColor = CelestialGold,
                    indicatorColor = CelestialGoldWash,
                    unselectedIconColor = CelestialTextMuted,
                    unselectedTextColor = CelestialTextMuted
                ),
                modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
            )
        }
    }
}
