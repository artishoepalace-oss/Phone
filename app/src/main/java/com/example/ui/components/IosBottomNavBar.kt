package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosDarkSurface

sealed class NavItem(val route: String, val label: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector, val tabIndex: Int) {
    object Favorites : NavItem("favorites", "Favorites", Icons.Filled.Star, Icons.Outlined.StarBorder, 0)
    object Recents : NavItem("recents", "Recents", Icons.Filled.AccessTime, Icons.Outlined.AccessTime, 1)
    object Contacts : NavItem("contacts", "Contacts", Icons.Filled.Person, Icons.Outlined.Person, 2)
    object Keypad : NavItem("keypad", "Keypad", Icons.Filled.Dialpad, Icons.Outlined.Dialpad, 3)
    object Voicemail : NavItem("voicemail", "Voicemail", Icons.Filled.Voicemail, Icons.Outlined.Voicemail, 4)
}

@Composable
fun IosBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem.Favorites,
        NavItem.Recents,
        NavItem.Contacts,
        NavItem.Keypad,
        NavItem.Voicemail
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(IosDarkSurface.copy(alpha = 0.95f))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = selectedTab == item.tabIndex
            val color by animateColorAsState(
                targetValue = if (isSelected) IosBlue else Color(0xFF8E8E93),
                label = "NavColor"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(item.tabIndex) }
                    .padding(4.dp)
                    .testTag("nav_tab_${item.route}")
            ) {
                Icon(
                    imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                    contentDescription = item.label,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.label,
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
